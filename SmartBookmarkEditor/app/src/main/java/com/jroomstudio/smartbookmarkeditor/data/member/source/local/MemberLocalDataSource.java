package com.jroomstudio.smartbookmarkeditor.data.member.source.local;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberDataSource;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Member 로컬 데이터 소스
**/
public class MemberLocalDataSource implements MemberDataSource {

    // 싱글턴 인스턴스
    private static volatile MemberLocalDataSource INSTANCE;

    // 데이터베이스 액세스 인터페이스
    private MemberDao mMemberDao;

    // 쓰레드
    private AppExecutors mAppExecutors;

    // 다이렉트 인스턴스 방지
    private MemberLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull MemberDao memberDao){
        mAppExecutors = appExecutors;
        mMemberDao = memberDao;
    }

    // 싱글턴 인스턴스 생성
    public static MemberLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull MemberDao memberDao){
        if(INSTANCE == null){
            synchronized (MemberLocalDataSource.class){
                if(INSTANCE == null){
                    INSTANCE = new MemberLocalDataSource(appExecutors,memberDao);
                }
            }
        }
        return INSTANCE;
    }

    // 로컬에서 Member 객체 찾기
    @Override
    public void getToken(@NonNull String email, @NonNull String password,
                          int loginType,@NonNull LoadTokenCallback callback) {
        checkNotNull(email);
        checkNotNull(password);
        checkNotNull(callback);
        Runnable runnable = () -> {
            final Member member = mMemberDao.getMemberByEmail(email);
            mAppExecutors.getMainThread().execute(() ->{
                if(member != null){
                    //
                }else{
                    callback.onTokenNotAvailable();
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void refreshToken(@NonNull String email, @NonNull String password,
                             @NonNull LoadTokenCallback callback) {

    }

    @Override
    public void getData(@NonNull Member member, @NonNull LoadDataCallback callback) {

    }

    // Member 삭제
    @Override
    public void deleteMember(@NonNull String email, @NonNull String password) {
        checkNotNull(email);
        checkNotNull(password);
        Runnable runnable = () -> { mMemberDao.deleteMember(); };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // Member update
    @Override
    public void updateUserData(@NonNull Member member, @NonNull UpdateDataCallback callback) {

    }


    // Member 저장
    @Override
    public void saveMember(@NonNull Member member) {
        checkNotNull(member);
        Runnable runnable = () -> { mMemberDao.insertMember(member); };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void refresh() {
        // 사용하지 않음
    }
}
