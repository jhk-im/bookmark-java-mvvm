package com.jroomstudio.smartbookmarkeditor.data.notice;

import androidx.annotation.NonNull;

import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import java.util.List;

import static com.google.android.gms.common.internal.Preconditions.checkNotNull;

public class NoticeLocalRepository implements NoticeLocalDataSource {

    // 인스턴스
    // 해당 변수는 Main Memory 에 저장
    private static volatile NoticeLocalRepository INSTANCE;

    /**
     * notifications 테이블 데이터 액세스
     **/
    private NotificationsDAO mNotificationsDAO;

    /**
     * - 데이터베이스 작업 시 사용되는 쓰레드를 관리하는 Executor 프레임워크가 구현되어있다.
     * - 클래스 인스턴스 생성시 입력받아 셋팅한다.
     **/
    private AppExecutors mAppExecutors;

    // 다이렉트 인스턴스 방지
    // 다이렉트 인스턴스 방지
    private NoticeLocalRepository(@NonNull AppExecutors appExecutors,
                                  @NonNull NotificationsDAO notificationsDAO){
        mAppExecutors = appExecutors;
        mNotificationsDAO = notificationsDAO;
    }

    // 싱글 인스턴스 리턴
    public static NoticeLocalRepository getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull NotificationsDAO notificationsDAO){
        if(INSTANCE == null){
            synchronized (NoticeLocalRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new NoticeLocalRepository(appExecutors, notificationsDAO);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * NoticeDataSource 오버라이드 메소드
     **/
    // 모든 알림 가져오기
    @Override
    public void getNotifications(@NonNull LoadNotificationsCallback callback) {
        Runnable getAllRunnable = () -> {
            final List<Notice> notifications = mNotificationsDAO.getAllNotifications();
            mAppExecutors.getMainThread().execute(() -> {
                if(notifications.isEmpty()){
                    // 새 테이블이거나 비어있는경우
                    callback.onDataNotAvailable();
                }else{
                    // 데이터 로드에 성공하여 리스트를 담아 콜백
                    callback.onNotificationsLoaded(notifications);
                }
            });
        };
        mAppExecutors.getDiskIO().execute(getAllRunnable);
    }

    // 입력된 read 값인 알림 모두 가져오기
    @Override
    public void getNotifications(boolean read, @NonNull LoadNotificationsCallback callback) {
        Runnable getIsReadRunnable = () -> {
            final List<Notice> notifications = mNotificationsDAO.getNoticeByRead(read);
            mAppExecutors.getMainThread().execute(() -> {
                if(notifications.isEmpty()){
                    // 새 테이블이거나 비어있는경우
                    callback.onDataNotAvailable();
                }else{
                    // 데이터 로드에 성공하여 리스트를 담아 콜백
                    callback.onNotificationsLoaded(notifications);
                }
            });
        };
        mAppExecutors.getDiskIO().execute(getIsReadRunnable);
    }

    // 입력한 아이디로 알림 삭제
    @Override
    public void deleteNotice(@NonNull String id) {
        Runnable deleteRunnable = () -> mNotificationsDAO.deleteNoticeById(id);
        mAppExecutors.getDiskIO().execute(deleteRunnable);
    }

    // 알림 모두삭제
    @Override
    public void deleteAllNotifications() {
        Runnable deleteAllRunnable = () -> mNotificationsDAO.deleteAllNotice();
        mAppExecutors.getDiskIO().execute(deleteAllRunnable);
    }

    // 알림 read 값 변경
    @Override
    public void updateRead(@NonNull String id, boolean read) {
        Runnable updateReadRunnable = () -> {
            mNotificationsDAO.updateRead(read,id);
        };
        mAppExecutors.getDiskIO().execute(updateReadRunnable);
    }

    // 알림 저장
    @Override
    public void saveNotice(@NonNull Notice notice) {
        checkNotNull(notice);
        Runnable saveRunnable = () -> {
            mNotificationsDAO.insertNotice(notice);
        };
        mAppExecutors.getDiskIO().execute(saveRunnable);
    }

}
