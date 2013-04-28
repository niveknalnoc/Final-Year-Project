package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.Utilities.URL_GET_USER;
import static ie.dcu.easyorderfyp.Utilities.URL_REGISTER_USER;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// Buttons for logging in and registering
	private Button btnLogin;
	private Button btnRegister;
	
	// Variables for Login Data
	public static String username;
	public static String password;
	public static int id;
	public static boolean isLoggedIn;
	
	// EditText
	private EditText txtUsername;
	private EditText txtPassword;
	
	// User this_user
	private static User this_user;
	
	// Boolean for no users found - error flag
	private static boolean user_found = false;
	
	// Boolean for fatal error - cannot create user
	private static boolean user_created = false;
	
	// Boolean for no users returned from http post request
	//private boolean no_users_located = false;
	
	private static String BUTTONPRESSED;
	
	private static final String REGISTER = "register";
	private static final String LOGIN = "login";
	
	
	// ArrayList to hold the users downloaded
	ArrayList<User> downloadedUsersList;

	// JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";
    
    // Products JSONArray
    JSONArray items;
    
    // JSONObject json
    JSONObject json;
    
    // Creating JSON Parser object
    WebCallService webCall; 
    
    // Progress Dialog
    private ProgressDialog pDialog;
	
    // Alert dialog manager
 	AlertDialogManager alert;
 	
    // Internet detector
 	ConnectionDetector cd;
 	
 	// Return activity 
 	final Activity returnActivity = this;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        webCall = new WebCallService();
    	alert = new AlertDialogManager();
    	cd = new ConnectionDetector(getApplicationContext());
    	this_user = new User();
    		
    	getSharedPref();
    	
    	if(isLoggedIn == true){
    		Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
    		i.putExtra("this_user", this_user);
    		startActivity(i);
    	}
    	
    	// Check if Internet present
    	if (!cd.isConnectingToInternet()) {
    		// Internet Connection is not present
    		alert.showAlertDialog(MainActivity.this,
    				"Internet Connection Error",
    				"Please connect to WiFi or 3g to use EasyOrder", false);
    	}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_login);
		
		getSharedPref();
    	
    	if(isLoggedIn == true){
    		Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
    		i.putExtra("this_user", this_user);
    		startActivity(i);
    	}
		
		txtUsername = (EditText) findViewById(R.id.editTextUsername);
		txtPassword = (EditText) findViewById(R.id.editTextPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
				
		   	// Login button selected
		    btnLogin.setOnClickListener(new View.OnClickListener() {
		 
		    	@Override
		    	public void onClick(View view) {
		    		
		    		// Check if user filled the form
		    		username = txtUsername.getText().toString().toLowerCase();
		    		password = txtPassword.getText().toString().toLowerCase();

		    		if(username.length() > 0 && password.length() > 0){
		    			// call to server to identify if user is a registered user
		    			downloadedUsersList = new ArrayList<User>();
		    			this_user = new User(00, username, password);
		    			// Loading users in Background Thread
		    			// required to check if user is already registered
		    			BUTTONPRESSED = LOGIN;
		    			Log.d("LOGIN","LOGIN");
		    			new LoadAllUsers().execute();
		    			
		    		}else{
		    			alert.showAlertDialog(MainActivity.this, "Registration Error!", "Please enter your details", false);
		    		}
		    	}
		    });
			
		    // Register button selected
		    btnRegister.setOnClickListener(new View.OnClickListener() {
		 
		    	@Override
		    	public void onClick(View view) {
		    		
		    		// Check if user filled the form
		    		username = txtUsername.getText().toString().toLowerCase();
		    		password = txtPassword.getText().toString().toLowerCase();

		    		if(username.length() > 0 && password.length() > 0){
		    			// call to server to identify if user is a registered user
		    			downloadedUsersList = new ArrayList<User>();
		    			this_user = new User(00, username, password);
		    			// Loading users in Background Thread
		    			// required to check if user is already registered
		    			BUTTONPRESSED = REGISTER;
		    			Log.d("REGISTER","REGISTER");
		    			new LoadAllUsers().execute();
		    		}else{
		    			alert.showAlertDialog(MainActivity.this, "Registration Error!", "Please enter your details", false);
		    		}
		    	}
		    });
		}
	
	/**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllUsers extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Connecting to Server! ");
            pDialog.setMessage("Checking Username and Password...");
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
            params.add(new BasicNameValuePair("username", username));
	        params.add(new BasicNameValuePair("password", password));
            
	        // getting JSON string from URL
	        json = new JSONObject();
            json = webCall.makeHttpRequest(URL_GET_USER, params);
 
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
                        int id = c.getInt(TAG_ID);
                        String username = c.getString(TAG_USERNAME);
                        String password = c.getString(TAG_PASSWORD);
 
                        // storing menu items object in arraylist
                        downloadedUsersList.clear();
                        downloadedUsersList.add(new User(id, username, password));
                        Log.d("LOADALLUSERS","LOADALLUSERS");
                    }
                    
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            /*if(no_users_located ==  true){
            	Log.d("LOADALLUSERS","REGISTER USER");
            	registerUser(downloadedUsersList);
            	BUTTONPRESSED = REGISTER;
            }else{
            	Log.d("LOADALLUSERS","LOGIN USER");
            	loginUser(downloadedUsersList);
            	BUTTONPRESSED = LOGIN;
            }*/
 
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

                	user_found = searchForUser(downloadedUsersList);
                    
                    if(user_found == true){
                    	//login
                    	logUserIn();
                    }else{
                    	//register
                    	registerUser();
                    }
                	
                	
                	/*if(BUTTONPRESSED.equals(REGISTER)){
                		Log.d("LOADALLUSERS","REGISTER BUTTON onPostExecute");
                		if(user_found == true){
                			Log.d("LOADALLUSERS","user_found = true");
                			Toast.makeText(getApplicationContext(), username + 
                			" you are already registered, please login", 
                				Toast.LENGTH_LONG).show();
                    			
                		}else if(user_found == false){
                			Log.d("LOADALLUSERS"," user_found = false");
                			Toast.makeText(getApplicationContext(), username + 
                			" you are a new user! Welcome - creating your login details now...", 
                       			Toast.LENGTH_LONG).show();

                			BUTTONPRESSED = LOGIN;
                			new CreateNewUser().execute();
                		}
                	}else if(BUTTONPRESSED.equals(LOGIN)){
                		Log.d("LOADALLUSERS","LOGIN BUTTON onPostExecute");
                		if(user_found == true && isLoggedIn == true){
                			Log.d("LOADALLUSERS"," user_found = TRUE");
                			Toast.makeText(getApplicationContext(), 
                				" Welcome back "+ username +" logging you in now..." , 
                        		Toast.LENGTH_LONG).show();

                			Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
                			i.putExtra("this_user", this_user);
                			startActivity(i);
                        		
                    	}else if(user_found == false && isLoggedIn == false){
                    		Log.d("LOADALLUSERS"," user_found = false");
                    		Toast.makeText(getApplicationContext(), username + 
                    			" not found, please register to continue", 
                        		Toast.LENGTH_LONG).show();
                    	}
                    }*/
                }
            });
 
        }	
    }
    
    boolean searchForUser(ArrayList<User> downloadedUsersList){
    	
    	String name = this_user.getUserName();
		String pword = this_user.getPassword();
		boolean match = false;
		
    	for(int i = 0 ; i < downloadedUsersList.size() ; i++){

    		if(downloadedUsersList.get(i).getUserName().equalsIgnoreCase(name) && downloadedUsersList.get(i).getPassword().equalsIgnoreCase(pword)){
    			this_user.setId(downloadedUsersList.get(i).getItemId());
    			this_user.setUserName(downloadedUsersList.get(i).getUserName());
    			this_user.setPassword(downloadedUsersList.get(i).getPassword());
    			match = true;
   			}
    	}
    	
    	if(match == true){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
    
    void registerUser() {

    	Toast.makeText(getApplicationContext(), username + 
    			" you are a new user! Welcome - creating your login details now...", 
           			Toast.LENGTH_LONG).show();
    	new CreateNewUser().execute();
    	
    	/*String name = this_user.getUserName();
		String pword = this_user.getPassword();
		
    	for(int i = 0 ; i < downloadedUsersList.size() ; i++){

    		if(downloadedUsersList.get(i).getUserName().equalsIgnoreCase(name) && downloadedUsersList.get(i).getPassword().equalsIgnoreCase(pword)){
    			user_found = true;
    			Log.d("REGISTERUSER"," user_found = true");
   			}
    	}
    	if(!user_found == true)
    		user_found = false;
    	Log.d("REGISTERUSER"," user_found = false");*/
    	
    }
    
    void logUserIn() {

    	setSharedPref(this_user, isLoggedIn);
    	
    	Toast.makeText(getApplicationContext(), 
				" Welcome back "+ this_user.getUserName() +" logging you in now..." , 
        		Toast.LENGTH_LONG).show();
    	
    	Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
		i.putExtra("this_user", this_user);
		startActivity(i);
    	
    	/*String name = this_user.getUserName();
		String pword = this_user.getPassword();
		
    	for(int i = 0 ; i < downloadedUsersList.size() ; i++){
    		if(downloadedUsersList.get(i).getUserName().equalsIgnoreCase(name) && downloadedUsersList.get(i).getPassword().equalsIgnoreCase(pword)){
    			// Match found - user already registered so set the user_id now for this_user!
    			user_found = true;
    			this_user = downloadedUsersList.get(i);
    			Log.d("LOGINUSER","user_found = true");
    		}
    	}
    	
    	if(user_found == true){
    		isLoggedIn = true;
    		setSharedPref(this_user, isLoggedIn);
    	}else if(user_found == false){
    		isLoggedIn = false;
    	}*/
    	
    }
    
    private void setSharedPref(User this_user, boolean isLoggedIn) {
    	
    	//save the login data
        SharedPreferences preferences = getSharedPreferences("FileName",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        username = this_user.getUserName();
        password = this_user.getPassword();
        id = this_user.getItemId();
        isLoggedIn = true;
        
        // save the user login data to shared preferences
        editor.putString("savedUsername", username);
        editor.putString("savedPassword", password);
        editor.putInt("savedId", id);
        editor.putBoolean("isLoggedIn", isLoggedIn);
 
        // commit the edits
        editor.commit();
    }
    
    private void getSharedPref() {
    	
    	SharedPreferences preferences = getSharedPreferences("FileName",MODE_PRIVATE);
		
        username = preferences.getString("savedUsername", "username not set");
        password = preferences.getString("savedPassword", "password not set");
        id = preferences.getInt("savedId", 00);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        
        this_user.setId(id);
        this_user.setPassword(password);
        this_user.setUserName(username);
        
    }
    
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Registering New user! ");
            pDialog.setMessage("Creating Username and Password...");
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
	        params.add(new BasicNameValuePair("username", username));
	        params.add(new BasicNameValuePair("password", password));

	     	// getting JSON Object
	        json = new JSONObject();
            json = webCall.makeHttpRequest(URL_REGISTER_USER, params);
 
            // check log cat from response
            Log.d("Create User", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created new user
                    // closing this screen
                	user_created = true;
                    finish();
                } else {
                    // failed to create product
                	Log.d("Not Registered!!!", "");
                	user_created = false;
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
                	if(user_created == true) {
                		Log.d("CREATEUSER","USER CREATED");
                		// user registered successfully
                		Toast.makeText(getApplicationContext(), username + " has been registered", Toast.LENGTH_LONG).show();
                		// call LoadAllUsers to get the users id and to confirm user is fully created
                		new LoadAllUsers().execute();
                	}else if(user_created == false) {
                		Intent i = new Intent(getApplicationContext(), EasyOrderERROR.class);
                		startActivity(i);
                	}
                }
            });
 
        }	
    }
    
}
