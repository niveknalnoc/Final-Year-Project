package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.Utilities.SENDER_ID;
import static ie.dcu.easyorderfyp.Utilities.REGISTER_SERVER_URL;
import static ie.dcu.easyorderfyp.Utilities.UNREGISTER_SERVER_URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TakeAwayActivity extends Activity {

		public static String name;
		public static String user_pin;

		// UI elements
		EditText txtName;
		EditText txtPin;
		
		// alert dialog manager
		AlertDialogManager alert;
		
		// Register button
		Button btnRegister;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_take_away);
			
			alert = new AlertDialogManager();
			
			// Check if GCM configuration is set
			if (REGISTER_SERVER_URL == null || UNREGISTER_SERVER_URL == null || SENDER_ID == null || REGISTER_SERVER_URL.length() == 0 
					|| UNREGISTER_SERVER_URL.length() == 0 || SENDER_ID.length() == 0) {
				// GCM sernder id / server urls are missing
				alert.showAlertDialog(TakeAwayActivity.this, "Configuration Error!",
						"Please set your Server URL and GCM Sender ID", false);
				// stop executing code by return
				 return;
			}
			
			txtName = (EditText) findViewById(R.id.txtName);
			txtPin = (EditText) findViewById(R.id.txtPin);
			btnRegister = (Button) findViewById(R.id.btnRegister);
			
			/*
			 * Click event on Register button
			 * */
			btnRegister.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// read users name and pin
					name = txtName.getText().toString();
					user_pin = txtPin.getText().toString();
					
					// Check if user filled the form
					if(name.trim().length() > 0 && user_pin.trim().length() > 0){
						// Launch Main Activity
						Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
						
						// Registering user on our server					
						// Sending registraiton details to MainActivity
						i.putExtra("name", name);
						i.putExtra("pin", user_pin);
						startActivity(i);
						finish();
					}else{
						// User entered no details
						alert.showAlertDialog(TakeAwayActivity.this, "Registration Error!", "Please enter your details", false);
					}
				}
			});
		}

}
