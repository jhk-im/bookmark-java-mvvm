package com.jroomstudio.smartbookmarkeditor.data.category.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;

/**
 * categories 테이블의 데이터베이스
 **/
@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class CategoriesLocalDatabase extends RoomDatabase {

    // 북마크 테이블 데이터베이스 인스턴스
    private static CategoriesLocalDatabase INSTANCE;

    /**
     * categories 테이블의 데이터를 액세스하는 인터페이스
     **/
    public abstract CategoriesDAO categoriesDAO();

    // synchronized 구현을 위한 Object
    private static final Object sLock = new Object();

    public static CategoriesLocalDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CategoriesLocalDatabase.class, "CategoriesData.db").build();
            }
            return INSTANCE;
        }
    }

}
