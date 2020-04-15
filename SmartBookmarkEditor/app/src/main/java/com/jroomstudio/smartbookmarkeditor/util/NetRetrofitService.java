package com.jroomstudio.smartbookmarkeditor.util;


import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Service Interface
 * - 웹서버와 통신을 위한 API 를 정의하기 위한 인터페이스
 * - POST, DELETE, GET, PUT 등 다양한 Request Method 제공
 **/
public interface NetRetrofitService {

    /**
     * 응답형식 -> jsonObject 를 담는 ArrayList
     * GET 메소드
     **/
    @GET("users/{users}/repos")
    Call<ArrayList<JsonObject>>
    getListRepos(@Path("users") String id);


}
