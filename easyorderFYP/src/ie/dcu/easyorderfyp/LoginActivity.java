package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.ServerUtilities.URL_GET_USER;
import static ie.dcu.easyorderfyp.ServerUtilities.URL_REGISTER_USER;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	public static String username;
	public static String password;
	public static int user_id;
	private static boolean isLoggedIn;

	// EditText
	private EditText txtUsername;
	private EditText txtPassword;

	// Boolean for no users found - error flag
	private static boolean user_found;

	// Boolean for fatal error - cannot create user
	private static boolean user_created;

	// variable to hold what button was pressed
	private static String BUTTONPRESSED;

	// buttons that can be pressed - used with BUTTONPRESSED
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

		// retrieve any saved user login details
		getSharedPref();

		// if the user is logged in then continue to main menu
		if (isLoggedIn == true) {
			Intent i = new Intent(getApplicationContext(),
					LoggedInActivity.class);
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

		user_found = false;
		user_created = false;
		BUTTONPRESSED = null;

		// retrieve any stored login details for the user
		getSharedPref();

		// if the user is logged in then continue to the main menu
		if (isLoggedIn == true) {
			Intent i = new Intent(getApplicationContext(),
					LoggedInActivity.class);
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
				username = txtUsername.getText().toString();
				String password_plain = txtPassword.getText().toString()
						.toLowerCase();

				if (username.length() > 0 && password_plain.length() > 0) {
					// hash the password
					password = hash(password_plain);

					BUTTONPRESSED = LOGIN;
					Log.d("LOGIN", "LOGIN");
					// call to server to identify if user is a registered user
					// Loading users in Background Thread
					// required to check if user is already registered
					new LoadUser().execute();

				} else {
					alert.showAlertDialog(LoginActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});

		// Register button selected
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				// Check if user filled the form
				username = txtUsername.getText().toString();
				String password_plain = txtPassword.getText().toString();

				if (username.length() > 0 && password_plain.length() > 0) {
					password = hash(password_plain);

					BUTTONPRESSED = REGISTER;
					Log.d("REGISTER", "REGISTER");
					// call to server to identify if user is a registered user
					// Loading users in Background Thread
					// required to check if user is already registered
					new LoadUser().execute();
				} else {
					alert.showAlertDialog(LoginActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadUser extends AsyncTask<String, String, String> {

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

					// downloadedUsersList.clear();
					// looping through All items
					for (int i = 0; i < items.length(); i++) {
						JSONObject c = items.getJSONObject(i);

						// storing each json item in variable
						user_id = c.getInt(TAG_ID);
						username = c.getString(TAG_USERNAME);
						password = c.getString(TAG_PASSWORD);

						// storing menu items object in arraylist
						user_found = true;
						Log.d("LOADALLUSERS", "LOADALLUSERS");
					}

				} else {
					// user is not found
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

					if (user_found == true && BUTTONPRESSED.equals(LOGIN)) {
						// login
						logUserIn();
					} else {
						if (BUTTONPRESSED.equals(LOGIN)) {
							userNotFoundSplashScreen();
						} else {
							// register
							registerUser();
						}
					}
				}
			});
		}
	}

	// splash screen pops up to notify the user that the
	// login details are not known
	private void userNotFoundSplashScreen() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

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

		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);
		builder.setTitle(username);
		builder.setMessage("User not found... What would you like to do?");
		builder.setPositiveButton("Register", dialogClickListener);
		builder.setNegativeButton("Enter Details Again", dialogClickListener)
				.show();
	}// end userNotFoundSplashScreen

	void registerUser() {

		if (user_created == true) {
			Toast.makeText(
					getApplicationContext(),
					username
							+ " you are a new user! Welcome - creating your login details now...",
					Toast.LENGTH_SHORT).show();
		}

		// async task to create a new user in the database
		new CreateNewUser().execute();
	}

	// hash users password
	private String hash(String pw) {

		String h_pw = null;

		// Byte array containing the key s
		byte byteData[] = pw.getBytes();
		// SHA-256 digest created from key s (1024 bit -> 256 bit)
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(byteData);
			// Create the byte arrays to hold the key and plaintext
			byte byteDataHash[] = md.digest();
			// convert the byte to hex format
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < byteDataHash.length; i++) {
				sb.append(Integer
						.toString((byteDataHash[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			h_pw = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return h_pw;
	}

	// method to log the user in
	void logUserIn() {

		// set the login details for the user
		// doing this means next time the app is used
		// the login details will not have to be entered again
		// untill the user logs out and these valuse are removed
		setSharedPref(username, password, user_id, isLoggedIn);

		Toast.makeText(getApplicationContext(),
				" Hi " + username + " logging you in now...", Toast.LENGTH_SHORT)
				.show();

		Intent i = new Intent(getApplicationContext(), LoggedInActivity.class);
		startActivity(i);

	}

	// store the login details in shared preferences or the next login
	private void setSharedPref(String username, String password, int user_id,
			boolean isLoggedIn) {

		// save the login data
		SharedPreferences preferences = getSharedPreferences("FileName",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

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

		SharedPreferences preferences = getSharedPreferences("FileName",
				MODE_PRIVATE);

		username = preferences.getString("savedUsername", "username not set");
		password = preferences.getString("savedPassword", "password not set");
		user_id = preferences.getInt("savedUserId", 00);
		isLoggedIn = preferences.getBoolean("isLoggedIn", false);

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
					if (user_created == true) {
						Log.d("CREATEUSER", "USER CREATED");
						// user registered successfully
						Toast.makeText(getApplicationContext(),
								username + " has been registered",
								Toast.LENGTH_SHORT).show();
						// call LoadAllUsers to get the users id and to confirm
						// user is fully created
						BUTTONPRESSED = LOGIN;
						new LoadUser().execute();
					} else if (user_created == false) {
						Toast.makeText(getApplicationContext(),
								username + " is not available...",
								Toast.LENGTH_SHORT).show();
						// Intent i = new Intent(getApplicationContext(),
						// EasyOrderERROR.class);
						// startActivity(i);
					}
				}
			});

		}
	}

	@Override
	public void onBackPressed() {
		// do nothing
	}

}
