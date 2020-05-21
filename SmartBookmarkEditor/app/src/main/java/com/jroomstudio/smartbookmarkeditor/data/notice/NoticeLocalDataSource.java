package com.jroomstudio.smartbookmarkeditor.data.notice;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Notice 데이터에 액세스 하기위한 진입점
 **/
public interface NoticeLocalDataSource {

    interface LoadNotificationsCallback{
        void onNotificationsLoaded(List<Notice> notifications);
        void onDataNotAvailable();
    }

    // notifications 리스트 모두 가져오기
    void getNotifications(@NonNull LoadNotificationsCallback callback);

    // 입력된 read 값인 리스트 모두 가져오기
    void getNotifications(boolean read, @NonNull LoadNotificationsCallback callback);

    // 입력된 id 의 Notice 객체를 찾아 제거
    void deleteNotice(@NonNull String id);

    // 모두삭제
    void deleteAllNotifications();

    // read 값 변경
    void updateRead(@NonNull String id, boolean read);

    // Notice 저장
    void saveNotice(@NonNull Notice notice);
}
