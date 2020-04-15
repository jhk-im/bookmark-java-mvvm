package com.jroomstudio.smartbookmarkeditor.util;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * NetRetrofit.class
 * - Retrofit 바디
 * - Service Interface 를 이용하여 Retrofit 객체를 생성하는 클래스
 *  - NetRetrofit 싱글톤
 *  - baseUrl 지정
 *  - 파싱을 위한 컨버터 지정
 **/
public class NetRetrofit {

    // NetRetrofit 싱글톤
    private static NetRetrofit INSTANCE = null;

    // 연결할 URL
    public static String mBaseUrl;

    // 인스턴스 리턴
    public static NetRetrofit getInstance(String url) {
        if(INSTANCE == null){
            INSTANCE = new NetRetrofit(url);
        }
        return INSTANCE;
    }

    //다이렉트 인스턴스 방지
    private NetRetrofit(@NonNull String baseUrl){
        mBaseUrl = baseUrl;
    }

    // http 클라이언트 생성
    // OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build();

    // 레트로핏 생성
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(client) --> 클라이언트 추가
            .build();

    NetRetrofitService service = retrofit.create(NetRetrofitService.class);

    public NetRetrofitService getService() {
        return service;
    }
}
