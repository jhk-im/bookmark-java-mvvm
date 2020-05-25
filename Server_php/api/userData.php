<?php
include_once './config/database.php';
require "../vendor/autoload.php";
use \Firebase\JWT\JWT;

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");


$secret_key = "YOUR_SECRET_KEY";
$jwt = null;
$databaseService = new DatabaseService();
$conn = $databaseService->getConnection();

$data = json_decode(file_get_contents("php://input"));
$memberEmail = $data->member_email;
//error_log(json_encode($data));

$table_name = 'member';
$query = "SELECT * FROM " . $table_name . " WHERE member_email = ? LIMIT 0,1";
$stmt = $conn->prepare( $query );
$stmt->bindParam(1, $memberEmail);
$stmt->execute();
$num = $stmt->rowCount();

if($num > 0){
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $email = $row['member_email'];
    $name = $row['member_name'];
    $photoUrl = $row['photo_url'];
    $password = $row['auto_password'];
    $darkTheme = $row['dark_theme'];
    $pushNotice = $row['push_notice'];
    $type = $row['login_type'];
}


$headers = null;
$requestHeaders = apache_request_headers();
$requestHeaders = array_combine(array_map('ucwords',array_keys($requestHeaders)), array_values($requestHeaders));
if (isset($requestHeaders['Authorization'])) 
{
  $headers = trim($requestHeaders['Authorization']);            
}

$arr = explode(" ", $headers);

/*
echo json_encode(array(
    "message" => "sd" .$arr[1]
));
*/

$jwt = $arr[1];

if($jwt){

    try {

        $decoded = JWT::decode($jwt, $secret_key, array('HS256'));

        // Access is granted. Add code of the operation here
        echo json_encode(
            array(
                "member_email" => $email,
                "member_name" => $name,
                "photo_url" => $photoUrl,
                "auto_password" => $password,
                "dark_theme" => (bool)$darkTheme,
                "push_notice" => (bool)$pushNotice,
                "login_type" => $type
            ));
        
        /*
        echo json_encode(array(
            "message" => "Access granted:",
            //"error" => $e->getMessage()
        ));
        */  
    }catch (Exception $e){

    http_response_code(401);

    echo json_encode(array(
        "message" => "Access denied.",
        "error" => $e->getMessage()
    ));
}

}
?>