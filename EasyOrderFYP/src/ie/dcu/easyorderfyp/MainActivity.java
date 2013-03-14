package ie.dcu.easyorderfyp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button btnViewProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		
		 // view products click event
        btnViewProducts.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), DownloadMenuItems.class);
                startActivity(i);
 
            }
        });

	}

}
