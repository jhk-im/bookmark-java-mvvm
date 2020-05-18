package com.jroomstudio.smartbookmarkeditor.data.member.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.data.member.JwtToken;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemberRepository implements MemberDataSource {

    // MemberRepository 싱글턴 인스턴스
    private static MemberRepository INSTANCE = null;

    // 로컬 데이터베이스
    //private final MemberDataSource mLocalDataSource;

    // 원격 데이터베이스
    private final MemberDataSource mRemoteDataSource;
    /**
     * 로컬과 원격 사이에서 중간역할
     * 데이터의 변경이 없을 시 이곳에서 꺼내 쓰게된다.
     **/
    Map<String, Member> mCached;
    //Member mMember;

    // 데이터가 변경이 있을 때 true 를 표시하여 업데이트 한다.
    //boolean mMemberUpdate = false;
    boolean mCacheDirty = false;

    // 다이렉트 인스턴스 방지
    private MemberRepository(//@NonNull MemberDataSource localDataSource,
                             @NonNull MemberDataSource remoteDataSource){
        //mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }

    /**
     * 싱글턴 인스턴스를 생성
     *
     * @param remoteDataSource 원격 데이터 소스
     * @return the {@link MemberRepository} instance
     */
    public static MemberRepository getInstance(//MemberDataSource localDataSource,
                                               MemberDataSource remoteDataSource){
        if(INSTANCE == null){
            INSTANCE = new MemberRepository(//localDataSource,
                    remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    // 캐시메모리에서 객체찾기
    @Nullable
    private Member getMemberWithEmail(@NonNull String email) {
        checkNotNull(email);
        if (mCached == null) {
            return null;
        } else {
            return mCached.get(email);
        }
    }

    // 캐시메모리 업데이트
    private void refreshCache(Member member) {
        if (mCached == null) {
            mCached = new LinkedHashMap<>();
        }
        mCached.clear();
        mCached.put(member.getEmail(),member);
        mCacheDirty = false;
    }

    // 이메일과 패스워드로 토큰 받기
    @Override
    public void getToken(@NonNull String email,
                          @NonNull String password,
                          int loginType,
                          @NonNull LoadTokenCallback callback) {
        checkNotNull(email);
        checkNotNull(password);
        checkNotNull(callback);

        // 로컬에 저장된 email 과 password 로 원격에 로그인한다.
        mRemoteDataSource.getToken(email, password,loginType,
                new LoadTokenCallback() {
                    @Override
                    public void onTokenLoaded(JwtToken token) {
                        // 멤버 정보 가져오기 성공
                        callback.onTokenLoaded(token);
                    }

                    @Override
                    public void onTokenNotAvailable() {
                        // 데이터가 없다면
                        // 1. 첫 회원가입
                        callback.onTokenNotAvailable();
                    }

                    @Override
                    public void onLoginFailed() {
                        // 비밀번호가 다를경우
                        // 이미 가입되어있는 아이디이다.
                        callback.onLoginFailed();
                    }
                });
    }

    // 회원 탈퇴
    @Override
    public void deleteMember(@NonNull String email, @NonNull String password) {
        checkNotNull(email);
        checkNotNull(password);
        //mLocalDataSource.deleteMember(email,password);
        mRemoteDataSource.deleteMember(email,password);
        // 멤버 정보 가져오기 성공
        if(mCached == null){
            mCached = new LinkedHashMap<>();
        }
        mCached.clear();
    }

    // 다크테마 변경
    @Override
    public void updateDarkTheme(@NonNull String email, @NonNull String password, boolean darkTheme) {
        checkNotNull(email);
        checkNotNull(password);
        //mLocalDataSource.updateDarkTheme("","",darkTheme);
        mRemoteDataSource.updateDarkTheme(email,password,darkTheme);

        Member cachedMember = getMemberWithEmail(email);
        Member updateMember = new Member(
                email, cachedMember.getName(),
                cachedMember.getPhotoUrl(), password, darkTheme,
                cachedMember.isPushNotice(), cachedMember.getLoginType(),cachedMember.isLoginStatus());

        // 멤버 정보 가져오기 성공
        if(mCached == null){
            mCached = new LinkedHashMap<>();
        }
        mCached.put(email,updateMember);

    }

    // 푸쉬알림 변경
    @Override
    public void updatePushNotice(@NonNull String email, @NonNull String password, boolean pushNotice) {
        checkNotNull(email);
        checkNotNull(password);
        mRemoteDataSource.updatePushNotice(email,password,pushNotice);

        Member cachedMember = getMemberWithEmail(email);
        Member updateMember = new Member(
                email, cachedMember.getName(),
                cachedMember.getPhotoUrl(), password, cachedMember.isDarkTheme(),
                pushNotice, cachedMember.getLoginType(),cachedMember.isLoginStatus()
        );

        // 멤버 정보 가져오기 성공
        if(mCached == null){
            mCached = new LinkedHashMap<>();
        }
        mCached.put(email,updateMember);

    }

    // 로그인 로그아웃 상태 변경
    @Override
    public void updateLoginStatus(@NonNull String email, @NonNull String password, boolean loginStatus) {
        checkNotNull(email);
        checkNotNull(password);
        //mLocalDataSource.updateLoginStatus("","",loginStatus);
        mRemoteDataSource.updateLoginStatus(email,password,loginStatus);

        Member cachedMember = getMemberWithEmail(email);
        Member updateMember = new Member(
                email, cachedMember.getName(),
                cachedMember.getPhotoUrl(), password, cachedMember.isDarkTheme(),
                cachedMember.isPushNotice(),cachedMember.getLoginType(),loginStatus);

        // 멤버 정보 가져오기 성공
        if(mCached == null){
            mCached = new LinkedHashMap<>();
        }
        mCached.put(email,updateMember);
    }

    // 첫 회원가입
    @Override
    public void saveMember(@NonNull Member member) {
        checkNotNull(member);
        //mLocalDataSource.saveMember(member);
        mRemoteDataSource.saveMember(member);
        // 멤버 정보 가져오기 성공
        if(mCached == null){
            mCached = new LinkedHashMap<>();
        }
        mCached.put(member.getEmail(),member);
    }

    // 무엇인가 변경되었음을 알린다.
    @Override
    public void refresh() {
        mCacheDirty = true;
    }


}