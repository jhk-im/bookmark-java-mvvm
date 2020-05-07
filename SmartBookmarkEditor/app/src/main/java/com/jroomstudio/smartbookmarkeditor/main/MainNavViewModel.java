package com.jroomstudio.smartbookmarkeditor.main;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeDataSource;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDataSource;

import java.util.List;


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

    // Home 버튼과 Note 버튼 구분
    public ObservableBoolean isHomeSelected = new ObservableBoolean();

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

    /**
     * Main Nav Activity ViewModel 생성자
     * @param noticeLocalDataSource - 알림 객체 로컬 데이터 액세스
     **/
    public MainNavViewModel(NoticeLocalDataSource noticeLocalDataSource,
                            MainNavNavigator navNavigator){
        mNoticeLocalDataSource = noticeLocalDataSource;
        mNavigator = navNavigator;
    }


    /**
     * 첫 시작점
     **/
    public void onLoaded(){
        // 첫 시작은 항상 Home 으로
        isHomeSelected.set(true);
        notifyPropertyChanged(BR._all);

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
     * 클릭메소드
     **/
    public void onClickLogin(){
        if(mNavigator!=null){
            mNavigator.onClickLogin();
        }
    }

    // 홈버튼 클릭
    public void onClickHome(){
        isHomeSelected.set(true);
        notifyPropertyChanged(BR._all);
        if(mNavigator!=null){
            mNavigator.onClickHome();
        }
    }
    // 노트버튼 클릭
    public void onClickNote(){
        isHomeSelected.set(false);
        notifyPropertyChanged(BR._all);
        if(mNavigator!=null){
            mNavigator.onClickNote();
        }
    }

    // 버튼 재설정
    void replaceHomeButton(boolean status){
        isHomeSelected.set(status);
        notifyPropertyChanged(BR._all);
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
