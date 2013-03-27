package ie.dcu.easyorderfyp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanItemsActivity extends Activity {
	
	private Button btnScanItems;
	
	private int thisRequestCode;
	private int thisResultCode;
	private String codeContents;
	private String tableNumber;

	// arraylist to hold the menu, itemsScaned and the order
	ArrayList<MenuItem> menu;
	ArrayList<MenuItem> scannedItems;
	ArrayList<Order> order;
	
	final Activity returnActivity = this;
	
	private IntentResult activityResultIntent;
	
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_items);
		
		Bundle b = this.getIntent().getExtras();
		tableNumber = b.getString("tableNumber");
		menu = new ArrayList<MenuItem>();
		scannedItems = new ArrayList<MenuItem>();
		menu = getIntent().getParcelableArrayListExtra ("downloadedMenuItems");
		
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

	@Override
	protected void onResume() {
		super.onResume();
		
		// parse scanned code
		if (thisRequestCode == 49374) {
            if (thisResultCode == -1) { 
	          	codeContents = activityResultIntent.getContents();
	         	MenuItem item = parseCodeScanned(codeContents);
	         	if(menu.contains(item)) {
	         		// order matches the menu on the db, add to order arraylist
	         		scannedItems.add(item);
	         	}else{
	         		//menu does not contain this item, let customer know
	         		alert.showAlertDialog(this,
            				"Invalid Item Code",
            				"This Item Is Not Available Today!", false);
	         	}
	         	
            }
		}
		
		// **** NEED A BUTTON FOR SUBMITTING THE ORDER ****
		// AFTER ORDER SUBMITTED CLEAR THE MENUITEMSSCANNED ARRAYLIST
		// DISPLAY ORDER SUBMITTED SCREEN - BACK BUTTON BRINGS TO MAIN MENU
		
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		activityResultIntent =
				IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		thisRequestCode = requestCode;
		thisResultCode = resultCode;
		
	}
	
	private MenuItem parseCodeScanned(String codeContents) {
		
		int startCount = 0;
		int endCount = 0;
		
		boolean idSet = false;
		boolean nameSet = false;
		boolean priceSet = false;
		
		String setId = null;
		String setItemName = null;
		Double setPrice = null;
		int setAvailable = 3;
		String temp;
		
		for(int i = 0 ; i < codeContents.length() ; i++) {
			if(codeContents.charAt(i) == ':'){
				temp = codeContents.substring(startCount, endCount);
				endCount++;
				startCount = endCount;
				if(idSet != true) {
					setId = temp;
					idSet = true;
				}else if(nameSet != true) {
					setItemName = temp;
					nameSet = true;
				}else if(priceSet != true) {
					setPrice = Double.parseDouble(temp);
					setAvailable = Integer.parseInt(codeContents.substring(startCount));
					priceSet = true;
				}
			}else{
				endCount++;
			}
		}
		
		return new MenuItem(setId, setItemName, setPrice, setAvailable);
	}

}
