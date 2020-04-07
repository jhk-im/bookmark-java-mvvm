package com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;

/**
 * bookmarks 클래스 테이블의 데이터베이스
 **/
@Database(entities = {Bookmark.class}, version = 1, exportSchema = false)
public abstract class BookmarksLocalDatabase extends RoomDatabase {

    // 북마크 테이블 데이터베이스 인스턴스
    private static BookmarksLocalDatabase INSTANCE;

    /**
     * 룸 데이터베이스에서 bookmark 테이블에 데이터를 액세스하는 인터페이스
     * abstract 메소드이기 때문에 해당 클래스의 인스턴스를 생성하면 bookmark 테이블에 접근할 수 있다.
     **/
    public abstract BookmarksDAO bookmarksDAO();

    // synchronized 구현을 위한 Object
    private static final Object sLock = new Object();

    /**
     * synchronized 키워드
     * 객체에 대한 동기화
     * 같은 객체에 대한 모든 동기화 블록은 오직 한 쓰레드만 블록안으로 접근하도록 한다.
     * - 메소드 안의 동기화
     * - 접근을 시도하는 다른 쓰레드는 블록안의 쓰레드가 실행을 마치고 벗어날 때 까지
     *   블록 상태가 된다.
     *
     *
     * databaseBuilder()
     * 런타임시 호출하여 Database 인스턴스를 가져온다.
     * Context , database 클래스, 파일명을 기입한다.
     * addCallback(new Callback()) 으로 onCreate 를 오버라이드한다.
     * -> 처음 데이터베이스가 생성될 때의 로직을 추가한다.
     * 파일명을 가지는 실제 파일이 생성된다.
     * */

    public static BookmarksLocalDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        BookmarksLocalDatabase.class, "BookmarksData.db").build();
            }
            return INSTANCE;
        }
    }
}
