package com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 룸 데이터베이스의 bookmarks 테이블에서 액세스하는 과정을 구체적으로 구현한다.
 **/
public class BookmarksLocalDataSource implements BookmarksDataSource {

    /**
     * - BookmarksLocalDataSource 의 INSTANCE
     * - volatile 은 Java 변수를 Main memory 에 저장하겠다는 것을 명시하는 것
     **/
    private static volatile BookmarksLocalDataSource INSTANCE;

    /**
     * - bookmarks 테이블 데이터 액세스 기능을 하는 인터페이스
     * - 클래스 인스턴스 생성시 입력받아 셋팅한다.
     **/
    private BookmarksDAO mBookmarksDAO;

    /**
     * - 데이터베이스 작업 시 사용되는 쓰레드를 관리하는 Executor 프레임워크가 구현되어있다.
     * - 클래스 인스턴스 생성시 입력받아 셋팅한다.
     **/
    private AppExecutors mAppExecutors;

    // 다이렉트 인스턴스 방지
    private BookmarksLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull BookmarksDAO bookmarksDAO){
        mAppExecutors = appExecutors;
        mBookmarksDAO = bookmarksDAO;
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     * - 클래스 인스턴스 생성 메소드
     * - getInstance 메소드에 AppExecutors, BookmarksDAO 를 입력받는다.
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     *
     * @param appExecutors 데이터 액세스를 실행 할 쓰레드를 관리하는 인스턴스
     * @param bookmarksDAO 데이터 베이스에서 쿼리문으로 제어하는 인터페이스 인스턴스
     **/
    public static BookmarksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull BookmarksDAO bookmarksDAO){
        if(INSTANCE == null){
            synchronized (BookmarksLocalDataSource.class) {
                if(INSTANCE == null){
                    INSTANCE = new BookmarksLocalDataSource(appExecutors, bookmarksDAO);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * INSTANCE 를 null 로
     **/
    @VisibleForTesting
    static void clearInstance() { INSTANCE = null; }


    /*
     * BookmarksDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshBookmarks() {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // bookmark list 가져오기
    @Override
    public void getBookmarks(@NonNull LoadBookmarksCallback callback) {
        Runnable runnable = () -> {
           final List<Bookmark> bookmarks = mBookmarksDAO.getAllBookmarks();
           mAppExecutors.getMainThread().execute(() -> {
               if(bookmarks.isEmpty()){
                   // 새 테이블이거나 비어있는경우
                   callback.onDataNotAvailable();
               }else{
                 // 데이터 로드에 성공하여 리스트를 담아 콜백
                 callback.onBookmarksLoaded(bookmarks);
               }
           });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // 입력된 카테고리의 북마크 리스트 가져오기
    @Override
    public void getBookmarks(@NonNull String category, @NonNull LoadBookmarksCallback callback) {
        Runnable runnable = () -> {
            final List<Bookmark> bookmarks = mBookmarksDAO.getAllBookmarks(category);
            mAppExecutors.getMainThread().execute(() -> {
                if(bookmarks.isEmpty()){
                    // 새 테이블이거나 비어있는경우
                    callback.onDataNotAvailable();
                }else{
                    // 데이터 로드에 성공하여 리스트를 담아 콜백
                    callback.onBookmarksLoaded(bookmarks);
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // bookmark 객체 가져오기
    @Override
    public void getBookmark(@NonNull String id, @NonNull GetBookmarkCallback callback) {
        Runnable runnable = () -> {
            final Bookmark bookmark = mBookmarksDAO.getBookmarkById(id);
            mAppExecutors.getMainThread().execute(() -> {
                if(bookmark != null){
                    // id 에 해당하는 아이템이 있는경우
                    callback.onBookmarkLoaded(bookmark);
                } else{
                    // 아이템이 없다면
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // bookmark insert
    // 중복된 id 는 업데이트
    @Override
    public void saveBookmark(@NonNull Bookmark bookmark) {
        checkNotNull(bookmark);
        Runnable runnable = () -> {
            mBookmarksDAO.insertBookmark(bookmark);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // 모든 bookmark 삭제
    @Override
    public void deleteAllBookmark() {
        Runnable runnable = () -> {
            mBookmarksDAO.deleteAllBookmarks();
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    // id로 bookmark 삭제
    @Override
    public void deleteBookmark(@NonNull String id) {
        Runnable deleteRunnable = () -> mBookmarksDAO.deleteBookmarkById(id);
        mAppExecutors.getDiskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteAllInCategory(@NonNull String category) {
        //입력된 카테고리의 아이템 모두 삭제
        Runnable runnable = () -> mBookmarksDAO.deleteAllInCategory(category);
        mAppExecutors.getDiskIO().execute(runnable);
    }


    // 포지션 변경
    @Override
    public void updatePosition(@NonNull Bookmark bookmark, int position) {
        Runnable runnable = () -> {
            mBookmarksDAO.updatePosition(bookmark.getId(),position);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }
}
