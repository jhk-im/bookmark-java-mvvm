package com.jroomstudio.smartbookmarkeditor.data.notice;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * notifications  테이블의 데이터 베이스
 **/
@Database(entities = {Notice.class}, version = 1, exportSchema = false)
public abstract class NoticeLocalDatabase extends RoomDatabase {

    // 인스턴스
    private static NoticeLocalDatabase INSTANCE;

    /**
     * 룸 데이터베이스 notifications 테이블에 데이터를 액세스하는 abstract 메소드
     **/
    public abstract NotificationsDAO notificationsDAO();

    // synchronized 구현을 위한 Object
    private static final Object sLock = new Object();

    public static NoticeLocalDatabase getInstance(Context context){
        synchronized (sLock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        NoticeLocalDatabase.class, "NotificationsData.db").build();
            }
            return INSTANCE;
        }
    }
}
