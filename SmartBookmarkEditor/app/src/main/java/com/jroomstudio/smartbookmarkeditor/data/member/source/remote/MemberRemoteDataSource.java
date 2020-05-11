package com.jroomstudio.smartbookmarkeditor.data.member.source.remote;


import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberDataSource;

/**
 * 회원정보 원격 데이터 소스
 **/
public class MemberRemoteDataSource implements MemberDataSource {

    // 싱글턴 인스턴스
    private static MemberRemoteDataSource INSTANCE;

    // 통신 레이턴시
    private static final int LATENCY_IN_MILLIS = 2000;

    // 다이렉트 인스턴스 방지
    private MemberRemoteDataSource(){}

    // 싱글턴 인스턴스 생성
    public static MemberRemoteDataSource getInstance(){
        if(INSTANCE == null){
            INSTANCE = new MemberRemoteDataSource();
        }
        return INSTANCE;
    }

    // 멤버 객체 가져오기
    @Override
    public void getMember(@NonNull String email, @NonNull String password,
                          @NonNull LoadDataCallback callback) {

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

    }

    @Override
    public void refresh() {
        // 사용하지 않음
    }
}
