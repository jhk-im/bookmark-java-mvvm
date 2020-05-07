package com.jroomstudio.smartbookmarkeditor.notice;


import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.jroomstudio.smartbookmarkeditor.BR;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeDataSource;
import com.jroomstudio.smartbookmarkeditor.data.notice.NoticeLocalDataSource;

import java.util.List;

/**
 * Notice Activity 에서 사용할 데이터가 노출된다.
**/
public class NoticeViewModel extends BaseObservable {

    /**
     * Observable
     * 해당 뷰모델과 연결된 액티비티의 UI 를 관찰하고 컨트롤한다.
     **/
    public final ObservableList<Notice> noticeList = new ObservableArrayList<>();

    /**
     * - 해당 뷰모델과 연결될 액티비티,프래그먼트 의 Context
     * - leak 을 피하려면 응용 프로그램 context 여야 한다.
     **/
    private Context mContext;

    // 액티비티 네비게이터
    private NoticeNavigator mNavigator;
    // 네비게이터 셋팅
    public void setNavigator(NoticeNavigator navigator) { mNavigator = navigator; }
    // 네비게이터 null - notice activity onDestroy 에서 호출
    public void onActivityDestroyed(){ mNavigator = null; }

    // 알림 객체 로컬 데이터 소스
    private NoticeLocalDataSource mNoticeLocalDataSource;

    /**
     * Notice Activity ViewModel 생성자
     * @param noticeLocalDataSource - 알림 객체 로컬 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public NoticeViewModel(NoticeLocalDataSource noticeLocalDataSource, Context context){
        mNoticeLocalDataSource = noticeLocalDataSource;
        mContext = context;
    }

    // 프래그먼트 onResume 에서 호출
    void start() { loadNotifications(); }

    private void loadNotifications()
    {
        // 알림 객체 가져오기
        mNoticeLocalDataSource.getNotifications(new NoticeDataSource.LoadNotificationsCallback() {
            @Override
            public void onNotificationsLoaded(List<Notice> notifications) {
                // 옵저버블 리스트에 추가
                // 카테고리 position 순서대로 정렬
                noticeList.clear();
                noticeList.addAll(notifications);
                notifyPropertyChanged(BR._all);
            }

            @Override
            public void onDataNotAvailable() {
                // 데이터 없음
                // 다 비우고 알림
                noticeList.clear();
                notifyPropertyChanged(BR._all);
            }
        });
    }

    // 전체삭제버튼 클릭 - xml 과 바로연결 -> 팝업실행
    public void allDeleteButtonOnClick(){
        if(mNavigator != null){
            mNavigator.allDeleteItems();
        }
    }

    // 롱클릭 해당 아이템 삭제 -> 어댑터에서 호출 -> 팝업실행
    void deleteLongClickItem(Notice notice){
        if(mNavigator != null){
            mNavigator.deleteItem(notice.getId());
        }
    }

    // 클릭하여 자세히 보기로 이동
    void moveToDetailClickItem(Notice notice){
        if(mNavigator != null){
            mNavigator.moveToDetail(notice);
        }
    }

}
