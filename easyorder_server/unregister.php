<?php

// response json
$json = array();

/**
 * Unregistering a user device
 * Store reg id in users table
 */
if (isset($_POST["gcm_regid"])) {
    $gcm_regid = $_POST["gcm_regid"]; // GCM Registration ID
    
    // Remove user details in db
    include_once 'db_functions.php';

    $db = new DB_Functions();

    $result = $db->removeUser($gcm_regid);

    echo $result;
} else {
    // user details missing
}
?>
