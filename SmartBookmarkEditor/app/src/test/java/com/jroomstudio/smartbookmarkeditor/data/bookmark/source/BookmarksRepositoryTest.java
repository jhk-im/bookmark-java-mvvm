package com.jroomstudio.smartbookmarkeditor.data.bookmark.source;


import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;

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

    /**
     * 주석
     * Mock (org.mockito.Mock)
     * -> Mock 객체를 만드는데 사용된다.
     * Captor (org.mockito.Captor)
     * -> verification(확인) 단계에서 argument 로 사용된 객체를 capture 한다.
     * Test (org.junit.Test)
     * -> 테스트 코드임을 표시한다.
     * Before (org.junit.Before)
     * -> Test 주석을 시작하기 전 사전에 진행해야 할 정의에 해당한다.
     * -> Test 주석이 시작되기 전 항상 호출된다.
     * After (org.junit.After)
     * -> Test 주석을 실행한 후 호출된다.
     * -> 테스트가 모두 끝난 후 임시로 사용된 객체를 리셋한다.
     **/

    // Mockito 는 @Mock 주석을 사용하여 mock 을 주입하는 편리한 방법을 제공한다.
    @Mock // 로컬데이터베이스 mock 객체
    private BookmarksDataSource mLocalDataSource;

    @Mock // 원격데이터베이스 mock 객체 생성
    private BookmarksDataSource mRemoteDataSource;

    @Mock // 북마크 리스트를 로드하는 인터페이스 mock 객체 생성
    private BookmarksDataSource.LoadBookmarksCallback loadBookmarksCallback;

    @Mock // 북마크 싱글 객체를 로드하는 인터페이스 mock 객체 생성
    private BookmarksDataSource.GetBookmarkCallback getBookmarkCallback;

    /**
     * ArgumentCaptor
     * - 특정 메소드에 사용되는 argument(전달인자 or 입력값)를 capture(저장) 한다.
     * - getValue() 메소드를 통해 나중에 다시 사용할 수 있다.
     **/
    @Captor // 북마크 리스트로드 인터페이스를 ArgumentCaptor 로 감싼다.
    private ArgumentCaptor<BookmarksDataSource.LoadBookmarksCallback> loadArgumentCaptor;

    @Captor // 북마크 싱글객체 로드 인터페이스를 ArgumentCaptor 로 감싼다.
    private ArgumentCaptor<BookmarksDataSource.GetBookmarkCallback> getArgumentCaptor;

    // Test 전 셋팅
    @Before
    public void setupBookmarksRepository() {
        // Mock 객체 초기화
        // @Mock 어노테이션으로 선언된 변수들의 Mock 객체를 생성한다.
        MockitoAnnotations.initMocks(this);

        // 테스트를 위해 Mock 객체로 생성된 모든 클래스와 인터페이스의 최상위 클래스
        // Mock 이 아닌 실제 객체의 인스턴스를 생성한다.
        mBookmarksRepository = BookmarksRepository.getInstance(mLocalDataSource,mRemoteDataSource);
    }

    // Mock 객체가 아닌 실제 객체의 인스턴스가 생성되었기 때문에 test 종료 후 인스턴스를 제거한다.
    @After
    public void destroyRepositoryInstance() { BookmarksRepository.destroyInstance(); }

    // test - 원격과 로컬 모두 콜백할 때 문제없이 호출되는가?
    /**
     * any() - (org.mockito.Matchers.any)
     * -> 입력된 객체가 null 인지 판별
     **/
    @Test
    public void getBookmarks_repositoryCachesAfterFirstApiCall(){

        //1. 최상위 클래스에 getBookmarks() 작업요청
        //2. 로컬데이터베이스의 getBookmarks() 에 loadArgumentCaptor 를 입력하여 작업요청
        //3. loadArgumentCaptor 의 getValue() 에서 onDataNotAvailable() 콜백
        //        -> onDataNotAvailable() 메소드가 리턴되면 원격 데이터를 조회함
        //4. 원격데이터베이스의 getBookmarks() 에 loadArgumentCaptor 를 입력하여 작업요청
        //5. 이번에는 getValue() 에서 onBookmarksLoaded() 에 임의의 객체리스트를 담아서 콜백
        //6. 최상위 클래스에 getBookmarks() 에 LoadBookmarksCallback.class 입력
        towBookmarksLoadCallsToRepository(loadBookmarksCallback);

        // 7. verify() 로 원격 데이터소스가 조회되었는지 확인
        // any() (import static org.mockito.Matchers.any)
        // -> 입력된 object null 확인
        // -> LoadBookmarksCallback 호출되는지 확인?
        verify(mRemoteDataSource).
                getBookmarks(any(BookmarksDataSource.LoadBookmarksCallback.class));
    }

    // 로컬 데이터베이스 getBookmarks() test
    @Test
    public void getBookmarks_requestAllBookmarksFromLocalDataSource(){
        // 데이터베이스에 bookmark 리스트 요청
        mBookmarksRepository.getBookmarks(loadBookmarksCallback);
        // verify() -> 로컬 데이터베이스 소스에서 데이터가 로드되는지 확인
        // any() -> LoadBookmarksCallback 호출되는지 확인
        verify(mLocalDataSource).getBookmarks(any(BookmarksDataSource.LoadBookmarksCallback.class));
    }

    // 로컬 데이터베이스 saveBookmark() text
    @Test
    public void saveBookmark_savesBookmarkToServiceAPI() {
        //임의의 Bookmark 객체 생성
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        //최상위 클래스에 saveBookmark 로 객체 저장
        mBookmarksRepository.saveBookmark(bookmark);

        // 원격 소스 -> 데이터 로드되는지 확인 후 saveBookmark() 생성객체 입력
        verify(mRemoteDataSource).saveBookmark(bookmark);
        // 로컬소스  -> 데이터 로드되는지 확인 후 saveBookmarkI) 생성객체 입력
        verify(mLocalDataSource).saveBookmark(bookmark);

        // junit assertThat()
        // mCachedBookmarks Map 에 size 1 인지 확인한다.
        // 추가되었으면 문제없이 test 완료
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(1));
    }

    // 로컬 데이터베이스 updatePosition(Bookmark,position) test
    @Test
    public void updatePosition_ServiceAPIUpdatesCache(){
        // 객체생성
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        // 저장
        mBookmarksRepository.saveBookmark(bookmark);

        // 포지션값 변경
        mBookmarksRepository.updatePosition(bookmark,1);
        // 원격 소스 데이터로드 확인
        verify(mRemoteDataSource).updatePosition(bookmark,1);
        // 로컬 소스 데이터로드 확인
        verify(mLocalDataSource).updatePosition(bookmark,1);
        // junit assertThat()
        // mCachedBookmarks 에 추가된 bookmark 의 position 값이 업데이트 되었는지 확인
        assertThat(mBookmarksRepository.mCachedBookmarks.get(bookmark.getId()).getPosition(), is(1));
    }

    // 로컬 데이터베이스 updatePosition(id,position) test
    @Test
    public void updatePositionId_ServiceAPIUpdatesCache(){
        // 객체생성
        Bookmark bookmark = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        // 저장
        mBookmarksRepository.saveBookmark(bookmark);

        // id 로 변경
        mBookmarksRepository.updatePosition(bookmark.getId(),1);
        // 원격 데이터로드 확인
        verify(mRemoteDataSource).updatePosition(bookmark,1);
        // 로컬 데이터로드 확인
        verify(mLocalDataSource).updatePosition(bookmark,1);
        assertThat(mBookmarksRepository.mCachedBookmarks.get(bookmark.getId()).getPosition(), is(1));
    }

    // 로컬 데이터베이스 getBookmark() test
    @Test
    public void getBookmark_requestSingleBookmarkFromLocal(){
        // getBookmark() 실행
        mBookmarksRepository.getBookmark(TITLE1,getBookmarkCallback);

        // verify() -> 조회확인
        // eq()  import static org.mockito.Matchers.eq;
        // -> 주어진 값과 같은 객체 인수
        // any() -> callback 되는지 확인
        verify(mLocalDataSource).getBookmark(eq(TITLE1),
                any(BookmarksDataSource.GetBookmarkCallback.class));
    }

    // 로컬 데이터베이스 deleteAllBookmarks() test
    @Test
    public void deleteAllBookmarks_ToServiceAPIUpdatesCache() {
        // 객체생성
        Bookmark bookmark1 = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark1);
        Bookmark bookmark2 = new Bookmark(TITLE2,"https://www.naver.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark2);
        Bookmark bookmark3 = new Bookmark(TITLE3,"https://www.daum.com/",
                "WEB_VIEW","Best",0);
        mBookmarksRepository.saveBookmark(bookmark3);

        // deleteAllBookmark() 실행
        mBookmarksRepository.deleteAllBookmark();
        // 데이터 조회확인 후 메소드 실행
        verify(mRemoteDataSource).deleteAllBookmark();
        verify(mLocalDataSource).deleteAllBookmark();
        // junit assertThat()
        // mCachedBookmarks Map 에 size 0 인지 확인한다.
        // 모두 삭제되었으면 문제없이 test 완료
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(0));
    }

    // 로컬 데이터베이스 deleteBookmark(id)
    @Test
    public void deleteBookmark_ToServiceAPIFromCache() {
        // 객채생성
        Bookmark bookmark1 = new Bookmark(TITLE1,"https://www.youtube.com/",
                "WEB_VIEW","Best",0);
        // 저장
        mBookmarksRepository.saveBookmark(bookmark1);
        // 저장된 객체 존재하는지 확인
        assertThat(mBookmarksRepository.mCachedBookmarks.containsKey(bookmark1.getId()), is(true));

        // id로 아이디 삭제하기
        mBookmarksRepository.deleteBookmark(bookmark1.getId());

        // 로컬 조회되는지 확인 -> delete 진행
        verify(mRemoteDataSource).deleteBookmark(bookmark1.getId());
        // 원격 조회되는지 확인 -> delete 진행
        verify(mLocalDataSource).deleteBookmark(bookmark1.getId());
        // 삭제된 객체 삭제되었는지 확인
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
                "WEB_VIEW","Community",2);
        mBookmarksRepository.saveBookmark(bookmark3);

        // 카테고리가 best 인 객체 전부 삭제
        mBookmarksRepository.deleteAllInCategory("Best");

        // 원격 데이터소스 로드 확인
        verify(mRemoteDataSource).deleteAllInCategory("Best");
        // 로컬 데이터소스 로드 확인
        verify(mLocalDataSource).deleteAllInCategory("Best");
        // Best 카테고리 삭제후 Community 카테고리인 TITLE3 만 남는지 확인
        assertThat(mBookmarksRepository.mCachedBookmarks.size(), is(1));
    }



    // 로컬 데이터베이스 DirtyCache test
    @Test
    public void getBookmarksWithDirtyCache_RetrievedFromRemote() {
        // DirtyCached 가 false 인 경우
        // refreshBookmarks() -> 캐시메모리 refresh 가 완료된 경우
        mBookmarksRepository.refreshBookmarks();
        // getBookmarks() 요청
        mBookmarksRepository.getBookmarks(loadBookmarksCallback);

        //DirtyCached 가 false 이면 우선적으로 원격을 살펴본다.
        //임시로 만든 BOOKMARKS 리스트를 로드 콜백에 실어 보낸다.
        setBookmarksAvailable(mRemoteDataSource, BOOKMARKS);

        // 로컬이 아닌 원격 데이터에서 소스가 반환되는지 확인
        // never() (org.mockito.Mockito.never)
        // -> 호출된적 없음을 확인한다.
        verify(mLocalDataSource, never()).getBookmarks(loadBookmarksCallback);
        // 로드 콜백에 임시로 만든 리스트가 전달되었는지 확인
        verify(loadBookmarksCallback).onBookmarksLoaded(BOOKMARKS);
    }

    /**
     * 원격
     **/
    private void setBookmarksAvailable(BookmarksDataSource dataSource, List<Bookmark> bookmarks){
        // 데이터소스 로드 되는지 확인
        verify(dataSource).getBookmarks(loadArgumentCaptor.capture());
        // 캡처에 onBookmarksLoaded 실행하고 임의의 리스트 실어보냄
        loadArgumentCaptor.getValue().onBookmarksLoaded(bookmarks);
    }

    /**
     * 데이터 베이스에 두번의 호출을 발행
     * verify (org.mockito.Mockito.verify)
     * -> mock 객체는 자신의 모든 행동을 기록한다.
     * -> verify() 를 이용해 특정 조건으로 실행되었는지 검증할 수 있다.
     **/
    private void towBookmarksLoadCallsToRepository(BookmarksDataSource.LoadBookmarksCallback callback){

        // 1. 최상위 클래스에 작업을 요청
        mBookmarksRepository.getBookmarks(callback); // 첫번째 api call

        // 2.verify() 로 로컬 데이터소스가 조회되었는지 확인
        // getBookmarks()에 loadArgumentCaptor 를 입력하여 로컬 데이터베이스 callback 캡쳐
        verify(mLocalDataSource).getBookmarks(loadArgumentCaptor.capture());

        // 3. localArgumentCaptor 의 getValue() 에서
        // 데이터가 없다는 콜백인 onDataNotAvailable() 메소드를 리턴
        loadArgumentCaptor.getValue().onDataNotAvailable();

        // 데이터가 없으면 원격 데이터를 조회하게된다.
        // 4. verify() 로 원격 데이터소스가 조회되었는지 확인
        // getBookmarks()에 loadArgumentCaptor 를 입력하여 원격 데이터베이스의 callback 캡쳐
        verify(mRemoteDataSource).getBookmarks(loadArgumentCaptor.capture());

        // 5. 데이터가 캐시 되도록 임의의 Bookmark 객체 리스트를 담아서
        // 데이터가 있을때 인자값에 넣어 전달하는 콜백인 onBookmarksLoaded() 메소드 리턴
       loadArgumentCaptor.getValue().onBookmarksLoaded(BOOKMARKS);

       // 6. 다시한번 최상위 클래스의 getBookmarks 에 LoadBookmarksCallback 클래스 입력
       mBookmarksRepository.getBookmarks(callback); // 두번째 api call

    }
}
