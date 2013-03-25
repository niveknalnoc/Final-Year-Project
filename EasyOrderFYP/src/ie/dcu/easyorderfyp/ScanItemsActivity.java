package ie.dcu.easyorderfyp;

import com.google.zxing.integration.android.IntentIntegrator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanItemsActivity extends Activity {

	private String tableNumber;
	
	private Button btnScanItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_items);
		
		Bundle b = this.getIntent().getExtras();
		tableNumber = b.getString("tableNumber");
		
		Log.e("Table Number", "> " + tableNumber);
		
		TextView tvTableNum = (TextView) findViewById(R.id.txtTableNumber);
        tvTableNum.setText("Table: " + tableNumber);
        
        final Activity returnActivity = this;
        
        btnScanItems = (Button) findViewById(R.id.btnScan);
		
		// scan table code intent onClick(btnScan)
		btnScanItems.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
        	   // Launching scan item Activity
           	   IntentIntegrator integrator = new IntentIntegrator(returnActivity);
			   integrator.initiateScan();
           }
       });
	}


}
