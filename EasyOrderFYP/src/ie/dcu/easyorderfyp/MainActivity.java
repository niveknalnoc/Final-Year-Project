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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {
	
	Button btnViewProducts;
	Button btnScanItem;
	
	String id_scanned;
	String name_scanned;
	String price_scanned;
	String available_scanned;
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    JSONParser jsonParser = new JSONParser();
    
 // url to create new product
    private static String url_submit_order = "http://192.168.1.12/easyorder/EASYORDER_SERVER_PHP/submit_order.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
	
	private IntentResult activityResultIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		btnScanItem = (Button) findViewById(R.id.btnScanItem);
		
		 // view products click event
        btnViewProducts.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching download all menu items Activity
                Intent i = new Intent(getApplicationContext(), DownloadMenuItems.class);
                startActivity(i);
 
            }
        });
        
        // view products click event
        btnScanItem.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching scan item Activity
            	IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
				integrator.initiateScan();
 
            }
        });
        
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent =
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (resultCode != RESULT_CANCELED) {
			String code = activityResultIntent.getContents();
			id_scanned = code.substring(0, 2);
			name_scanned = code.substring(3, 18);
			price_scanned = code.substring(19,24);
			available_scanned = code.substring(25,26);
		
			// submit order to server
			new CreateNewOrder().execute();

			// DEBUG - NOT REQUIRED IN LIVE APP
			System.out.println("TEST : --> .. --> :"+ id_scanned +" - " + name_scanned + " - " + price_scanned + " - " + available_scanned);
		}
	}
	
	/**
     * Background Async Task to Create new product
     * */
    class CreateNewOrder extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Submitting Order.. ");
            pDialog.setMessage("Sending order to the bar..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
           
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id_scanned));
            params.add(new BasicNameValuePair("item_name", name_scanned));
            params.add(new BasicNameValuePair("price", price_scanned));
            params.add(new BasicNameValuePair("available", available_scanned));
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_submit_order,
                    "POST", params);
 
            // check log cat fro response
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }

}
