package ie.dcu.easyorderfyp;

import static ie.dcu.easyorderfyp.RegisterActivity.isRegistered;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;

public class OrderSubmitted extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isRegistered == true){
			setContentView(R.layout.activity_order_submitted_tw);
			unregisterAlert();
		}else{
			setContentView(R.layout.activity_order_submitted);
		}
		
	}
	
	private void unregisterAlert() {

		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;
				}
			}

		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(OrderSubmitted.this);
		builder.setTitle("Order Submitted!");
		builder.setMessage("Stay on the following screen until you collect your food!");
		builder.setPositiveButton("Ok", dialogClickListener).show();
	}// end userNotFoundSplashScreen
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(isRegistered == true){
			GCMRegistrar.unregister(this);
			isRegistered = false;
		}
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(i);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isRegistered == true){
			GCMRegistrar.unregister(this);
			isRegistered = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(isRegistered == true){
			GCMRegistrar.unregister(this);
			isRegistered = false;
		}
	}
}