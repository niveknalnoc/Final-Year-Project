package ie.dcu.easyorderfyp;

import android.content.Context;
import android.content.Intent;

public class Utilities {
	
	// give your server registration url here
    static final String REGISTER_SERVER_URL = "http://192.168.1.12/easyorder_server/register.php"; 
    static final String UNREGISTER_SERVER_URL = "http://192.168.1.12/easyorder_server/unregister.php"; 
    

    // Google project id
    static final String SENDER_ID = "543416993618"; 

    static final String DISPLAY_MESSAGE_ACTION =
            "EASYORDER";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}
