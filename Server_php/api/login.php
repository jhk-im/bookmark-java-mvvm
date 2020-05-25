<?php
include_once './config/database.php';
require "../vendor/autoload.php";
use \Firebase\JWT\JWT;

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");


$memberEmail = '';
$autoPassword = ''; 

$databaseService = new DatabaseService();
$conn = $databaseService->getConnection();



$data = json_decode(file_get_contents("php://input"));

//error_log(json_encode($data));

$memberEmail = $data->member_email;
$autoPassword = $data->auto_password;
$loginType = $data->login_type;

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
    $password = $row['auto_password'];
    $type = $row['login_type'];
    
    if(password_verify($autoPassword, $password))
    {
        $secret_key = "YOUR_SECRET_KEY";
        $issuer_claim = "THE_ISSUER"; // this can be the servername
        $audience_claim = "THE_AUDIENCE";
        $issuedat_claim = time(); // issued at
        $notbefore_claim = $issuedat_claim + 0; //not before in seconds
        $expire_claim = $issuedat_claim + 60; // expire time in seconds
        $token = array(
            "iss" => $issuer_claim,
            "aud" => $audience_claim,
            "iat" => $issuedat_claim,
            "nbf" => $notbefore_claim,
            "exp" => $expire_claim,
            "data" => array(
                "member_email" => $email,
                "member_name" => $name,
                "auto_password" => $autoPassword
        ));

        http_response_code(200);

        $jwt = JWT::encode($token, $secret_key);
        echo json_encode(
            array(
                "message" => "Successful login.",
                "jwt" => $jwt,
                "email" => $email,
                "expireAt" => $expire_claim,
                "name" => $name
            ));
    }
    else
    {

        http_response_code(401);
        echo json_encode(
            array(
                "message" => "LoginFailed", 
                "jwt" => "",
                "email" => $email,
                "expireAt" => "",
                "name" => ""
                ));
    }
}

?>
