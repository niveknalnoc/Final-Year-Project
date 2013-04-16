package ie.dcu.easyorderfyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
	
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

		if(status != null)
			// Setting alert dialog icon
			builder.setIcon((status) ? R.drawable.success : R.drawable.fail);
		
        AlertDialog alert = builder.create();
        alert.show();
    }

}