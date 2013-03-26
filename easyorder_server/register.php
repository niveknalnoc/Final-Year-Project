<?php

// response json
$json = array();

/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["gcm_regid"]) && isset($_POST["name"]) && isset($_POST["user_pin"])) {
    $gcm_regid = $_POST["gcm_regid"]; // GCM Registration ID
    $name = $_POST["name"];
    $user_pin = $_POST["user_pin"];
    
    // Store user details in db
    include_once 'db_functions.php';
    include_once 'GCM.php';

    $db = new DB_Functions();
    $gcm = new GCM();

    $res = $db->storeUser($gcm_regid, $name, $user_pin);

    $registration_ids = array($gcm_regid);
    $message = array("product" => "shirt");

    $result = $gcm->send_notification($registration_ids, $message);

    echo $result;
} else {
    // user details missing
}
?>
