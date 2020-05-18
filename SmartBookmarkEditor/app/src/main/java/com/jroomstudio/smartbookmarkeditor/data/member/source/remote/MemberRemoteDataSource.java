package com.jroomstudio.smartbookmarkeditor.data.member.source.remote;


import android.util.Log;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.member.JwtToken;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberDataSource;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;
import com.jroomstudio.smartbookmarkeditor.util.NetRetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 회원정보 원격 데이터 소스
 **/
public class MemberRemoteDataSource implements MemberDataSource {

    // 싱글턴 인스턴스
    private static MemberRemoteDataSource INSTANCE;

    // 레트로핏 인스턴스
    // private NetRetrofit mNetRetrofit;
    private NetRetrofitService mNetRetrofitService;

    // 쓰레드
    private AppExecutors mAppExecutors;


    // 통신 레이턴시
    //private static final int LATENCY_IN_MILLIS = 2000;

    // 다이렉트 인스턴스 방지
    private MemberRemoteDataSource(@NonNull AppExecutors appExecutors){
        mAppExecutors = appExecutors;
        // 레트로핏 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetRetrofitService.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNetRetrofitService = retrofit.create(NetRetrofitService.class);
    }

    // 싱글턴 인스턴스 생성
    public static MemberRemoteDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null){
            INSTANCE = new MemberRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    // 로그인하고 jwt 토큰발행 -> 토큰으로 유저 데이터 가져오기
    @Override
    public void getToken(@NonNull String email, @NonNull String password,
                          int loginType,@NonNull LoadTokenCallback callback) {
        Runnable runnable = () -> {
            Member member = new Member(
                    email,"","",
                    password,false,false,
                    loginType,false
            );
            mNetRetrofitService.postDataCallback("application/json",member)
                    .enqueue(new Callback<JwtToken>() {
                        @Override
                        public void onResponse(Call<JwtToken> call, Response<JwtToken> response) {
                            // JWT 콜백
                            Log.e("message",response.code()+"");
                            if(response.code()==200){
                                // 로그인 성공
                                // 토큰을 전달하여 저장한다.
                                callback.onTokenLoaded(response.body());
                            } else if(response.code()==401){
                                // 비밀번호 틀림
                                // 이미 다른 방법으로 가입된 이메일
                                callback.onLoginFailed();
                                //Log.e("message",response.body().getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<JwtToken> call, Throwable t) {
                            // 아무런 응답이 없을 시
                            // 등록된 회원이 없다고 판단
                            Log.e("onFailure",t.getMessage());
                            callback.onTokenNotAvailable();
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    // 회원탈퇴
    @Override
    public void deleteMember(@NonNull String email, @NonNull String password) {

    }

    // 다크테마 변경
    @Override
    public void updateDarkTheme(@NonNull String email, @NonNull String password,
                                boolean darkTheme) {

    }

    // 푸시알림 변경
    @Override
    public void updatePushNotice(@NonNull String email, @NonNull String password,
                                 boolean pushNotice) {

    }

    // 로그인 상태 변경 (로그인, 로그아웃)
    @Override
    public void updateLoginStatus(@NonNull String email, @NonNull String password,
                                  boolean loginStatus) {

    }

    // 첫 회원가입
    @Override
    public void saveMember(@NonNull Member member) {

        Runnable runnable = () -> {
            mNetRetrofitService.postData("application/json",member)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.e("response", response.toString());
                                Log.e("body", response.raw().body()+"");
                                Log.e("header", response.headers()+"");
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("onFailure",t.getMessage());
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void refresh() {
        // 사용하지 않음
    }
}
