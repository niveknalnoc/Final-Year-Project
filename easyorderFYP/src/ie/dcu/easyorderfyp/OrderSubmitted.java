package ie.dcu.easyorderfyp;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static ie.dcu.easyorderfyp.RegisterActivity.isRegistered;

public class OrderSubmitted extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_submitted);
		
		if(isRegistered == true){
			GCMRegistrar.unregister(this);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(i);
	}
}