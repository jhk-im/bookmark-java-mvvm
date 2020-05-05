package com.jroomstudio.smartbookmarkeditor.data.notice;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


/**
 * notifications 테이블 데이터 액세스 인터페이스
 **/
@Dao
public interface NotificationsDAO {

    /**
     * 모든 notice 아이템을 가져온다.
     **/
    @Query("SELECT * FROM notifications")
    List<Notice> getAllNotifications();

    /**
     * 입력된 boolean 값인 notice 모두 가져오기
     **/
    @Query("SELECT * FROM notifications WHERE read = :read")
    List<Notice> getNoticeByRead(boolean read);

    /**
     * 입력된 id 로 notice 를 삭제한다.
     **/
    @Query("DELETE FROM notifications WHERE id = :id")
    int deleteNoticeById(String id);

    /**
     * 모두삭제
     **/
    @Query("DELETE FROM notifications")
    void deleteAllNotice();

    /**
     * read 업데이트
     **/
    @Query("UPDATE notifications SET read = :read WHERE id = :id")
    void updateRead(boolean read, String id);

    /**
     * notice 저장
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotice(Notice notice);
}
