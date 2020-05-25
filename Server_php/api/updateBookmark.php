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
$memberEmail = $_GET['member_email'];
$updateCategory = $_GET['update_category'];
$bookmarkId = $_GET['bookmark_id'];
$bookmarkTitle = $_GET['bookmark_title'];
$bookmarkUrl = $_GET['bookmark_url'];
$bookmarkCategory = $_GET['bookmark_category'];
$bookmarkFavicon = $_GET['bookmark_favicon'];


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
        $count = 0;
        //$replaceBookmarks = array();
        for($i = 0, $size = count($json['categories']); $i < $size; ++$i)
        { 
          if($updateCategory === $categories[$i]['category_title'])
          {
            $bookmarkPosition = sizeof($categories[$i]['bookmarks']); 
            $newBookmark = array(
              'bookmark_id'=>$bookmarkId,
              'bookmark_title'=>$bookmarkTitle,
              'bookmark_url'=>$bookmarkUrl,
              'bookmark_action'=>'WEB_VIEW',
              'bookmark_category'=>$updateCategory,
              'bookmark_position'=>$bookmarkPosition,
              'bookmark_favicon'=>$bookmarkFavicon);
            array_push($categories[$i]['bookmarks'],$newBookmark);
          } 
          
          if($bookmarkCategory === $categories[$i]['category_title'])
          {
            for($j = 0, $sizej = count($categories[$i]['bookmarks']); $j < $sizej; ++$j)
            {
              if($bookmarkId === $categories[$i]['bookmarks'][$j]['bookmark_id'])
              {              
                if($updateCategory ==='')
                {
                  $categories[$i]['bookmarks'][$j]['bookmark_title'] = $bookmarkTitle;
                  $categories[$i]['bookmarks'][$j]['bookmark_url'] = $bookmarkUrl;
                  $categories[$i]['bookmarks'][$j]['bookmark_category'] = $bookmarkCategory;
                  $categories[$i]['bookmarks'][$j]['bookmark_favicon'] = $bookmarkFavicon;  
                  $categories[$i]['bookmarks'][$j]['bookmark_position'] = $count;                            
                }
                else
                {
                  unset($categories[$i]['bookmarks'][$j]);
                  //error_log(json_encode($categories[$i]['bookmarks']));           
                }                               
              }
              else
              {  
                if($updateCategory != '')
                {
                  $categories[$i]['bookmarks'][$j]['bookmark_position'] = $count;
                  //array_push($replaceBookmarks, $categories[$i]['bookmarks'][$j]);
                  ++$count;                                     
                }             
              }                          
            }                 
          }                                   
        }  
        $json['categories'] = $categories;
        //error_log(json_encode($categories));
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