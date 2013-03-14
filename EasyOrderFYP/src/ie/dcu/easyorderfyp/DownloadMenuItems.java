package ie.dcu.easyorderfyp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
public class DownloadMenuItems extends ListActivity {
 
	// Object MenuItem
    MenuItem m;
	
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> itemsList;
    ArrayList<MenuItem> downloadedMenuItems;
 
    // url to get all products list
    private static String url_all_items = "http://192.168.1.12/easyorder/EASYORDER_SERVER_PHP/get_items.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "items";
    private static final String TAG_ID = "id";
    private static final String TAG_ITEM_NAME = "item_name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_AVAILABLE = "available";
 
    // products JSONArray
    JSONArray items = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_items);
 
        // Hashmap for ListView
        itemsList = new ArrayList<HashMap<String, String>>();
        downloadedMenuItems = new ArrayList<MenuItem>();
        
        // Loading products in Background Thread
        new LoadAllItems().execute();
 
        // Get listview
        ListView lv = getListView();
 
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
            pDialog = new ProgressDialog(DownloadMenuItems.this);
            pDialog.setMessage("Loading Menu Items. Please wait...");
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
            JSONObject json = jParser.makeHttpRequest(url_all_items, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Items: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    items = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Products
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_ITEM_NAME);
                        Double price = c.getDouble(TAG_PRICE);
                        int available = c.getInt(TAG_AVAILABLE);
 
                        // ******* NOT REQUIRED IN LIVE APP - DEBUGGING FEATURE *******
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // ******* NOT REQUIRED IN LIVE APP - DEBUGGING FEATURE *******
                        // set each items data to m -> MenuItem object
                        //m.setId(id);
                        //m.setItemName(name);
                        //m.setPrice(price);
                        //m.setAvailable(available);
                        
                        // add the items data to the MenuItem object and store in ArrayList<MenuItem> downloadedMenuItems
                        //downloadedMenuItems.add(m);
                        downloadedMenuItems.clear();
                        downloadedMenuItems.add(new MenuItem(id,name,price,available));
                        
                        // ******* NOT REQUIRED IN LIVE APP - DEBUGGING FEATURE *******
                        for(int j = 0 ; j < downloadedMenuItems.size() ; j++) {
                        	System.out.println("TEST : --> .. --> " + downloadedMenuItems.get(j).getAvailable());
                        }
                        
                        
                        // ******* NOT REQUIRED IN LIVE APP - DEBUGGING FEATURE *******
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_ITEM_NAME, name);
 
                        // adding HashList to ArrayList
                        itemsList.add(map);
                        
                    }
                } else {
                    // no menu items found
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            DownloadMenuItems.this, itemsList,
                            R.layout.list_items, new String[] { TAG_ID,
                                    TAG_ITEM_NAME},
                            new int[] { R.id.id, R.id.item_name});
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}
