package ie.dcu.easyorderfyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EatInActivity extends Activity {
	
	private Button btnScanTable;
	private IntentResult activityResultIntent;
	
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eat_in);
		
		final Activity returnActivity = this;
		
		btnScanTable = (Button) findViewById(R.id.btnScan);
		
		// scan table code intent onClick(btnScan)
		btnScanTable.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
        	   // Launching scan item Activity
           	   IntentIntegrator integrator = new IntentIntegrator(returnActivity);
			   integrator.initiateScan();
           }
       });
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// parse scanned code
		if(activityResultIntent != null) {
			String codeContents = activityResultIntent.getContents();
			boolean isValidCodeScanned = validateCodeContents(codeContents);
			
			if(isValidCodeScanned){
				// call intent to start scanning items
				Log.e("Scanned Locator Code...", "> " + codeContents);
				final Activity returnActivity = this;
				Intent scanItemsActivity = new Intent(this, ScanItemsActivity.class);
				scanItemsActivity.putExtra("tableNumber", codeContents.substring(1));
				startActivity(scanItemsActivity);
			}
			else {
				// alert dialog - invalid code scanned
				alert.showAlertDialog(this,
						"Invalid Locator Code",
						"Please scan a valid Locator QR code located on your table!", false);
				Log.e("Scanned Locator Code...", "> " + codeContents);
			}
			
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent =
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	}
	
	private boolean validateCodeContents(String codeContents) {
		boolean x = false;
		String firstToken = codeContents.substring(0, 1);
		// check valid code scanned [char followed by an int]
		if(firstToken.equals("T") && codeContents.length() > 1) {
			try{
				int tableNumber = Integer.parseInt(codeContents.substring(1));
				x = true;
			}catch(NumberFormatException e){
				x = false;
			}
		}
		return x;
	}

}