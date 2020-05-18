package com.jroomstudio.smartbookmarkeditor.util;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.JwtToken;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Service Interface
 * - 웹서버와 통신을 위한 API 를 정의하기 위한 인터페이스
 * - POST, DELETE, GET, PUT 등 다양한 Request Method 제공
 **/

public interface NetRetrofitService {

    String SERVER_URL = "http://115.68.221.104";

    /**
     * GET 방식, URL/member/{userEmail} 호출.
     * Member Type 의 JSON 을 통신을 통해 받음.
     * "http://115.68.221.104/member/userEmail" 이 최종 호출 주소.
     * @return Member 객체를 JSON 형태로 반환.
     */
    @GET("member/php_retrofit_test/get_data.php")
    Call<ResponseBody> getData(@Query("member_email") String memberEmail,
                               @Query("auto_password") String autoPassword);


    @GET("member/php_retrofit_test/get_response.php")
    Call<Member> getDataCallback(@Query("member_email") String memberEmail,
                                 @Query("auto_password") String autoPassword);


    /**
     * POST 방식
     * Field 형식을 통해 넘겨주는 값들이 여러 개일 때 FieldMap 을 사용함.
     * Retrofit 에서는 Map 보다는 HashMap 권장.
     * FormUrlEncoded Field 형식 사용 시 Form 이 Encoding 되어야 하기 때문에 사용하는 어노테이션
     * Field 형식은 POST 방식에서만 사용가능.
     * @return Data 객체를 JSON 형태로 반환.
     */

    // Field 로 하나하나 입력해서 넘기기
    @FormUrlEncoded
    @POST("member/php_retrofit_test/register.php")
    Call<ResponseBody> postData(@Field("member_email") String email,
                                @Field("member_name") String name,
                                @Field("auto_password") String password,
                                @Field("photo_url") String photoUrl,
                                @Field("dark_theme") boolean darkTheme,
                                @Field("push_notice") boolean pushNotice,
                                @Field("login_type") int loginType,
                                @Field("login_status") boolean loginStatus
    );


    // json 으로 데이터 넘기기 (회원가입)
    @POST("member/api/register.php")
    Call<Void> postData(
            @Header("Content-Type") String contentType,
            @Body Member param);

    // Json 으로 넘기고 jwt 콜백
    @POST("member/api/login.php")
    Call<JwtToken> postDataCallback(
            @Header("Content-Type") String contentType,
            @Body Member param);

    // HashMap 으로 넘기고 콜백 받기 (로그인)
    @FormUrlEncoded
    @POST("member/api/login.php")
    Call<Member> postDataCallback(@FieldMap HashMap<String, Object> param);


    // HashMap 으로 넘기기
    @FormUrlEncoded
    @POST("member/php_retrofit_test/register.php")
    Call<ResponseBody> postData(@FieldMap HashMap<String, Object> param);



    // jwt test
    // header 로 jwt 토큰을 넘겨받는다.
    @FormUrlEncoded
    @POST("member/php_retrofit_test/post_jwt_create.php")
    Call<Void> postJWTGetToken(@FieldMap HashMap<String, Object> param);
    // jwt auth
    @POST("member/php_retrofit_test/post_jwt_auth.php")
    Call<ResponseBody> postJWTAuth(@Header("Authorization") String auth);

    /**
     * PUT 방식. 값은 위들과 같음.
     * Body Data param : 통신을 통해 전달하는 값이 특정 JSON 형식일 경우
     * 매번 JSON 으로 변환하지 않고, 객체를 통해서 넘겨주는 방식.
     * PUT 뿐만 아니라 다른 방식에서도 사용가능.
     * @param member 전달 데이터
     * @return Member 객체를 JSON 형태로 반환.
     */
    @PUT("member/php_retrofit_test/post_json_response.php")
    Call<ResponseBody> putData(@Body Member member);


    /**
     * PATCH 방식. 값은 위들과 같습니다.
     * Field("title") String title : patch 방식을 통해 title 에 해당하는 값을 넘기기 위해 사용.
     * FormUrlEncoded Field 형식 사용 시 Form 이 Encoding 되어야 하기 때문에 사용하는 어노테이션
     * @return Member 객체를 JSON 형태로 반환.
     */
    @FormUrlEncoded
    @PATCH("member/php_retrofit_test/get_data.php")
    Call<ResponseBody> patchData(@Field("member_email") String email, @Field("member_name") String name);


    /**
     * DELETE
     * Call<ResponseBody> : 통신을 통해 되돌려 받는 값이 없을 경우 사용.
     */
    @DELETE("member/php_retrofit_test/{userEmail}")
    Call<ResponseBody> deleteData(@Path("user_email") String userEmail);

}
