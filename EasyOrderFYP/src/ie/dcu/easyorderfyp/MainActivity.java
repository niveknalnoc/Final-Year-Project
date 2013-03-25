package ie.dcu.easyorderfyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	// Buttons for main menu options
	private Button btnEatIn;
	private Button btnTakeAway;
	
    // alert dialog manager
 	AlertDialogManager alert = new AlertDialogManager();
 	
    // Internet detector
 	ConnectionDetector cd;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to wifi or 3g to use EasyOrder", false);
			// stop executing code by return
			return;
		}
		
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
            	Intent i = new Intent(getApplicationContext(), TakeAwayActivity.class);
                startActivity(i);
 
            }
        });
        
	}
}
