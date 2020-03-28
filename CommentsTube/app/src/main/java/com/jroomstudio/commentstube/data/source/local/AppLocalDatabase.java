package com.jroomstudio.commentstube.data.source.local;

import android.content.Context;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.util.AppExecutors;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tab 클래스의 테이블이 포함 된 데이터 베이스
 * */
@Database(entities = {Tab.class}, version = 1, exportSchema = false)
public abstract class AppLocalDatabase extends RoomDatabase {

    // 룸 데이터베이스의 인스턴스
    private static AppLocalDatabase INSTANCE;

    /**
     * 룸 데이터베이스 쿼리문 구현 인터페이스
     * 룸 데이터베이스에서 실질적으로 데이터를 다루는 쿼리문이 구현되어있다.
     * abstract 로 구현되어있기 때문에 룸 데이터베이스 인스턴스를 생성하면
     * 쿼리문 인터페이스를 활용하여 데이터를 다루게 된다.
     * */
    public abstract TabsDao tabsDao();

    // synchronized 구현을 위한 Object
    private static final Object sLock = new Object();

    public static AppLocalDatabase getInstance(Context context , AppExecutors executors){
        /**
         * synchronized 키워드
         * 객체에 대한 동기화
         * 같은 객체에 대한 모든 동기화 블록은 오직 한 쓰레드만 블록안으로 접근하도록 한다.
         * - 메소드 안의 동기화
         * - 접근을 시도하는 다른 쓰레드는 블록안의 쓰레드가 실행을 마치고 벗어날 때 까지
         *   블록 상태가 된다.
         * */
        synchronized (sLock) {
            if(INSTANCE == null){
                /**
                 * databaseBuilder()
                 * 런타임시 호출하여 Database 인스턴스를 가져온다.
                 * Context , database 클래스, 파일명을 기입한다.
                 * addCallback(new Callback()) 으로 onCreate 를 오버라이드한다.
                 * -> 처음 데이터베이스가 생성될 때의 로직을 추가한다.
                 * 파일명을 가지는 실제 파일이 생성된다.
                 * */
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppLocalDatabase.class,"Tabs.db").
                        addCallback(new Callback() {
                            /**
                             * 데이터베이스가 처음 생성될 때 호출된다.
                             * @param db The database.
                             */
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Runnable addRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // Tab 기본정보 입력
                                        INSTANCE.tabsDao().insertTab(new Tab("SUBSCRIBE","SUB_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("BEST","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("GAME","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("MUSIC","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("SPORTS","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("MOVIE","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("NEWS","DEFAULT_FRAGMENT"));
                                        INSTANCE.tabsDao().insertTab(new Tab("DOCUMENTARY","DEFAULT_FRAGMENT"));
                                    }
                                };
                                executors.getDiskIO().execute(addRunnable);
                            }
                        }).build();

            }
            // 데이터 베이스 리턴
            return INSTANCE;
        }
    }

}
