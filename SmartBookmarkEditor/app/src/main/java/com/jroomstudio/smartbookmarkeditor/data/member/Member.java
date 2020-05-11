package com.jroomstudio.smartbookmarkeditor.data.member;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.common.base.Objects;

/**
 * 회원가입을 위한 Member 불변 모델클래스
 **/
@Entity(tableName = "member")
public class Member {

    /**
     * 원격 데이터베이스에서 id로 사용될 중복될 수 없는 값
     **/
    @NonNull
    @ColumnInfo(name = "email")
    private final String mEmail;

    /**
     * 사용자의 이름
     **/
    @NonNull
    @ColumnInfo(name = "name")
    private final String mName;

    /**
     * 사용자의 이미지 url
     **/
    @NonNull
    @ColumnInfo(name = "photo_url")
    private final String mPhotoUrl;

    /**
     * 자동으로 생성 될 사용자의 암호화될 패스워드
     * 구글, 페이스북 으로부터 전달받은 id 가 저장된다.
     **/
    @NonNull
    @ColumnInfo(name = "auto_password")
    private final String mAutoPassword;

    /**
     * 다크테마 상태
     **/
    @ColumnInfo(name = "dark_theme")
    private final boolean mDarkTheme;

    /**
     * 푸쉬 알림 상태
     **/
    @ColumnInfo(name = "push_notice")
    private final boolean mPushNotice;

    /**
     * 푸쉬 알림 상태
     **/
    @ColumnInfo(name = "login_satus")
    private final boolean mLoginStatus;

    /**
     * 로그인 타입 (1 페이스북 / 0 구글 )
     **/
    @ColumnInfo(name = "login_type")
    private final int mLoginType;



    /**
     * 생성자
     **/
    public Member(@NonNull String email,
                  @NonNull String name,
                  @NonNull String photoUrl,
                  @NonNull String autoPassword,
                  boolean darkTheme,
                  boolean pushNotice,
                  boolean loginStatus,
                  int loginType){
        this.mEmail = email;
        this.mName = name;
        this.mPhotoUrl = photoUrl;
        this.mAutoPassword = autoPassword;
        this.mDarkTheme = darkTheme;
        this.mPushNotice = pushNotice;
        this.mLoginStatus = loginStatus;
        this.mLoginType = loginType;
    }

    @NonNull
    public String getEmail() { return mEmail; }

    @NonNull
    public String getName() { return mName; }

    @NonNull
    public String getPhotoUrl() { return mPhotoUrl; }

    @NonNull
    public String getAutoPassword(){ return mAutoPassword; }

    public boolean isDarkTheme(){ return mDarkTheme; }

    public boolean isPushNotice(){ return mPushNotice; }

    public boolean isLoginStatus(){ return mLoginStatus; }

    public int getLoginType(){ return mLoginType; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Member member = (Member) obj;
        return  Objects.equal(mEmail, member.getEmail()) &&
                Objects.equal(mName, member.getName()) &&
                Objects.equal(mPhotoUrl, member.getPhotoUrl()) &&
                Objects.equal(mAutoPassword, member.getAutoPassword()) &&
                Objects.equal(mDarkTheme, member.isDarkTheme()) &&
                Objects.equal(mPushNotice, member.isPushNotice()) &&
                Objects.equal(mLoginStatus, member.isLoginStatus()) &&
                Objects.equal(mLoginType, member.getLoginType());
    }

    @Override
    public int hashCode() { return Objects.hashCode(mEmail,mName); }

    @NonNull
    @Override
    public String toString() {
        return mEmail+"\n"+
                mName+"\n" +
                mPhotoUrl+"\n" +
                mAutoPassword+"\n" +
                "DarkTheme : "+mDarkTheme+"\n" +
                "Notice : "+mPushNotice+"\n" +
                "Login Status : "+mLoginStatus+"\n" +
                "LoginType : "+mLoginType+"\n"; }

}
