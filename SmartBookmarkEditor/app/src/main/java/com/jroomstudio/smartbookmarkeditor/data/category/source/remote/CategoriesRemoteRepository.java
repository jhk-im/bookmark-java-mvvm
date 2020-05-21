package com.jroomstudio.smartbookmarkeditor.data.category.source.remote;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.member.JsonWebToken;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;
import com.jroomstudio.smartbookmarkeditor.util.NetRetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 네트워크 지연시간 시뮬레이션을 테스트하는 데이터소스 구현
 **/
public class CategoriesRemoteRepository implements CategoriesRemoteDataSource {

    // INSTANCE
    private static CategoriesRemoteRepository INSTANCE;
    // 레트로핏 인스턴스
    // private NetRetrofit mNetRetrofit;
    private NetRetrofitService mNetRetrofitService;

    // 쓰레드
    private AppExecutors mAppExecutors;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    // 다이렉트 인스턴스 방지
    private CategoriesRemoteRepository(@NonNull AppExecutors appExecutors,
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

    /**
     * - CategoriesRemoteDataSource 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static CategoriesRemoteRepository getInstance(@NonNull AppExecutors appExecutors,
                                                         @NonNull SharedPreferences sharedPreferences) {
        if(INSTANCE == null){
            INSTANCE = new CategoriesRemoteRepository(appExecutors,sharedPreferences);
        }
        return INSTANCE;
    }


    @Override
    public void refreshToken(@NonNull RefreshTokenCallback callback) {
        Runnable runnable = () ->{
            String email = spActStatus.getString("member_email","");
            String password = spActStatus.getString("auto_password","");
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
                                callback.onRefreshTokenCallback(response.body());
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

    @Override
    public void getCategories(@NonNull Category category, @NonNull LoadCategoriesCallback callback) {
        Runnable runnable = () ->{
            String jwt = spActStatus.getString("jwt","");
            //mNetRetrofitService.
        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void saveCategory(@NonNull Category category, @NonNull UpdateCallback callback) {
        Runnable runnable = () ->{

        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void deleteCategory(@NonNull String id, @NonNull UpdateCallback callback) {
        Runnable runnable = () ->{

        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void updateCategory(@NonNull Category category, @NonNull GetCategoryCallback callback) {
        Runnable runnable = () ->{

        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }

    @Override
    public void updateCategories(@NonNull List<Category> categories, @NonNull UpdateCallback callback) {
        Runnable runnable = () ->{

        };
        mAppExecutors.getNetworkIO().execute(runnable);
    }
}
