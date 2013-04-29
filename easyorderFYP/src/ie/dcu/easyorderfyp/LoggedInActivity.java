package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.Utilities.REGISTER_SERVER_URL;
import static ie.dcu.easyorderfyp.Utilities.SENDER_ID;
import static ie.dcu.easyorderfyp.Utilities.UNREGISTER_SERVER_URL;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoggedInActivity extends Activity {

	// TextView
	private TextView tvWelcome;
	
	// Buttons
	private Button btnEatIn;
	private Button btnTakeAway;
	private Button btnLogout;
	private Button btnPrevOrders;
	
	// Variables for Login Data
	public static int user_id;
	public static String username;
	public static String password;
	
	// Current user details
	User current_user;
	
	// Alert dialog manager
	AlertDialogManager alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent i = getIntent();
		User current_user = new User();
		current_user = (User) i.getParcelableExtra("this_user");
		
		username = current_user.getUserName();
		password = current_user.getPassword();
		user_id = current_user.getUserId();
		
		System.out.println("user_id LoggedInActivity : " + user_id);
	}
	
	public void onBackPressed() {
    }   
	
	private void resetSharedPreferences(){
		
		//Remove the users login data
        SharedPreferences preferences = getSharedPreferences("FileName",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        preferences.edit().clear().commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// display table number at the top of the screen
		tvWelcome = (TextView) findViewById(R.id.txtHeader);
		tvWelcome.setText("Hello " + username + ", What would you like to do?");
				
		btnEatIn = (Button) findViewById(R.id.btnEatIn);
		btnTakeAway = (Button) findViewById(R.id.btnTakeAway);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnPrevOrders = (Button) findViewById(R.id.btnPrevOrders);
		
		// eat in click event
		btnEatIn.setOnClickListener(new View.OnClickListener() {
				 
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), TableLocator.class);
				i.putExtra("username", username);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		});
				        
		// Take Away click event
		btnTakeAway.setOnClickListener(new View.OnClickListener() {
				 
			@Override
			public void onClick(View view) {
				// Launching take away Activity
				// Check if GCM configuration is set
				if (REGISTER_SERVER_URL == null || UNREGISTER_SERVER_URL == null || SENDER_ID == null || REGISTER_SERVER_URL.length() == 0 
						|| UNREGISTER_SERVER_URL.length() == 0 || SENDER_ID.length() == 0) {
						// GCM sernder id / server urls are missing
								alert.showAlertDialog(LoggedInActivity.this, "Configuration Error!",
								"Please set your Server URL and GCM Sender ID", false);
				    		// stop executing code by return
							return;
				}
				// Launch Main Activity
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				// Registering user on our server					
				// Sending registration details to MainActivity
				i.putExtra("username", username);
				i.putExtra("password", password);
				System.out.println("user_id button Take Away : " + user_id);
				i.putExtra("user_id", user_id);
				startActivity(i);
				finish();
				 
			}
		});
		
		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {
						 
			@Override
			public void onClick(View view) {
				// Remove user login data from SharedPreferences
				resetSharedPreferences();
				// Bring the user back to the main screen
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
			}
		});
		
		// eat in click event
		btnPrevOrders.setOnClickListener(new View.OnClickListener() {
						 
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), PreviousOrders.class);
				i.putExtra("username", username);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		});
		
	}
}