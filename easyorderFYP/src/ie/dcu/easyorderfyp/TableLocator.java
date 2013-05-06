package ie.dcu.easyorderfyp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
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
import android.widget.TextView;

import static ie.dcu.easyorderfyp.RegisterActivity.isRegistered;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static ie.dcu.easyorderfyp.ServerUtilities.URL_DOWNLOAD_MENU;

public class TableLocator extends Activity {
	
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // Internet detector
  	ConnectionDetector cd;
    
  	// Creating JSON Parser object
    WebCallService webCall; 
    
    // ArrayList to hold the downloaded menu items
    ArrayList<MenuItem> downloadedMenuItems;
    
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "items";
    private static final String TAG_ID = "item_id";
    private static final String TAG_ITEM_NAME = "item_name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_AVAILABLE = "available";
 
    // Products JSONArray
    JSONArray items = null;
	
	private Button btnScanTable;
	private int thisRequestCode;
	private int thisResultCode;
	private String codeContents;
	
	private TextView tvDineOption;
	
	// Variables for Login Data
	private static int user_id;
	private static String username;
	
	// no_iems_error_flag is set if no items found , return_flag is set to escape activity back to main menu
	private boolean no_items_error_flag = false;
	private boolean return_flag = false;
	
	final Activity returnActivity = this;
	
	private IntentResult activityResultIntent;
	
	// alert dialog manager
	AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eat_in);
		
		if(isRegistered == true){
			// display take-away at the top of the screen
			tvDineOption = (TextView) findViewById(R.id.txtDineOption);
			tvDineOption.setText("Take-Away Order");
		}else{
			// display eat in at the top of the screen
			tvDineOption = (TextView) findViewById(R.id.txtDineOption);
			tvDineOption.setText("Eat In Order");
		}
		
		webCall = new WebCallService();
		alert = new AlertDialogManager();
		cd = new ConnectionDetector(getApplicationContext());
		
		Bundle b = this.getIntent().getExtras();
		user_id = b.getInt("user_id");
		username = b.getString("username");
		
		System.out.println(user_id);

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(TableLocator.this,
					"Internet Connection Error",
					"Please connect to WiFi or 3g to use EasyOrder", false);
		}
		
		btnScanTable = (Button) findViewById(R.id.btnScan);
		
		// scan table code intent onClick(btnScan)
		btnScanTable.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
        	   // Launching scan item Activity
           	   IntentIntegrator integrator = new IntentIntegrator(returnActivity);
			   integrator.initiateScan();
           }
       });
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// If user hasn't pressed back button at the scan item screen
		if(return_flag == false) {
			// parse scanned code
			if (thisRequestCode == 49374) {
				if (thisResultCode == -1) { 
					codeContents = activityResultIntent.getContents();
					boolean isValidCodeScanned = validateCodeContents(codeContents);
					if(isValidCodeScanned){
						return_flag = true;
						downloadedMenuItems = new ArrayList<MenuItem>();
						// Loading products in Background Thread
						new LoadAllItems().execute();
					}
					else {
						// alert dialog - invalid code scanned
						alert.showAlertDialog(this,
								"Invalid Locator Code",
								"Please scan a valid Locator QR code located on your table!", false);
					}
				}
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent =
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		thisRequestCode = requestCode;
		thisResultCode = resultCode;
		
	}
	
	private boolean validateCodeContents(String codeContents) {
		boolean validator = false;
		String firstToken = codeContents.substring(0, 1);
		// check valid code scanned [char followed by an int]
		if(firstToken.equals("T") && codeContents.length() > 1) {
			try{
				int tableNumber = Integer.parseInt(codeContents.substring(1));
				if(tableNumber > 0)
				validator = true;
				else
					validator = false;
			}catch(NumberFormatException e){
			}
		}
		return validator;
	}
	
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllItems extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TableLocator.this);
            pDialog.setTitle("Setting Up The Menu! ");
            pDialog.setMessage("Loading Menu Items...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            
            // getting JSON string from URL
            JSONObject json = webCall.makeHttpRequest(URL_DOWNLOAD_MENU, params);
 
            // Check your log cat for JSON response
            Log.d("All Items: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // items found
                    // getting Array of items
                    items = json.getJSONArray(TAG_PRODUCTS);
 
                    downloadedMenuItems.clear();
                    // looping through All items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
 
                        // storing each json item in variable
                        String item_id = c.getString(TAG_ID);
                        String item_name = c.getString(TAG_ITEM_NAME);
                        Double item_price = c.getDouble(TAG_PRICE);
 
                        // storing menu items object in arraylist
                        downloadedMenuItems.add(new MenuItem(item_id,item_name,item_price));
                    }
                } else {
                    // no menu items found - set flag
                	no_items_error_flag = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                	if(no_items_error_flag == true) {
                		Intent i = new Intent(getApplicationContext(), EasyOrderERROR.class);
                		startActivity(i);
                	}else {
                		Intent i = new Intent(getApplicationContext(), ScanItemsActivity.class);
                		i.putExtra("tableNumber", codeContents.substring(1));
                		i.putExtra("user_id", user_id);
                		i.putParcelableArrayListExtra ("downloadedMenuItems", downloadedMenuItems);
                		startActivity(i);
                	}
                }
            });
 
        }	
    }
    
}