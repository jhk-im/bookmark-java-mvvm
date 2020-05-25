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

$memberEmail = $_GET['member_email'];
$categoryTitle = $_GET['category_title'];
$bookmarkId = $_GET['bookmark_id'];
error_log($memberEmail);
error_log($bookmarkId);
$data = json_decode(file_get_contents("php://input"));
$headers = null;
$requestHeaders = apache_request_headers();
$requestHeaders = array_combine(array_map('ucwords',array_keys($requestHeaders)), array_values($requestHeaders));
if (isset($requestHeaders['Authorization'])) 
{
  $headers = trim($requestHeaders['Authorization']);            
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
          if($categoryTitle === $categories[$i]['category_title'])
          {            
            //error_log($categoryTitle);
            //error_log($categories[$i]['category_title']);
            for($j = 0, $sizej = count($categories[$i]['bookmarks']); $j < $sizej; ++$j)
            {            
              if($bookmarkId === $categories[$i]['bookmarks'][$j]['bookmark_id'])
              {              
                http_response_code(200);
                echo json_encode($categories[$i]['bookmarks'][$j]);
                //error_log(json_encode($categories[$i]['bookmarks'][$j]));
              }
            }              
          }
        }
                 
    }catch (Exception $e){

    http_response_code(401);

    echo json_encode(array(
        "message" => "Access denied.",
        "error" => $e->getMessage()
    ));
}

}
?>