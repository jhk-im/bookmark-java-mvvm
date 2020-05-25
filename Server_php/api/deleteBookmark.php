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
$bookmarkId = $_GET['bookmark_id'];
$categoryTitle = $_GET['category_title'];

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
        $newBookmarks = array(); 
        $count = 0;
        for($i = 0, $size = count($categories); $i < $size; ++$i)
        {          
          if($categoryTitle === $categories[$i]['category_title'])
          {          
            $bookmarks = $categories[$i]['bookmarks'];        
            for($j = 0, $sizej = count($bookmarks); $j < $sizej; ++$j)
            {
              if($bookmarkId === $bookmarks[$j]['bookmark_id'])
              {
                unset($bookmarks[$j]);            
                //error_log($bookmarkId);
              }
              else
              {
                $bookmarks[$j]['bookmark_position'] = $count;
                array_push($newBookmarks,$bookmarks[$j]);
                ++$count;
              }              
            }
            $categories[$i]['bookmarks'] = $newBookmarks;                                                                          
          }
        } 
        $json['categories'] = $categories;
        //error_log(json_encode($json['categories']));         
        http_response_code(200);
        file_put_contents($file,json_encode($json,JSON_PRETTY_PRINT));                                      
    }catch (Exception $e){

    http_response_code(401);

    echo json_encode(array(
        "message" => "Access denied.",
        "error" => $e->getMessage()
    ));
}

}
?>