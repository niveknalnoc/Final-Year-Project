package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.ServerUtilities.REGISTER_SERVER_URL;
import static ie.dcu.easyorderfyp.GCMServerUtilities.SENDER_ID;
import static ie.dcu.easyorderfyp.ServerUtilities.UNREGISTER_SERVER_URL;

import com.google.android.gcm.GCMRegistrar;

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
	
	public static boolean takeAway = false;

	// Buttons
	private Button btnEatIn;
	private Button btnTakeAway;
	private Button btnLogout;
	private Button btnPrevOrders;

	// Alert dialog manager
	AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public void onBackPressed() {
		// do nothing
	}

	private void resetSharedPreferences() {

		// Remove the users login data
		SharedPreferences preferences = getSharedPreferences("FileName",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		preferences.edit().clear().commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// display table number at the top of the screen
		tvWelcome = (TextView) findViewById(R.id.txtHeader);
		tvWelcome.setText("Hello " + LoginActivity.username + ", What would you like to do?");

		btnEatIn = (Button) findViewById(R.id.btnEatIn);
		btnTakeAway = (Button) findViewById(R.id.btnTakeAway);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnPrevOrders = (Button) findViewById(R.id.btnPrevOrders);

		// eat in click event
		btnEatIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						TableLocator.class);
				startActivity(i);
			}
		});

		// Take Away click event
		btnTakeAway.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching take away Activity
				// Check if GCM configuration is set
				if (REGISTER_SERVER_URL == null
						|| UNREGISTER_SERVER_URL == null || SENDER_ID == null
						|| REGISTER_SERVER_URL.length() == 0
						|| UNREGISTER_SERVER_URL.length() == 0
						|| SENDER_ID.length() == 0) {
					// GCM sernder id / server urls are missing
					alert.showAlertDialog(LoggedInActivity.this,
							"Configuration Error!",
							"Please set your Server URL and GCM Sender ID",
							false);
					// stop executing code by return
					return;
				}
				takeAway = true;
				// Launch Main Activity
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				// Registering user on our server
				// Sending registration details to MainActivity
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
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
			}
		});

		// previous orders click event
		btnPrevOrders.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						PreviousOrders.class);
				startActivity(i);
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}