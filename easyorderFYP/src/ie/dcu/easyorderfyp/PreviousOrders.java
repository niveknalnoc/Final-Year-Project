package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.Utilities.URL_GET_USERS_ORDERS;

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

public class PreviousOrders extends ListActivity {
	
	// Progress Dialog
    private ProgressDialog pDialog;
	
    // Alert dialog manager
 	AlertDialogManager alert;
 	
 	//lv to display the order list
 	private ListView lv;
 	
 	ArrayAdapter<Order> listData;
 	
 	// Variables for Login Data
 	public static int user_id;
 	public static String username;
 	
 	private static String selectedDate;
 	
 	// JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_ID = "item_id";
    private static final String TAG_ITEM_NAME = "item_name";
    private static final String TAG_TABLE_NUM = "table_num";
    private static final String TAG_QUANTITY = "quantity";
    private static final String TAG_DATE = "date";
 	
 	// Current user details
 	User current_user;
 	
 	// Products JSONArray
    JSONArray items;
    
    // JSONObject json
    JSONObject json;
    
    // Creating JSON Parser object
    WebCallService webCall; 
    
    // ArrayList to hold the users orders
 	private ArrayList<Order> downloadedOrderList;
 	private ArrayList<String> orderDateList;
 	private ArrayList<Order> selectedOrderItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previous_orders);
		
		Bundle b = this.getIntent().getExtras();
		user_id = b.getInt("user_id");
		username = b.getString("username");
		
		webCall = new WebCallService();
    	alert = new AlertDialogManager();
    	downloadedOrderList = new ArrayList<Order>();
    	orderDateList = new ArrayList<String>();
    	
    	new LoadUsersOrders().execute();
    	
    	System.out.println("HELLO");
    	System.out.println("SIZE " + orderDateList.size());
    	
	}
	
	void displayData(){
		//lv = (ListView) findViewById(android.R.id.list);
		ListView lv = getListView();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, orderDateList);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedDate = (String) parent.getItemAtPosition(position);
				getTransForDate();
			}
		});
	}
	
	void getTransForDate(){
		selectedOrderItems = new ArrayList<Order>();
		for(int i = 0 ; i < downloadedOrderList.size() ; i++){
			if(downloadedOrderList.get(i).getDate().equalsIgnoreCase(selectedDate)){
				/*int table_num = downloadedOrderList.get(i).getTableNum();
				String item_id = downloadedOrderList.get(i).getItemId();
				String item_name = downloadedOrderList.get(i).getItemName();
				int item_quantity = downloadedOrderList.get(i).getQuantity();
				String date = downloadedOrderList.get(i).getDate();*/
				
				int table_num = 20;
				String item_id = "item id";
				String item_name = "item name";
				int item_quantity = 9;
				String date = "290413";
				
				selectedOrderItems.add(new Order(table_num,item_id,item_name,item_quantity,date));
			}
		}
		displayOrderData(selectedOrderItems);
	}
		
	void displayOrderData(ArrayList<Order> items){
		System.out.println(items.size());
		listData = new ArrayAdapter<Order>(this,
                android.R.layout.simple_list_item_1, items);
		setListAdapter(listData);
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
            pDialog.setMessage("Retrieving your previou orders...");
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
            params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
            
	        // getting JSON string from URL
	        json = new JSONObject();
            json = webCall.makeHttpRequest(URL_GET_USERS_ORDERS, params);
 
            // Check your log cat for JSON response
            Log.d("Get Users: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // items found
                    // getting Array of items
                    items = json.getJSONArray(TAG_USERS);
 
                    //downloadedUsersList.clear();
                    // looping through All items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
 
                        // storing each json item in variable
                        String itemId = c.getString(TAG_ID);
                        String itemName = c.getString(TAG_ITEM_NAME);
                        int tableNum = c.getInt(TAG_TABLE_NUM);
                        int quantity = c.getInt(TAG_QUANTITY);
                        String date = c.getString(TAG_DATE);
 
                        // storing menu items object in arraylist
                        downloadedOrderList.add(new Order(tableNum, itemId, itemName, quantity, date));
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            for(int i = 0 ; i < downloadedOrderList.size(); i++){
    			orderDateList.add(downloadedOrderList.get(i).getDate());
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
                	displayData();
                }
            });
        }	
    }
    
}
