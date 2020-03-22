package com.jroomstudio.commentstube.data.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jroomstudio.commentstube.data.Tab;

@Database(entities = {Tab.class}, version = 1, exportSchema = false)
public abstract class AppLocalDatabase extends RoomDatabase {

    private static AppLocalDatabase INSTANCE;

    public abstract TabsDao tabsDao();

    private static final Object sLock = new Object();

    public static AppLocalDatabase getInstance(Context context){
        synchronized (sLock) {
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppLocalDatabase.class,"Tabs.db")
                        .build();
            }
            return INSTANCE;
        }
    }
    
}
