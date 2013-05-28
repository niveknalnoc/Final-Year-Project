package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.ServerUtilities.URL_GET_USERS_ORDERS;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PreviousOrders extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Alert dialog manager
	AlertDialogManager alert;

	// lv to display the order list
	private ListView lv;
	private TextView tv;

	// boolean to store is the order is found or not
	private boolean ordersFound = false;

	// custom adapter to display the orders
	private CustomAdapter orderAdapter;
	private ArrayAdapter<String> stringAdapter;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERS = "users";
	private static final String TAG_ID = "item_id";
	private static final String TAG_ITEM_NAME = "item_name";
	private static final String TAG_TABLE_NUM = "table_num";
	private static final String TAG_QUANTITY = "quantity";
	private static final String TAG_PRICE = "price";
	private static final String TAG_DATE = "date";

	// Items JSONArray
	JSONArray items;

	// JSONObject json
	JSONObject json;

	// Creating JSON Parser object
	WebCallService webCall;

	// ArrayList to hold the users orders
	private List<Order> downloadedOrderList;
	private List<String> orderDateList;
	private List<Order> selectedOrderItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previous_orders);

		webCall = new WebCallService();
		alert = new AlertDialogManager();

		// async task to load the users orders
		new LoadUsersOrders().execute();

	}

	// method used in displaying the data to the user in a listview
	void displayData() {
		lv = getListView();
		stringAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, orderDateList);
		lv.setAdapter(stringAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedDate = (String) parent
						.getItemAtPosition(position);
				getTransForDate(selectedDate);
			}
		});
	}

	// method to get the transactions for the user
	void getTransForDate(String selectedDate) {
		selectedOrderItems = new ArrayList<Order>();
		// if the date selected matches the dates in the orders list then
		// we want the transaction items for this date
		for (int i = 0; i < downloadedOrderList.size(); i++) {
			if (downloadedOrderList.get(i).getDate()
					.equalsIgnoreCase(selectedDate)) {
				// we dont want duplicate so only add if not already added
				if (!selectedOrderItems.contains(selectedDate)) {
					int table_num = downloadedOrderList.get(i).getTableNum();
					String item_id = downloadedOrderList.get(i).getItemId();
					String item_name = downloadedOrderList.get(i).getItemName();
					int item_quantity = downloadedOrderList.get(i)
							.getQuantity();
					String date = downloadedOrderList.get(i).getDate();
					Double item_price = downloadedOrderList.get(i).getPrice();
					System.out.println(item_price);
					selectedOrderItems.add(new Order(table_num, item_id,
							item_name, item_quantity, item_price, date));
				}
			}
		}
		displayOrderData(selectedOrderItems);
	}

	void displayOrderData(List<Order> items) {

		orderAdapter = new CustomAdapter(this,
				R.layout.display_prev_order_data, items);
		setListAdapter(orderAdapter);

		tv = (TextView) findViewById(R.id.tv);
		tv.setText(LoginActivity.username + " your items ordered were: ");

		lv.setAdapter(orderAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadUsersOrders extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PreviousOrders.this);
			pDialog.setTitle("Connecting to Server! ");
			pDialog.setMessage("Retrieving your previous orders...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All users orders from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username",
					LoginActivity.username));

			// getting JSON string from URL
			json = new JSONObject();
			json = webCall.makeHttpRequest(URL_GET_USERS_ORDERS, params);

			// Check your log cat for JSON response
			Log.d("Get prev items: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// items found
					// getting Array of items
					items = json.getJSONArray(TAG_USERS);
					ordersFound = true;
					downloadedOrderList = new ArrayList<Order>();
					// looping through All items
					for (int i = 0; i < items.length(); i++) {
						JSONObject c = items.getJSONObject(i);

						// storing each json item in variable
						String itemId = c.getString(TAG_ID);
						String itemName = c.getString(TAG_ITEM_NAME);
						int tableNum = c.getInt(TAG_TABLE_NUM);
						int quantity = c.getInt(TAG_QUANTITY);
						double itemPrice = c.getDouble(TAG_PRICE);
						String date = c.getString(TAG_DATE);

						// get the date in yy/mm/dd format rather than
						// yy/mm/dd hh:mm:ss
						String stripDate = date.substring(0, 10);

						// storing menu items object in arraylist
						downloadedOrderList.add(new Order(tableNum, itemId,
								itemName, quantity, itemPrice, stripDate));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// if there are orders available get the dates and add to the order
			// date list
			if (ordersFound == true) {
				orderDateList = new ArrayList<String>();
				for (int i = 0; i < downloadedOrderList.size(); i++) {
					if (!orderDateList.contains(downloadedOrderList.get(i)
							.getDate())) {
						orderDateList.add(downloadedOrderList.get(i).getDate());
					}
				}
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String s) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					if (ordersFound == true) {
						displayData();
					} else {
						Toast.makeText(
								getApplicationContext(),
								LoginActivity.username
										+ " you have no previous orders!",
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

}
