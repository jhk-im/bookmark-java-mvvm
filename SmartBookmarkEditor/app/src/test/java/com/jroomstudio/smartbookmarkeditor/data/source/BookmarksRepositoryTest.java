package com.jroomstudio.smartbookmarkeditor.data.source;


import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
/**
 * 북마크 캐시를 사용하여 메모리 내 저장소를 구현하기위한 단위 테스트.
 **/
public class BookmarksRepositoryTest {

    private final static String TITLE1 = "Youtube";
    private final static String TITLE2 = "Naver";
    private final static String TITLE3 = "Kakao";

    private static List<Bookmark> BOOKMARKS =
            Lists.newArrayList(new Bookmark("Youtube","https://www.youtube.com/",
                    "WEB_VIEW","Best",0),
                    new Bookmark("Naver","https://www.naver.com/",
                            "APP_VIEW","Best",1));

    private BookmarksRepository mBookmarksRepository;

    @Mock
    private BookmarksDataSource mLocalDataSource;

    @Mock
    private BookmarksDataSource mRemoteDataSource;

    @Mock
    private BookmarksDataSource.LoadBookmarksCallback loadBookmarksCallback;

    @Mock
    private BookmarksDataSource.GetBookmarkCallback getBookmarkCallback;

    @Captor
    private ArgumentCaptor<BookmarksDataSource.LoadBookmarksCallback> loadArgumentCaptor;

    @Captor
    private ArgumentCaptor<BookmarksDataSource.GetBookmarkCallback> getArgumentCaptor;

    @Before
    public void setupBookmarksRepository() {
        /*
         * Mockito 는 @Mock 주석을 사용하여 mock 을 주입하는 편리한 방법을 제공한다.
         * 테스트에 mock 을 주입하여 initMocks 메소드를 호출해야한다.
         */
        MockitoAnnotations.initMocks(this);
        // 테스트중인 클래스에 대해 참조를 얻는다.
        mBookmarksRepository = BookmarksRepository.getInstance(mLocalDataSource,mRemoteDataSource);
    }

    @After
    public void destroyRepositoryInstance() { BookmarksRepository.destroyInstance(); }

    // 원격과 로컬의 callback 메소드가 문제없이 호출되는가?
    @Test
    public void getBookmarks_repositoryCachesAfterFirstApiCall(){
        /*
         * 두개의 호출이 Bookmarks 저장소에 발행된 경우
         * Callback 을 캡처하는 설정 Captor 가 주어진다.
         */
        towBookmarksLoadCallsToRepository(loadBookmarksCallback);

        // 서비스 Api 에 데이터 한번만 요청
        verify(mRemoteDataSource).getBookmarks(any(BookmarksDataSource.LoadBookmarksCallback.class));
    }

    // 로컬 데이터베이스 getBookmarks() test
    @Test
    public void getBookmarks_requestAllBookmarksFromLocalDataSource(){
        // 로컬 데이터베이스에 bookmark 리스트 요청
        mBookmarksRepository.getBookmarks(loadBookmarksCallback);
        // 로컬 데이터베이스 소스에서 데이터가 로드된다.
        verify(mLocalDataSource).getBookmarks(any(BookmarksDataSource.LoadBookmarksCallback.class));
    }

