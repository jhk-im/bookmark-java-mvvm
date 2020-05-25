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

$data = json_decode(file_get_contents("php://input"),true);
//error_log(json_encode($data));

$headers = null;
$requestHeaders = apache_request_headers();
$requestHeaders = array_combine(array_map('ucwords',array_keys($requestHeaders)), array_values($requestHeaders));
if (isset($requestHeaders['Authorization'])) 
{
  $headers = trim($requestHeaders['Authorization']);            
}
if (isset($requestHeaders['MemberEmail'])) 
{
  $memberEmail = trim($requestHeaders['MemberEmail']);  
}

$arr = explode(" ", $headers);


$jwt = $arr[1];

if($jwt){

    try {

        $decoded = JWT::decode($jwt, $secret_key, array('HS256'));
        $file = './bookmark/'.$memberEmail.'.txt';
        $json = json_decode(file_get_contents($file),true);
        $categories = $json['categories'];
        for($i = 0, $size = count($categories); $i < $size; ++$i)
        {
          for($j = 0, $sizej = count($data); $j < $sizej; ++$j)
            {
              if($categories[$i]['category_id'] === $data[$j]['category_id'])
              {
                //error_log($categories[$i]['category_position']);
                //error_log($j);
                $categories[$i]['category_position'] = $j;                                
              }
            }
        }
        $json['categories'] = $categories;
        //error_log(json_encode($json));
        file_put_contents($file,json_encode($json,JSON_PRETTY_PRINT));
        http_response_code(200);  
                
    }catch (Exception $e){

    http_response_code(401);

    echo json_encode(array(
        "message" => "Access denied.",
        "error" => $e->getMessage()
    ));
}

}
?>