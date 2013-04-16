package ie.dcu.easyorderfyp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanItemsActivity extends Activity {
	
	private Button btnScanItems;
	private Button btnSubmitButton;
	private ListView mOrderListView;
	private TextView tvTableNum;
	
	private int thisRequestCode;
	private int thisResultCode;
	private String codeContents;
	private String tableNumber;

	// arraylist to hold the menu, itemsScaned and the order
	ArrayList<MenuItem> menu;
	ArrayList<MenuItem> scannedItems;
	ArrayList<Order> order;
	
	final Activity returnActivity = this;
	
	// JSON Node names
    private static final String TAG_SUCCESS = "success";
    
    JSONParser jsonParser = new JSONParser();
    
    // url to create new product
    private static String url_submit_order = "http://192.168.1.12/easyorder_server/submit_order.php";
	
	private IntentResult activityResultIntent;
	
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Progress Dialog
    private ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_item);
		
		Bundle b = this.getIntent().getExtras();
		tableNumber = b.getString("tableNumber");
		menu = new ArrayList<MenuItem>();
		scannedItems = new ArrayList<MenuItem>();
		menu = getIntent().getParcelableArrayListExtra ("downloadedMenuItems");
		
		// display table number at the top of the screen
		tvTableNum = (TextView) findViewById(R.id.txtTableNumber);
		tvTableNum.setText("Order For Table: " + tableNumber);
		
		// order list containing scanned items
        mOrderListView = (ListView) findViewById(R.id.order_list);
        
        //Set up listview
        mOrderListView.setAdapter(new OrderListAdapter(
        		this,
        		R.layout.order_item_list
        ));
        
        final Activity returnActivity = this;
        
        btnScanItems = (Button) findViewById(R.id.btnScanItem);
		// scan table code intent onClick(btnScan)
		btnScanItems.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
        	   // Launching scan item Activity
           	   IntentIntegrator integrator = new IntentIntegrator(returnActivity);
			   integrator.initiateScan();
           }
       });
		
		 btnSubmitButton = (Button) findViewById(R.id.btnSubmit);
		 	// scan table code intent onClick(btnScan)
		 btnSubmitButton.setOnClickListener(new View.OnClickListener() {

		    @Override
		    public void onClick(View view) {
		    	// **** NEED A BUTTON FOR SUBMITTING THE ORDER ****
				// AFTER ORDER SUBMITTED CLEAR THE MENUITEMSSCANNED ARRAYLIST
				// DISPLAY ORDER SUBMITTED SCREEN - BACK BUTTON BRINGS TO MAIN MENU
		    	submitOrder();
		    }
	   });
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// parse scanned code
		if (thisRequestCode == 49374) {
            if (thisResultCode == -1) { 
	          	codeContents = activityResultIntent.getContents();
	         	MenuItem item = parseCodeScanned(codeContents);
	         	if(menu.contains(item)) {
	         		// order matches the menu on the db, add to order arraylist
	         		scannedItems.add(item);
	         		OrderListAdapter adapter = (OrderListAdapter) mOrderListView.getAdapter();
					adapter.addOrderItem(item);
	         	}else{
	         		//menu does not contain this item, let customer know
	         		alert.showAlertDialog(this,
            				"Invalid Item Code",
            				"This Item Is Not Available Today!", false);
	         	}
	         	
            }
		}
		
	}
	
	/**
	 * Submit the order
	 */
	private void submitOrder() {
		if(scannedItems.size() != 0) {
			new SubmitOrder().execute();
		}else{
			alert.showAlertDialog(ScanItemsActivity.this,
					"No items in basket	",
					"Please scan items to add to the order first!", false);
		}
	}
	
	/**
	 * Called when the order submit async has completed
	 */
	private void orderSubmitted() {
		//Bring user to next screen
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent =
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		thisRequestCode = requestCode;
		thisResultCode = resultCode;
		
	}
	
	private MenuItem parseCodeScanned(String codeContents) {
		
		int startCount = 0;
		int endCount = 0;
		
		boolean idSet = false;
		boolean nameSet = false;
		boolean priceSet = false;
		
		String setId = null;
		String setItemName = null;
		Double setPrice = null;
		int setAvailable = 3;
		String temp;
		
		for(int i = 0 ; i < codeContents.length() ; i++) {
			if(codeContents.charAt(i) == ':'){
				temp = codeContents.substring(startCount, endCount);
				endCount++;
				startCount = endCount;
				if(idSet != true) {
					setId = temp;
					idSet = true;
				}else if(nameSet != true) {
					setItemName = temp;
					nameSet = true;
				}else if(priceSet != true) {
					setPrice = Double.parseDouble(temp);
					setAvailable = Integer.parseInt(codeContents.substring(startCount));
					priceSet = true;
				}
			}else{
				endCount++;
			}
		}
		
		return new MenuItem(setId, setItemName, setPrice, setAvailable);
	}
	
	public void newQuantity(int index, int quan) {
		OrderListAdapter adapter = (OrderListAdapter) mOrderListView.getAdapter();
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
			
			OrderListAdapter adapter = (OrderListAdapter) mOrderListView.getAdapter();		
			
			String orderString = "";
			try {
				orderString = adapter.getOrderString();
				System.out.println(orderString);
			} catch(Exception e) {
				alert.showAlertDialog(ScanItemsActivity.this,
						"No items in basket	",
						"Please scan items to add to the order first!", false);
				return null;
			}
			
			List<NameValuePair> param = new ArrayList<NameValuePair>();
	        param.add(new BasicNameValuePair("table", tableNumber));
	        param.add(new BasicNameValuePair("order_string", orderString));

	     	// getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_submit_order, param);
 
            // check log cat from response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully submitted order
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
 
                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                	Log.d("NOT SUBMITTED!!!", "");
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
			//orderSubmitted();
		}
		
	}

}