    // 로컬 데이터베이스 saveBookmark() text
    @Test
    public void saveBookmark_savesBookmarkToServiceAPI() {
        //
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        //
        mBookmarksRepository.saveBookmark(bookmark);

        verify(mRemoteDataSource).saveBookmark(bookmark);
        verify(mLocalDataSource).saveBookmark(bookmark);
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(1));
    }

    // 로컬 데이터베이스 updatePosition(Bookmark,position) test
    @Test
    public void updatePosition_ServiceAPIUpdatesCache(){
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark);

        mBookmarksRepository.updatePosition(bookmark,bookmark.getPosition());
        verify(mRemoteDataSource).updatePosition(bookmark,bookmark.getPosition());
        verify(mLocalDataSource).updatePosition(bookmark,bookmark.getPosition());
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(1));
    }

    // 로컬 데이터베이스 updatePosition(id,position) test
    @Test
    public void updatePositionId_ServiceAPIUpdatesCache(){
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark);

        mBookmarksRepository.updatePosition(bookmark.getId(),bookmark.getPosition());
        verify(mRemoteDataSource).updatePosition(bookmark,bookmark.getPosition());
        verify(mLocalDataSource).updatePosition(bookmark,bookmark.getPosition());
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(1));
    }

    // 로컬 데이터베이스 getBookmark() test
    @Test
    public void getBookmark_requestSingleBookmarkFromLocal(){
        mBookmarksRepository.getBookmark(TITLE1,getBookmarkCallback);

        verify(mLocalDataSource).getBookmark(eq(TITLE1),
                any(BookmarksDataSource.GetBookmarkCallback.class));
    }

    // 로컬 데이터베이스 deleteAllBookmarks() test
    @Test
    public void deleteAllBookmarks_ToServiceAPIUpdatesCache() {
        Bookmark bookmark1 = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark1);
        Bookmark bookmark2 = new Bookmark(TITLE2,"https://www.naver.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark2);
        Bookmark bookmark3 = new Bookmark(TITLE3,"https://www.daum.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark3);

        mBookmarksRepository.deleteAllBookmark();
        verify(mRemoteDataSource).deleteAllBookmark();
        verify(mLocalDataSource).deleteAllBookmark();
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(0));
    }

    // 로컬 데이터베이스 deleteBookmark(id)
    @Test
    public void deleteBookmark_ToServiceAPIFromCache() {
        Bookmark bookmark1 = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark1);
        assertThat(mBookmarksRepository.mCachedBookmarks.containsKey(bookmark1.getId()), is(true));

        mBookmarksRepository.deleteBookmark(bookmark1.getId());

        verify(mRemoteDataSource).deleteBookmark(bookmark1.getId());
        verify(mLocalDataSource).deleteBookmark(bookmark1.getId());
        assertThat(mBookmarksRepository.mCachedBookmarks.containsKey(bookmark1.getId()), is(false));
    }

    // 로컬데이터베이스 deleteAllInCategory
    @Test
    public void deleteAllInCategory_ToServiceAPIUpdatesCache(){
        Bookmark bookmark1 = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark1);
        Bookmark bookmark2 = new Bookmark(TITLE2,"https://www.naver.com/",
                "WEB_VIEW","Best",1);
        mBookmarksRepository.saveBookmark(bookmark2);
        Bookmark bookmark3 = new Bookmark(TITLE3,"https://www.daum.com/",
                "WEB_VIEW","Best",2);
        mBookmarksRepository.saveBookmark(bookmark3);

        mBookmarksRepository.deleteAllInCategory("Best");
        verify(mRemoteDataSource).deleteAllInCategory("Best");
        verify(mLocalDataSource).deleteAllInCategory("Best");
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(0));
    }



    // 로컬 데이터베이스 DirtyCache test
    @Test
    public void getBookmarksWithDirtyCache_RetrievedFromRemote() {
        mBookmarksRepository.refreshBookmarks();
        mBookmarksRepository.getBookmarks(loadBookmarksCallback);

        setBookmarksAvailable(mRemoteDataSource, BOOKMARKS);

        verify(mLocalDataSource, never()).getBookmarks(loadBookmarksCallback);
        verify(loadBookmarksCallback).onBookmarksLoaded(BOOKMARKS);
    }

    //
    private void setBookmarksAvailable(BookmarksDataSource dataSource, List<Bookmark> bookmarks){
        verify(dataSource).getBookmarks(loadArgumentCaptor.capture());
        loadArgumentCaptor.getValue().onBookmarksLoaded(bookmarks);
    }

    /**
     * 데이터 베이스에 두번의 호출을 발행
     **/
    private void towBookmarksLoadCallsToRepository(BookmarksDataSource.LoadBookmarksCallback callback){

        // 저장소에 작업을 요청한 경우
        mBookmarksRepository.getBookmarks(callback); // 첫번째 api call

        // Mockito captor 를 사용하여 callback 캡쳐
        verify(mLocalDataSource).getBookmarks(loadArgumentCaptor.capture());

        // Local Database 가 아직 비어있을 때
        loadArgumentCaptor.getValue().onDataNotAvailable();

        // 원격 데이터 소스가 조회되었는지 확인
        verify(mRemoteDataSource).getBookmarks(loadArgumentCaptor.capture());

        // 데이터가 캐시 되도록 콜백 트리거
       loadArgumentCaptor.getValue().onBookmarksLoaded(BOOKMARKS);

       mBookmarksRepository.getBookmarks(callback); // 두번째 api call

    }
}
