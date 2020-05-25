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
$bookmarkId = $data->bookmark_id;
$bookmarkTitle = $data->bookmark_title;
$bookmarkUrl = $data->bookmark_url;
$bookmarkAction = $data->bookmark_action;
$bookmarkCategory = $data->bookmark_category;
$bookmarkPosition = $data->bookmark_position;
$bookmarkFavicon = $data->bookmark_favicon;


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
        $bookmarks = array();
        for($i = 0, $size = count($json['categories']); $i < $size; ++$i){
          
          
          if($bookmarkCategory === $categories[$i]['category_title'])
          {
            $bookmarks = $categories[$i]['bookmarks'];
            $bookmarkPosition = sizeof($bookmarks);  
            $newBookmark = array(
              'bookmark_id'=>$bookmarkId,
              'bookmark_title'=>$bookmarkTitle,
              'bookmark_url'=>$bookmarkUrl,
              'bookmark_action'=>$bookmarkAction,
              'bookmark_category'=>$bookmarkCategory,
              'bookmark_position'=>$bookmarkPosition,
              'bookmark_favicon'=>$bookmarkFavicon);
            //error_log(json_encode($newBookmark));
            array_push($categories[$i]['bookmarks'],$newBookmark);
            $json['categories'] = $categories;
            file_put_contents($file,json_encode($json,JSON_PRETTY_PRINT));
            http_response_code(200);                          
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