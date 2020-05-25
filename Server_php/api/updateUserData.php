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
$memberName = $data->member_name;
$photoUrl = $data->photo_url;
$darkTheme = $data->dark_theme;
$pushNotice = $data->push_notice;
$loginType = $data->login_type;

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
        $table_name = 'member';          
        $query = "UPDATE " . $table_name . 
        " SET photo_url = :photoUrl, dark_theme = :darkTheme, push_notice = :pushNotice 
        WHERE member_email =:memberEmail";
        $stmt = $conn->prepare( $query );
        $stmt->bindParam(':memberEmail', $memberEmail);
        $stmt->bindParam(':photoUrl', $photoUrl);
        $stmt->bindParam(':darkTheme', $darkTheme);
        $stmt->bindParam(':pushNotice', $pushNotice);
        $stmt->execute();
        
        echo json_encode(
            array(
                "member_email" => "",
                "member_name" => "",
                "photo_url" => $photoUrl,
                "auto_password" => "",
                "dark_theme" => (bool)$darkTheme,
                "push_notice" => (bool)$pushNotice,
                "login_type" => $loginType
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