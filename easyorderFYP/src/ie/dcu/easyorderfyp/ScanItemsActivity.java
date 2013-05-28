package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.RegisterActivity.isRegistered;
import static ie.dcu.easyorderfyp.ServerUtilities.URL_SUBMIT_ORDER;
import ie.dcu.easyorderfyp.QuantityDialog.QuanityChangeListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanItemsActivity extends FragmentActivity implements
		QuanityChangeListener {

	private Button btnScanItems;
	private Button btnSubmitButton;
	private ListView mOrderListView;
	private TextView tvTableNum;

	private int thisRequestCode;
	private int thisResultCode;
	private String codeContents;
	private int tableNumber;

	private boolean basketEmpty = true;

	// arraylist to hold the menu, itemsScaned and the order
	private List<MenuItem> menu;

	final Activity returnActivity = this;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	WebCallService webCall;

	private IntentResult activityResultIntent;

	// alert dialog manager
	AlertDialogManager alert;

	// Progress Dialog
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_item);

		webCall = new WebCallService();
		alert = new AlertDialogManager();
		menu = new ArrayList<MenuItem>();

		// retrieve the table number from the activity previous
		Bundle b = this.getIntent().getExtras();
		tableNumber = b.getInt("tableNumber");
		// retrieve the downloaded menu items from the previous activtity
		menu = getIntent().getParcelableArrayListExtra("downloadedMenuItems");

		// display table number at the top of the screen
		tvTableNum = (TextView) findViewById(R.id.txtTableNumber);
		if (tableNumber != 0)
			tvTableNum.setText("Order For Table: " + tableNumber);
		else
			tvTableNum.setText("Take Away Order");

		// order list containing scanned items
		mOrderListView = (ListView) findViewById(R.id.order_list);

		// Set up listview
		mOrderListView.setAdapter(new OrderListAdapter(this,
				R.layout.order_item_list));
		
		btnScanItems = (Button) findViewById(R.id.btnScanItem);

		// scan table code intent onClick(btnScan)
		btnScanItems.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching scan item Activity
				IntentIntegrator integrator = new IntentIntegrator(
						returnActivity);
				integrator.initiateScan();
			}
		});

		btnSubmitButton = (Button) findViewById(R.id.btnSubmit);

		// scan table code intent onClick(btnScan)
		btnSubmitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				submitOrderSplashScreen();
			}
		});
	}

	// splash screen pops up to notify the user that the
	// login details are not known
	private void submitOrderSplashScreen() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					submitOrder();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ScanItemsActivity.this);
		builder.setTitle("Submitting Order...");
		builder.setMessage("Are You Sure?");
		builder.setPositiveButton("Yes Please!", dialogClickListener);
		builder.setNegativeButton("Not yet", dialogClickListener).show();
	}// end userNotFoundSplashScreen

	@Override
	protected void onResume() {
		super.onResume();

		// parse qr code that was scanned
		if (thisRequestCode == 49374) {
			if (thisResultCode == -1) {
				codeContents = activityResultIntent.getContents();
				// parse the scanned code
				MenuItem item = parseCodeScanned(codeContents);
				// validate the input code scanned
				boolean isValid = validateInput(item);
				// an item is only added if the item is available on the
				// database
				// items from the database were retrieved from the previous
				// activity
				if (isValid) {
					if (menu.contains(item)) {
						// order matches the menu on the db, add to order
						// arraylist
						basketEmpty = false;
						addItemToOrder(item);
					} else {
						// menu does not contain this item, let customer know
						alert.showAlertDialog(this, "Invalid Item Code",
								"This Item Is Not Available Today!", false);
					}
				} else {
					alert.showAlertDialog(this, "Invalid Item Code",
							"This Item Is Not a Menu Item Code!", false);
				}
			}
		}
	}

	/**
	 * check that the input is a valid menu item
	 */
	private boolean validateInput(MenuItem item) {
		if (item.getItemIdentifier() == null || item.getItemName() == null
				|| item.getPrice() == 0.00)
			return false;
		else
			return true;
	}

	/**
	 * add to the order
	 */
	private void addItemToOrder(MenuItem item) {
		OrderListAdapter adapter = (OrderListAdapter) mOrderListView
				.getAdapter();
		adapter.addOrderItem(item);
	}

	/**
	 * Submit the order
	 */
	private void submitOrder() {
		if (basketEmpty == false) {
			new SubmitOrder().execute();
		} else {
			alert.showAlertDialog(ScanItemsActivity.this,
					"No items in basket	",
					"Please scan items to add to the order first!", false);
		}
	}

	/**
	 * Called when the order submit async has completed
	 */
	private void orderSubmitted() {
		// Bring user to next screen
		Intent orderSubmitted = new Intent(getApplicationContext(),
				OrderSubmitted.class);
		startActivity(orderSubmitted);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		thisRequestCode = requestCode;
		thisResultCode = resultCode;

	}

	/**
	 * Parse the scanned menu item
	 * 
	 * each ode has a string containing item id, name and price each of these
	 * pieces of data need to be parsed and assigned to a variable
	 */
	private MenuItem parseCodeScanned(String codeContents) {

		int startCount = 0;
		int endCount = 0;

		boolean idSet = false;
		boolean nameSet = false;
		boolean priceSet = false;

		String setId = null;
		String setItemName = null;
		Double setPrice = null;
		String temp;

		for (int i = 0; i < codeContents.length(); i++) {
			if (codeContents.charAt(i) == ':') {
				temp = codeContents.substring(startCount, endCount);
				endCount++;
				startCount = endCount;
				if (idSet != true) {
					setId = temp;
					idSet = true;
				} else if (nameSet != true) {
					setItemName = temp;
					nameSet = true;
				} else if (priceSet != true) {
					setPrice = Double.parseDouble(temp);
					priceSet = true;
				}
			} else {
				endCount++;
			}
		}
		if (!idSet || !nameSet || !priceSet)
			return new MenuItem(null, null, 0.00);
		else
			return new MenuItem(setId, setItemName, setPrice);
	}

	public void newQuantity(int index, int quan) {
		OrderListAdapter adapter = (OrderListAdapter) mOrderListView
				.getAdapter();
		adapter.updateQuantity(index, quan);
	}

	private class SubmitOrder extends AsyncTask<Void, Void, Void> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ScanItemsActivity.this);
			pDialog.setTitle("Submitting Order.. ");
			pDialog.setMessage("Sending order to the bar..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			OrderListAdapter adapter = (OrderListAdapter) mOrderListView
					.getAdapter();

			String orderString = "";
			try {
				orderString = adapter.getOrderString();
				System.out.println("orderString : " + orderString);
			} catch (Exception e) {
				alert.showAlertDialog(ScanItemsActivity.this,
						"No items in basket	",
						"Please scan items to add to the order first!", false);
				return null;
			}

			System.out.println("Integer.toString(user_id) : "
					+ Integer.toString(LoginActivity.user_id));
			// add the table number, username and order_string to the params
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("table", Integer
					.toString(tableNumber)));
			param.add(new BasicNameValuePair("username", LoginActivity.username));
			param.add(new BasicNameValuePair("order_string", orderString));

			System.out.println(LoginActivity.username);
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = webCall.makeHttpRequest(URL_SUBMIT_ORDER, param);

			// check log cat from response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				System.out.println("SUCCESS VALUE : " + success);

				if (success == 1) {
					// successfully submitted order
					orderSubmitted();
					// closing this screen
					finish();
				} else {
					// failed to create product
					Log.d("ORDER OK? ", "NOT SUBMITTED!!!");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (isRegistered == true) {
			GCMRegistrar.unregister(this);
			isRegistered = false;
		}
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(i);
	}
}