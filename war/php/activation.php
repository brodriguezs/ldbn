<html>
<head>
<title>LDBN - User account activation</title>
</head>
<body>
<?php
require_once("opendb.php");
require_once("common.php");
	
if ($use_account_email_activation && isset($_GET['a'])) {
	$a = $_GET['a'];
	checkMD5($a);
	$sql = "SELECT user_id FROM activation WHERE activation_string='$a'";
	if (! $sth = $dbhandle->query($sql)) {
		die($db_error_xml);
	}
	if ($row = $sth->fetch()) {
		$user_id = $row[0];
		$sql = "UPDATE user SET active=1 WHERE user_id=$user_id";
		if (! $sth = $dbhandle->query($sql)) {
			die($db_error_xml);
		}
		echo ('<P><B>Your account is now activated. <B><BR>You can go to the ' .
			  '<A HREF="http://'.$ldbnhost.'">main page</A>' .
    		  ' and login with your username and password.');
    	$sql = "DELETE FROM activation WHERE activation_string='$a'";
    	$dbhandle->query($sql);
	} else {
		echo ("<P>Invalid activation string.</P>");
	}
} else {
	echo ('<P>Some arguments are not set.</P>');
}
?>
</body>
</html>