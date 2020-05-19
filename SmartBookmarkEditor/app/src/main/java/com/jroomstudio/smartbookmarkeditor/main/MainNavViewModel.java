package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.member.JwtToken;
import com.jroomstudio.smartbookmarkeditor.data.member.Member;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberDataSource;
import com.jroomstudio.smartbookmarkeditor.data.member.source.MemberRepository;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeDataSource;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDataSource;

import java.util.List;
import java.util.Objects;


/**
 * - 메인 액티비티의 navigation container 에서 사용할 데이터를 관찰
 *
 * {@link BaseObservable}
 * - 속성이 변경될 때 알림을 받는 리스너 등록 메커니즘을 구현
 **/
public class MainNavViewModel extends BaseObservable {

    /**
     * Observable
     * 해당 뷰모델과 연결된 액티비티의 UI 를 관찰하고 컨트롤한다.
     **/

    // 읽지않은 알림 카운트
    public ObservableField<String> notReadNoticeCount = new ObservableField<>();
    public ObservableBoolean isNotReadCount = new ObservableBoolean();

    // 읽지않은 알림이 있는지 여부
    @Bindable
    public boolean isNotReadCountVisible() {
        return isNotReadCount.get();
    }


    // 알림 객체 로컬 데이터 소스
    private NoticeLocalDataSource mNoticeLocalDataSource;

    // 액티비티 네비게이터
    private MainNavNavigator mNavigator;

    // 회원 원격 데이터베이스
    private MemberRepository mMemberRepository;

    // 액티비티 상태저장 Shared Preferences
    private SharedPreferences spActStatus;

    private Context mContext;

    /**
     * Main Nav Activity ViewModel 생성자
     * @param noticeLocalDataSource - 알림 객체 로컬 데이터 액세스
     **/
    public MainNavViewModel(NoticeLocalDataSource noticeLocalDataSource,
                            MainNavNavigator navNavigator,
                            MemberRepository memberRepository,
                            SharedPreferences sharedPreferences,
                            Context context){
        mNoticeLocalDataSource = noticeLocalDataSource;
        mNavigator = navNavigator;
        mMemberRepository = memberRepository;
        spActStatus = sharedPreferences;
        mContext = context;
    }

    /**
     * 첫 시작점
     **/
    public void onLoaded(){
        // notification 상태
        setupNoticeLocalDataSource();
    }

    /**
     * notifications 데이터베이스 셋팅
     **/
    private void setupNoticeLocalDataSource(){
        // 읽지않은 알림 데이터 가져오기
        mNoticeLocalDataSource.getNotifications(
                false, new NoticeDataSource.LoadNotificationsCallback() {
                    @Override
                    public void onNotificationsLoaded(List<Notice> notifications) {
                        // 비어있지 않은경우
                        Log.e("notice",notifications.toString());
                        notReadNoticeCount.set(String.valueOf(notifications.size()));
                        isNotReadCount.set(true);
                        notifyPropertyChanged(BR._all);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        // 비어있는경우
                        Log.e("notice","DataNotAvailable");
                        notReadNoticeCount.set("");
                        isNotReadCount.set(false);
                        notifyPropertyChanged(BR._all);
                    }
                });
    }

