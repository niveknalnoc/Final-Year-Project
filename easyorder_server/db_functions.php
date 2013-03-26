<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor
    function __construct() {
        include_once 'gcm_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {
        $this->db->close();
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($gcm_regid, $name, $user_pin) {
        // insert user into database
        $result = mysql_query("INSERT INTO gcm_users(gcm_regid, name, user_pin, created_at) VALUES('$gcm_regid', '$name', '$user_pin',  NOW())");
        return $result;
    }
    
    public function removeUser($gcm_regid) {
    		$result = mysql_query("DELETE FROM gcm_users WHERE gcm_regid ='$gcm_regid' ");
    		return $result;
    }

    /**
     * Get user by table_id
     */
    public function getUserByEmail($table_id) {
        $result = mysql_query("SELECT * FROM gcm_users WHERE table_id = '$table_id' LIMIT 1");
        return $result;
    }

    /**
     * Getting all users
     */
    public function getAllUsers() {
        $result = mysql_query("select * FROM gcm_users");
        return $result;
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($table_id) {
        $result = mysql_query("SELECT table_id from gcm_users WHERE table_id = '$table_id'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed
            return true;
        } else {
            // user not existed
            return false;
        }
    }

}

?>
