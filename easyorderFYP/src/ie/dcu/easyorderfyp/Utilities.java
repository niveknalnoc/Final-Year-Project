package ie.dcu.easyorderfyp;

import android.content.Context;
import android.content.Intent;

public class Utilities {
	
	// GCM registration url
    static final String REGISTER_SERVER_URL = "http://192.168.1.12/easyorder_server/register.php"; 
    // GCM unregister url
    static final String UNREGISTER_SERVER_URL = "http://192.168.1.12/easyorder_server/unregister.php"; 
    // Url to get all items from the database
    static final String URL_DOWNLOAD_MENU = "http://192.168.1.12/easyorder_server/get_items.php";
    // url to submit order
    static final String URL_SUBMIT_ORDER = "http://192.168.1.12/easyorder_server/submit_order.php";
    // url to add user
    static final String URL_REGISTER_USER = "http://192.168.1.12/easyorder_server/add_user.php";
    // url to get users
    static final String URL_GET_USER = "http://192.168.1.12/easyorder_server/get_users.php";
    
	
    /**
     * 
     *  URL FOR TESTING WITHIN DCU 
     * 
     */
    /*
    // GCM registration url
    static final String REGISTER_SERVER_URL = "http://136.206.113.140/easyorder_server/register.php"; 
    // GCM unregister url
    static final String UNREGISTER_SERVER_URL = "http://136.206.113.140/easyorder_server/unregister.php"; 
    // Url to get all items from the database
    static final String URL_DOWNLOAD_MENU = "http://136.206.113.140/easyorder_server/get_items.php";
    // url to submit order
    static final String URL_SUBMIT_ORDER = "http://136.206.113.140/easyorder_server/submit_order.php";
    // url to add user
    static final String URL_REGISTER_USER = "http://136.206.113.140/easyorder_server/add_user.php";
    // url to get users
    static final String URL_GET_USER = "http://136.206.113.140/easyorder_server/get_users.php";*/

    // Google project id
    static final String SENDER_ID = "543416993618"; 

    static final String DISPLAY_MESSAGE_ACTION =
            "EasyOrder";

    static final String EXTRA_MESSAGE = "Message";

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
