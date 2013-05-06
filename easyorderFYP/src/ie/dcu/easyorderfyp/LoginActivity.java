package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.ServerUtilities.URL_GET_USER;
import static ie.dcu.easyorderfyp.ServerUtilities.URL_REGISTER_USER;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	// Buttons for logging in and registering
	private Button btnLogin;
	private Button btnRegister;
	
	// Variables for Login Data
	static String username;
	static String password;
	private static int user_id;
	private static boolean isLoggedIn;
	
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
    		alert.showAlertDialog(LoginActivity.this,
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
		    			this_user = new User(00, username, password);
		    			// Loading users in Background Thread
		    			// required to check if user is already registered
		    			BUTTONPRESSED = LOGIN;
		    			Log.d("LOGIN","LOGIN");
		    			new LoadAllUsers().execute();
		    			
		    		}else{
		    			alert.showAlertDialog(LoginActivity.this, "Registration Error!", "Please enter your details", false);
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
		    			this_user = new User(00, username, password);
		    			// Loading users in Background Thread
		    			// required to check if user is already registered
		    			BUTTONPRESSED = REGISTER;
		    			Log.d("REGISTER","REGISTER");
		    			new LoadAllUsers().execute();
		    		}else{
		    			alert.showAlertDialog(LoginActivity.this, "Registration Error!", "Please enter your details", false);
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
            pDialog = new ProgressDialog(LoginActivity.this);
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
                        this_user.setUserId(id);
            			this_user.setUserName(username);
            			this_user.setPassword(password);
                        user_found = true;
                        Log.d("LOADALLUSERS","LOADALLUSERS");
                    }
                    
                }else{
                	user_found = false;
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

                    if(user_found == true){
                    	//login
                    	logUserIn();
                    }else{
                    	if(BUTTONPRESSED.equals(LOGIN)){
                    		userNotFoundSplashScreen();
                    	}
                    	else{
                    		//register
                        	registerUser();
                    	}
                    }
                }
            });
        }	
    }
    
	private void userNotFoundSplashScreen() {

		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					registerUser();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}

		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setTitle(this_user.getUserName());
		builder.setMessage("User not found... What would you like to do?");
		builder.setPositiveButton("Register", dialogClickListener);
		builder.setNegativeButton("Enter Details Again", dialogClickListener).show();
	}// end userNotFoundSplashScreen
    
    void registerUser() {

    	Toast.makeText(getApplicationContext(), username + 
    			" you are a new user! Welcome - creating your login details now...", 
           			Toast.LENGTH_LONG).show();
    	new CreateNewUser().execute();
    	
    }
    
    void logUserIn() {

    	setSharedPref(this_user, isLoggedIn);
    	
    	Toast.makeText(getApplicationContext(), 
				" Hi "+ this_user.getUserName() +" logging you in now..." , 
        		Toast.LENGTH_LONG).show();
    	
    	Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
		i.putExtra("this_user", this_user);
		startActivity(i);
    	
    }
    
    private void setSharedPref(User this_user, boolean isLoggedIn) {
    	
    	//save the login data
        SharedPreferences preferences = getSharedPreferences("FileName",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        username = this_user.getUserName();
        password = this_user.getPassword();
        user_id = this_user.getUserId();
        isLoggedIn = true;
        
        // save the user login data to shared preferences
        editor.putString("savedUsername", username);
        editor.putString("savedPassword", password);
        editor.putInt("savedUserId", user_id);
        editor.putBoolean("isLoggedIn", isLoggedIn);
 
        // commit the edits
        editor.commit();
    }
    
    private void getSharedPref() {
    	
    	SharedPreferences preferences = getSharedPreferences("FileName",MODE_PRIVATE);
		
        username = preferences.getString("savedUsername", "username not set");
        password = preferences.getString("savedPassword", "password not set");
        user_id = preferences.getInt("savedUserId", 00);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        
        this_user.setUserId(user_id);
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
            pDialog = new ProgressDialog(LoginActivity.this);
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