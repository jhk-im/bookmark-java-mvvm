<?php
include_once './config/database.php';

header("Access-Control-Allow-Origin: * ");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

$memberEmail = '';
$memberName = '';
$photoUrl = '';
$autoPassword = '';
$darkTheme = true;
$pushNotice = true;
$loginType = 0;
$loginStatus = true;
$conn = null;

$databaseService = new DatabaseService();
$conn = $databaseService->getConnection();

$data = json_decode(file_get_contents("php://input"));

//error_log(json_encode($data));

$memberEmail = $data->member_email;
$memberName = $data->member_name;
$photoUrl = $data->photo_url;
$autoPassword = $data->auto_password;
$darkTheme = $data->dark_theme;
$pushNotice = $data->push_notice;
$loginType = $data->login_type;
//$loginStatus = $data->login_status;

$table_name = 'member';

$query = "INSERT INTO " . $table_name . "
                SET member_email = :memberEmail,
                    member_name = :memberName,
                    photo_url = :photoUrl,
                    auto_password = :autoPassword,
                    dark_theme = :darkTheme,
                    push_notice = :pushNotice,
                    login_type = :loginType";

$stmt = $conn->prepare($query);

$stmt->bindParam(':memberEmail', $memberEmail);
$stmt->bindParam(':memberName', $memberName);
$stmt->bindParam(':photoUrl', $photoUrl);
$password_hash = password_hash($autoPassword, PASSWORD_BCRYPT);
$stmt->bindParam(':autoPassword', $password_hash);
$stmt->bindParam(':darkTheme', $darkTheme);
$stmt->bindParam(':pushNotice', $pushNotice);
$stmt->bindParam(':loginType', $loginType);

if($stmt->execute()){

    http_response_code(200);
    echo json_encode(array('message' => 'User was successfully registered.'));
    
    $file = './bookmark/'.$memberEmail.'.txt';
    $userFile = fopen($file,'w');
    
    $bookmarks = array();
    $bookmark = array(
    'bookmark_id'=>'first_bookmark',
    'bookmark_title'=>'Google',
    'bookmark_url'=>'https://www.google.com/',
    'bookmark_action'=>'WEB_VIEW',
    'bookmark_category'=>'Bookmark',
    'bookmark_position'=>0,
    'bookmark_favicon'=>'https://www.google.com/favicon.ico');
    
    $categories = array();
    $category = array(
    'category_id'=>'first_category'
    ,'category_title'=>'Bookmark',
    'category_position'=>0,
    'category_selected'=>true,
    'bookmarks'=>array($bookmark));
    $categories['categories'] = array($category);
    
    file_put_contents($file,json_encode($categories,JSON_PRETTY_PRINT));
    
    
}
else{
    http_response_code(400);

    echo json_encode(array('message' => 'Unable to register the user.'));
}
?>