package com.jroomstudio.smartbookmarkeditor.data.member.source;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.member.JwtToken;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;

public interface MemberDataSource {

    interface LoadDataCallback {
        void onDataLoaded(Member member);
        void onDataNotAvailable();
    }
    interface LoadTokenCallback {
        void onTokenLoaded(JwtToken token);
        void onTokenNotAvailable();
        void onLoginFailed();
    }
    interface UpdateDataCallback {
        void updateCompleted(Member member);
        void tokenExpiration();
    }

    // 로그인 토큰 가져오기
    void getToken(@NonNull String email, @NonNull String password,
                   int loginType,@NonNull LoadTokenCallback callback);

    // 토큰만료시 재발급
    void refreshToken(@NonNull String email, @NonNull String password,
                      @NonNull LoadTokenCallback callback);


    // 회원 데이터 가져오기
    void getData(@NonNull Member member,
                 @NonNull LoadDataCallback callback);

    // 회원탈퇴
    void deleteMember(@NonNull String email, @NonNull String password);

    // 유저 데이터 없데이트
    void updateUserData(@NonNull Member member, @NonNull UpdateDataCallback callback);

    // 최초 로그인 후 저장
    void saveMember(@NonNull Member member);

    void refresh();

}
