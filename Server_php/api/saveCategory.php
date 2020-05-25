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
//$memberEmail = $data->member_email;
$categoryId = $data->category_id;
$categoryTitle = $data->category_title;
$categoryPosition = $data->category_position;
$categorySelected = $data->category_selected;

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
        if(sizeof($json['categories'])<1)
        {
          $categorySelected = true;
        }
        $newCategory = array(
          'category_id'=>$categoryId,
          'category_title'=>$categoryTitle,
          'category_position'=>sizeof($json['categories']),
          'category_selected'=>$categorySelected,
          'bookmarks'=>array());
        array_push($json['categories'],$newCategory);
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