package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.Utilities.REGISTER_SERVER_URL;
import static ie.dcu.easyorderfyp.Utilities.SENDER_ID;
import static ie.dcu.easyorderfyp.Utilities.UNREGISTER_SERVER_URL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoggedInActivity extends Activity {

	// TextView
	private TextView tvWelcome;
	
	// Buttons
	private Button btnEatIn;
	private Button btnTakeAway;
	
	// Variables for Login Data
	private int id;
	static String username;
	static String password;
	
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
		
		Toast.makeText(getApplicationContext(),
				username + " " + password, 
				Toast.LENGTH_LONG).show();
		
		// display table number at the top of the screen
		tvWelcome = (TextView) findViewById(R.id.txtHeader);
		tvWelcome.setText("Hello " + username + ", What would you like to do?");
		
		btnEatIn = (Button) findViewById(R.id.btnEatIn);
		btnTakeAway = (Button) findViewById(R.id.btnTakeAway);
		
        // eat in click event
        btnEatIn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), EatInActivity.class);
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
    			// Sending registraiton details to MainActivity
    			i.putExtra("username", username);
    			i.putExtra("password", password);
    			startActivity(i);
    			finish();
 
            }
        });
	}


}