    /**
     * 로그인 하여 jwt 토큰을 가져온다.
     * Main Activity 에서 호출
     **/
    void getTokenRemoteRepository(){
        // 로그인하고 토큰 받아오기
        mMemberRepository.getToken(
                Objects.requireNonNull(spActStatus.getString("member_email", "")),
                Objects.requireNonNull(spActStatus.getString("auto_password", "")),
                spActStatus.getInt("login_type", 0),
                new MemberDataSource.LoadTokenCallback() {
                    @Override
                    public void onTokenLoaded(JwtToken token) {
                        // 토큰 저장
                        SharedPreferences.Editor editor = spActStatus.edit();
                        editor.putBoolean("login_status",true);
                        editor.putString("jwt",token.getJwt());
                        editor.apply();
                        //mNavigator.loginCompleted();
                        Toast.makeText(mContext.getApplicationContext(),
                                "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        // 해당 토큰으로 유저 정보를 받아온다.
                        getUserDataRepository();
                    }

                    @Override
                    public void onTokenNotAvailable() {
                        // 토큰이 오지않으면
                        // 회원가입이라고 판단하여 데이터베이스에 저장
                        Member newMember = new Member(
                                Objects.requireNonNull(spActStatus.getString("member_email", "")),
                                Objects.requireNonNull(spActStatus.getString("member_name", "")),
                                Objects.requireNonNull(spActStatus.getString("photo_url", "")),
                                Objects.requireNonNull(spActStatus.getString("auto_password", "")),
                                true,
                                true,
                                spActStatus.getInt("login_type", 0),
                                true
                        );
                        // 회원가입
                        mMemberRepository.saveMember(newMember);
                        SharedPreferences.Editor editor = spActStatus.edit();
                        editor.putBoolean("login_status",false);
                        editor.putString("jwt","");
                        editor.apply();
                        // 회원가입을 한 후 토큰 발행받기
                        getTokenRemoteRepository();
                    }

                    @Override
                    public void onLoginFailed() {
                        // 회원가입할수 없는 유저
                        // ex ) 구글로 이미 가입한 이메일주소와 같은걸로 페이스북 회원가입 시도할 때
                        // 로그아웃 처리하고 리셋
                        // 로그아웃
                        mNavigator.loginOut(false);
                    }
                });
    }

    /**
     * 로그인 후 데이터 가져오기
     **/
    private void getUserDataRepository(){
        Member postMember = new Member(
                Objects.requireNonNull(spActStatus.getString("member_email", "")),
                Objects.requireNonNull(spActStatus.getString("member_name", "")),
                Objects.requireNonNull(spActStatus.getString("photo_url", "")),
                Objects.requireNonNull(spActStatus.getString("auto_password", "")),
                true,
                true,
                spActStatus.getInt("login_type", 0),
                true
        );
        mMemberRepository.getData(postMember,
                new MemberDataSource.LoadDataCallback() {

                    @Override
                    public void onDataLoaded(Member member) {
                        // 로그인 성공
                        mNavigator.loginCompleted(member,true);
                    }

                    @Override
                    public void onDataNotAvailable() {

                    }
                });
    }

    /**
     * 유저 데이터 업데이트
     **/
    void updateUserDataRepository(boolean darkTheme,
                                  boolean pushNotice,
                                  boolean loginStatus,
                                  boolean isDarkTheme){
        Member updateMember = new Member(
                Objects.requireNonNull(spActStatus.getString("member_email", "")),
                Objects.requireNonNull(spActStatus.getString("member_name", "")),
                Objects.requireNonNull(spActStatus.getString("photo_url", "")),
                Objects.requireNonNull(spActStatus.getString("auto_password", "")),
                darkTheme,
                pushNotice,
                spActStatus.getInt("login_type", 0),
                loginStatus
        );
        mMemberRepository.updateUserData(updateMember, new MemberDataSource.UpdateDataCallback() {
            @Override
            public void updateCompleted(Member member) {
                Log.e("update","성공");
                //Log.e("member",member.toString()+"");
                mNavigator.updateRemoteData(member,isDarkTheme);
            }

            @Override
            public void tokenExpiration() {
                Log.e("update","실패");
                refreshTokenRemoteRepository(darkTheme,pushNotice,loginStatus,isDarkTheme);
            }
        });
    }

    /**
     * 토큰 재발급
    * */
    private void refreshTokenRemoteRepository(boolean darkTheme,
                                              boolean pushNotice,
                                              boolean loginStatus,
                                              boolean isDarkTheme){
        mMemberRepository.refreshToken(
                Objects.requireNonNull(spActStatus.getString("member_email", "")),
                Objects.requireNonNull(spActStatus.getString("auto_password", "")),
                new MemberDataSource.LoadTokenCallback() {
                    @Override
                    public void onTokenLoaded(JwtToken token) {
                        Log.e("Token refresh","성공");
                        // 토큰저장
                        SharedPreferences.Editor editor = spActStatus.edit();
                        editor.putString("jwt",token.getJwt());
                        editor.apply();
                        // 다시실행
                        updateUserDataRepository(darkTheme,pushNotice,loginStatus,isDarkTheme);
                    }

                    @Override
                    public void onTokenNotAvailable() {
                        // 사용안함
                    }

                    @Override
                    public void onLoginFailed() {
                        // 사용안함
                    }
                });
    }

    /**
     * 클릭메소드
     **/
    public void onClickLogin(){
        if(mNavigator!=null){
            mNavigator.onClickLogin();
        }
    }

    // 홈버튼 클릭
    public void onClickHome(){
        if(mNavigator!=null){
            mNavigator.onClickHome();
        }
    }

    // 알림버튼 클릭
    public void onClickNotice(){
        if(mNavigator!=null){
            mNavigator.onClickNotice();
        }
    }

    // 개인정보 버튼 클릭
    public void onClickPIPP(){
        if(mNavigator!=null){
            mNavigator.onClickPIPP();
        }
    }
    // 오픈소스 버튼 클릭
    public void onClickOSL(){
        if(mNavigator!=null){
            mNavigator.onClickOSL();
        }
    }

}
