package com.jroomstudio.smartbookmarkeditor.data.member.source;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;

public interface MemberDataSource {

    interface LoadDataCallback {
        void onDataLoaded(Member member);
        void onDataNotAvailable();
    }

    // 로그인 후 정보 가져오기
    void getMember(@NonNull String email,@NonNull String password, @NonNull LoadDataCallback callback);

    // 회원탈퇴
    void deleteMember(@NonNull String email, @NonNull String password);

    // 다크테마 없데이트
    void updateDarkTheme(@NonNull String email, @NonNull String password, boolean darkTheme);

    // 푸쉬알림 업데이트
    void updatePushNotice(@NonNull String email, @NonNull String password, boolean pushNotice);

    // 로그인 , 로그아웃 업데이트
    void updateLoginStatus(@NonNull String email, @NonNull String password, boolean loginStatus);

    // 최초 로그인 후 저장
    void saveMember(@NonNull Member member);

    void refresh();

}
