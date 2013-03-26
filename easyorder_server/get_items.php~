<?php
 
/*
 * get_item.php
 */
 
// array for JSON response
$response = array();
 
// include db connect class
//require_once 'db_connect.php';
include_once 'db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// get all products from products table
$result = mysql_query("SELECT * FROM menu_items WHERE available =1") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // items node
    $response["items"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        
        // temp user array
        $product = array();
        $product["item_id"] = $row["item_id"];
        $product["item_name"] = $row["item_name"];
        $product["price"] = $row["price"];
        $product["available"] = $row["available"];
 
        // push single product into final response array
        array_push($response["items"], $product);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No products found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>

