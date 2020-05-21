package com.jroomstudio.smartbookmarkeditor.data.member;


import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

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
public class MemberRemoteRepository implements MemberDataSource {

    // 싱글턴 인스턴스
    private static MemberRemoteRepository INSTANCE;

    // 레트로핏 인스턴스
    // private NetRetrofit mNetRetrofit;
    private NetRetrofitService mNetRetrofitService;

    // 쓰레드
    private AppExecutors mAppExecutors;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 통신 레이턴시
    //private static final int LATENCY_IN_MILLIS = 2000;

    // 다이렉트 인스턴스 방지
    private MemberRemoteRepository(@NonNull AppExecutors appExecutors,
                                   @NonNull SharedPreferences sharedPreferences){
        mAppExecutors = appExecutors;
        spActStatus = sharedPreferences;
        // 레트로핏 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetRetrofitService.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNetRetrofitService = retrofit.create(NetRetrofitService.class);
    }

    // 싱글턴 인스턴스 생성
    public static MemberRemoteRepository getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull SharedPreferences sharedPreferences){
        if(INSTANCE == null){
            INSTANCE = new MemberRemoteRepository(appExecutors,sharedPreferences);
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
                    loginType
            );
            mNetRetrofitService.postTokenCallback("application/json",member)
                    .enqueue(new Callback<JsonWebToken>() {
                        @Override
                        public void onResponse(Call<JsonWebToken> call, Response<JsonWebToken> response) {
                            // JWT 콜백
                            Log.e("Get JWT",response.code()+"");
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
                        public void onFailure(Call<JsonWebToken> call, Throwable t) {
                            // 아무런 응답이 없을 시
                            // 등록된 회원이 없다고 판단
                            //Log.e("jwt 콜백","실패");
                            Log.e("Get JWT onFailure",t.getMessage());
                            callback.onTokenNotAvailable();
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void refreshToken(@NonNull String email, @NonNull String password,
                             @NonNull LoadTokenCallback callback) {
        Runnable runnable = () -> {
            Member member = new Member(
                    email,"","",
                    password,false,false,
                    0
            );
            mNetRetrofitService.refreshTokenCallback("application/json",member)
                    .enqueue(new Callback<JsonWebToken>() {
                        @Override
                        public void onResponse(Call<JsonWebToken> call, Response<JsonWebToken> response) {
                            // JWT 콜백
                            //Log.e("jwt 리프래쉬","성공");
                            Log.e("JWT Refresh",response.code()+"");
                            if(response.code()==200){
                                // 로그인 성공
                                // 토큰을 전달하여 저장한다.
                                callback.onTokenLoaded(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonWebToken> call, Throwable t) {
                            // 사용안함
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    // 회원 데이터 가져오기
    @Override
    public void getData(@NonNull Member member,
                        @NonNull LoadDataCallback callback) {
        Runnable runnable = () -> {
            String jwt = spActStatus.getString("jwt","");
            mNetRetrofitService.postDataCallback("Bearer "+jwt, "application/json", member)
                    .enqueue(new Callback<Member>() {
                        @Override
                        public void onResponse(Call<Member> call, Response<Member> response) {
                            //Log.e("body", response.body()+"");
                            Log.e("Get user data",response.code()+"");
                            callback.onDataLoaded(response.body());
                        }

                        @Override
                        public void onFailure(Call<Member> call, Throwable t) {
                            Log.e("Get user data onFailure",t.getMessage());
                            callback.onDataNotAvailable();
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    // 회원탈퇴
    @Override
    public void deleteMember(@NonNull String email, @NonNull String password) {

    }
    // 유저 정보 업데이트
    @Override
    public void updateUserData(@NonNull Member member, @NonNull UpdateDataCallback callback) {
        Runnable runnable = () -> {
            String jwt = spActStatus.getString("jwt","");
            mNetRetrofitService.updateDataCallback("Bearer "+jwt,"application/json",member)
                    .enqueue(new Callback<Member>() {
                        @Override
                        public void onResponse(Call<Member> call, Response<Member> response) {
                            if(response.code()==200){
                                //Log.e("header", response.headers()+"");
                                //Log.e("body", response.body()+"");
                                Log.e("Update Data",response.code()+"");
                                callback.updateCompleted(response.body());
                            } else if(response.code()==401){
                                // 실패시 jwt 재발급 받고 다시시도
                                callback.tokenExpiration();
                            }
                        }

                        @Override
                        public void onFailure(Call<Member> call, Throwable t) {
                            Log.e("Update onFailure",t.getMessage());
                            // 실패시 jwt 재발급 받고 다시시도
                            callback.tokenExpiration();
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }


    // 첫 회원가입
    @Override
    public void saveMember(@NonNull Member member) {

        Runnable runnable = () -> {
            mNetRetrofitService.postData("application/json",member)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            //Log.e("response", response.toString());
                            //Log.e("body", response.raw().body()+"");
                            Log.e("Insert User", response.code()+"");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("Insert User onFailure",t.getMessage());
                        }
                    });
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

}
