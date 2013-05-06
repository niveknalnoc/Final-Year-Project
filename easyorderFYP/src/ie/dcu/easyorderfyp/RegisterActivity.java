package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.GCMServerUtilities.DISPLAY_MESSAGE_ACTION;
import static ie.dcu.easyorderfyp.GCMServerUtilities.EXTRA_MESSAGE;
import static ie.dcu.easyorderfyp.GCMServerUtilities.SENDER_ID;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;


public class RegisterActivity extends Activity {
	
		// Asyntask
		AsyncTask<Void, Void, Void> mRegisterTask;
		
		// Alert dialog manager
		AlertDialogManager alert = new AlertDialogManager();
		
		// Variables for Login Data
		private static int user_id;
		private static String username;
		private static String password;
		
		static boolean isRegistered = false;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.blank);
			
			Bundle b = this.getIntent().getExtras();
			user_id = b.getInt("user_id");
			username = b.getString("username");
			password = b.getString("password");
			
			Toast.makeText(getApplicationContext(), 
					username + " " + password + " " + user_id, 
					Toast.LENGTH_LONG).show();
			
			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(this);

			// Make sure the manifest was properly set
			GCMRegistrar.checkManifest(this);

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));
			
			// Get GCM registration user_id
			final String regId = GCMRegistrar.getRegistrationId(this);

			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM			
				GCMRegistrar.register(this, SENDER_ID);
				isRegistered = true;
				Intent i = new Intent(getApplicationContext(), TableLocator.class);
				i.putExtra("username", username);
				i.putExtra("user_id", user_id);
				System.out.println("user_id RegisterActivity(user registered -> eatinactivity: " + user_id);
				startActivity(i);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.	
					isRegistered = true;
					Intent i = new Intent(getApplicationContext(), TableLocator.class);
					i.putExtra("username", username);
					i.putExtra("user_id", user_id);
					startActivity(i);
					Toast.makeText(getApplicationContext(), "Oops..Device already registered with GCM... Moving on :)", Toast.LENGTH_LONG).show();
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = this;
					mRegisterTask = new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							// Register on our server
							// On server creates a new user
							GCMServerUtilities.register(context, username, password, regId);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mRegisterTask = null;
						}

					};
					mRegisterTask.execute(null, null, null);
				}
				
			}
		}		

		/**
		 * Receiving push messages
		 * */
		private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
				System.out.println("NEWMESSAGE : .. : -> " +newMessage);
				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
				
				/**
				 * Take appropriate action on this message
				 * depending upon your app requirement
				 * For now i am just displaying it on the screen
				 * */
				
				// Showing received message
				Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
				
				// Releasing wake lock
				WakeLocker.release();
			}
		};
		
		@Override
		protected void onDestroy() {
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}
			try {
				unregisterReceiver(mHandleMessageReceiver);
				GCMRegistrar.onDestroy(this);
			} catch (Exception e) {
				Log.e("UnRegister Receiver Error", "> " + e.getMessage());
			}
			super.onDestroy();
		}

}