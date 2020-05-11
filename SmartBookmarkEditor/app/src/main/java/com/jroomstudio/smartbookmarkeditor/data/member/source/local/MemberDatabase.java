package com.jroomstudio.smartbookmarkeditor.data.member.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;

/**
 * Member 정보를 저장하는 로컬 데이터 베이스
 **/
@Database(entities = {Member.class}, version = 1, exportSchema = false)
public abstract class MemberDatabase extends RoomDatabase {

    private static MemberDatabase INSTANCE;

    public abstract MemberDao memberDao();

    private static final Object sLock = new Object();

    public static MemberDatabase getInstance(Context context){
        synchronized (sLock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MemberDatabase.class, "Member.db").build();
            }
            return INSTANCE;
        }
    }

}
