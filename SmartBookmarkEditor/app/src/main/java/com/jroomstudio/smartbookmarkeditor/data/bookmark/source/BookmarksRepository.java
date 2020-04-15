package com.jroomstudio.smartbookmarkeditor.data.bookmark.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 로컬 데이터베이스와 서버 데이터베이스 간의 동기화를 구현한다.
 * - 비회원은 로컬데이터를 사용한다.
 * - 회원은 로컬데이터와 원격데이터를 동기화하여 사용한다.
 **/
public class BookmarksRepository implements BookmarksDataSource {


    /**
     * - BookmarksRepository 의 INSTANCE
     **/
    private static BookmarksRepository INSTANCE = null;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * BookMarksDataSource 를 상속받는 BookmarksLocalDataSource 인스턴스를 입력받아 셋팅된다.
     **/
    private final BookmarksDataSource mLocalDataSource;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * BookMarksDataSource 를 상속받는 BookmarksRemoteDataSource 인스턴스를 입력받아 셋팅된다.
     **/
    private final BookmarksDataSource mRemoteDataSource;

    /**
     * 이 변수에 패키지 로컬 visibility(가시성)이 있으므로 테스트에서 액세스할 수 있다.
     **/
    Map<String, Bookmark> mCachedBookmarks;

    /**
     * 다음에 데이터를 요청할 때 강제로 업데이트 하도록 캐시를 유효하지 않은 것으로 표시
     * 이 변수는 패키지 local visibility 에 있으므로 테스트에서 액세스 할 수 있다.
     **/
    boolean mCacheDirty = false;

    // 다이렉트 인스턴스 방지
    private BookmarksRepository(@NonNull BookmarksDataSource localDataSource,
                           @NonNull BookmarksDataSource remoteDataSource) {
        mLocalDataSource = checkNotNull(localDataSource);
        mRemoteDataSource = checkNotNull(remoteDataSource);
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     *
     * @param localDataSource 디바이스 저장소 데이터 소스
     * @param remoteDataSource 백엔드 데이터 소스
     **/
    public static BookmarksRepository getInstance(BookmarksDataSource localDataSource,
                                             BookmarksDataSource remoteDataSource) {
        if(INSTANCE == null){
            INSTANCE = new BookmarksRepository(localDataSource,remoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * {@link #getInstance(BookmarksDataSource, BookmarksDataSource)}
     * 호출될 때 새 인스턴스를 작성하도록 강제하는 데 사용된다.
     **/
    public static void destroyInstance() { INSTANCE = null; }


    /**
     * 입력받은 Bookmark 리스트로 캐시메모리 refresh
     *
     * 캐시 메모리
     * - Map<String, Bookmarks>
     **/
    private void refreshCache(List<Bookmark> bookmarks){
        if(mCachedBookmarks == null){
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.clear();
        for(Bookmark bookmark : bookmarks){
            mCachedBookmarks.put(bookmark.getId(), bookmark);
        }
        // 캐시가 refresh 되면 곧바로 응답해도 되기때문에 false 로 변경
        mCacheDirty = false;
    }

    /**
     * - 캐시메모리에서 Bookmark 의 id 를 입력하여 Bookmark 객체를 찾아 반환한다.
     * - Test 에서 사용된다.
     *
     * 캐시메모리
     * - Map<String, Bookmark>
     **/
    @Nullable
    private Bookmark getBookmarkWithId(@NonNull String id) {
        checkNotNull(id);
        if(mCachedBookmarks == null || mCachedBookmarks.isEmpty()){
            return null;
        }else{
            return mCachedBookmarks.get(id);
        }
    }

    // 원격으로부터 데이터를 전송받아 로컬을 refresh 하는 경우
    private void refreshLocalDataSource(List<Bookmark> bookmarks){
        mLocalDataSource.deleteAllBookmark();
        for(Bookmark bookmark : bookmarks){
            mLocalDataSource.saveBookmark(bookmark);
        }
    }

    // 로컬이 비어있는 경우 원격에서 확인
    private void getBookmarksFromRemoteDataSource(@NonNull final LoadBookmarksCallback callback){
        mRemoteDataSource.getBookmarks(new LoadBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                refreshCache(bookmarks);
                refreshLocalDataSource(bookmarks);
                callback.onBookmarksLoaded(new ArrayList<>(mCachedBookmarks.values()));
            }

            @Override
            public void onDataNotAvailable() { callback.onDataNotAvailable(); }
        });
    }

    /*
     * BookmarksDataSource 오버라이드 메소드 구현
     */

    // Bookmark 의 데이터베이스가 변경되면 캐시메모리를 refresh 해야하기 때문에 true 로 변경
    // viewModel 에서 호출
    @Override
    public void refreshBookmarks() { mCacheDirty = true; }

    /**
     * - 저장소 테이블에서 모든 Bookmark 객체의 정보를 가져온다.
     * - 캐시, 로컬 데이터소스(SQLite) 또는 원격 데이터 소스 중 먼저 사용 가능한 작업을 가져온다.
     *
     * {@link LoadBookmarksCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getBookmarks(@NonNull LoadBookmarksCallback callback) {
        checkNotNull(callback);
        // Map<String, Bookmark> 이 null 이 아니고 mCacheDirty 가 false 일때는 캐시메모리로 즉시 응답
        // 즉, remote 나 local 로 부터 데이터를 받아오는데 성공 한 후
        // 캐시 메모리의 강제 업데이트가 필요 없는 경우는 캐시메모리로 응답한다.
        /*
        if (mCachedBookmarks != null && !mCacheDirty) {
            callback.onBookmarksLoaded(new ArrayList<>(mCachedBookmarks.values()));
            return;
        }
        */
        // mCacheDirty 가 true 이면 데이터가 변경되어 refresh 해야하는 상황
        /*
        if(mCacheDirty) {
            getBookmarksFromRemoteDataSource(callback);
        }else{
        }
        */
        // LocalDataSource 로 부터 데이터를 가져온다.
        mLocalDataSource.getBookmarks(new LoadBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                // 로드성공
                // 받아온 데이터는 캐쉬메모리에 refresh
                refreshCache(bookmarks);
                callback.onBookmarksLoaded(new ArrayList<>(mCachedBookmarks.values()));
                //callback.onBookmarksLoaded(bookmarks);
            }
            @Override
            public void onDataNotAvailable() {
                // 로컬이 비어있을때 원격에서 확인한다.
                // getBookmarksFromRemoteDataSource(callback);
            }
        });

    }
    /**
     * - 저장소 테이블에서 입력된 카테고리안의 Bookmark 객체의 정보를 가져온다.
     * - 캐시, 로컬 데이터소스(SQLite) 또는 원격 데이터 소스 중 먼저 사용 가능한 작업을 가져온다.
     *
     * {@link LoadBookmarksCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getBookmarks(@NonNull String category, @NonNull LoadBookmarksCallback callback) {
        checkNotNull(callback);
        checkNotNull(category);
        // LocalDataSource 로 부터 데이터를 가져온다.
        mLocalDataSource.getBookmarks(category,new LoadBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Bookmark> bookmarks) {
                // 로드 성공하면 로드 콜백에 리스트를 보낸다.
                callback.onBookmarksLoaded(bookmarks);
            }
            @Override
            public void onDataNotAvailable() {
                // 해당 카테고리에 아이템 없다고 알린다.
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * - 로컬 데이터 저장소 에 액세스 한다
     * - 로컬 테이블이 비어있거나 없으면 네트워크 데이터 소스를 사용한다.
     * - 샘플을 단순화하기 위해 수행된다.
     *
     * {@link GetBookmarkCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getBookmark(@NonNull String id, @NonNull GetBookmarkCallback callback) {
        checkNotNull(id);
        checkNotNull(callback);

        // 캐시메모리에서 찾기
        Bookmark cachedBookmark = getBookmarkWithId(id);
        // 캐시에서 해당 Bookmark 를 찾았을 경우 즉시응답

        if(cachedBookmark != null){
            callback.onBookmarkLoaded(cachedBookmark);
            return;
        }

        // 캐시에 없는경우 로컬에서 Bookmark 객체를 가져온다.
        mLocalDataSource.getBookmark(id, new GetBookmarkCallback() {
            @Override
            public void onBookmarkLoaded(Bookmark bookmark) {
                // 로드성공
                // 캐시메모리 업데이트
                if(mCachedBookmarks == null){
                    mCachedBookmarks = new LinkedHashMap<>();
                }
                // 캐시메모리에도 추가
                mCachedBookmarks.put(bookmark.getId(),bookmark);
                callback.onBookmarkLoaded(bookmark);
            }

            // 데이터가 없을 때
            @Override
            public void onDataNotAvailable() {
                /*
                // 원격에서 찾는다
                mRemoteDataSource.getBookmark(id, new GetBookmarkCallback() {
                    @Override
                    public void onBookmarkLoaded(Bookmark bookmark) {
                        // 로드성공
                        // 캐시메모리 업데이트
                        if(mCachedBookmarks == null){
                            mCachedBookmarks = new LinkedHashMap<>();
                        }
                        // 캐시메모리에도 추가
                        mCachedBookmarks.put(bookmark.getId(),bookmark);
                        callback.onBookmarkLoaded(bookmark);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        onDataNotAvailable();
                    }
                });
                */
                callback.onDataNotAvailable();
            }
        });

    }

    // 로컬과 원격 데이터 소스에 각각 Bookmark 객체를 저장한다.
    @Override
    public void saveBookmark(@NonNull Bookmark bookmark) {
        checkNotNull(bookmark);
        // 로컬, 원격
        mLocalDataSource.saveBookmark(bookmark);
        //mRemoteDataSource.saveBookmark(bookmark);

        // 캐시메모리 업데이트
        if (mCachedBookmarks == null){
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.put(bookmark.getId(),bookmark);
    }

    // 모든 북마크 삭제
    @Override
    public void deleteAllBookmark() {
        // 로컬, 원격
        mLocalDataSource.deleteAllBookmark();
        //mRemoteDataSource.deleteAllBookmark();

        // 캐시메모리 업데이트
        if (mCachedBookmarks == null){
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.clear();
    }

    // bookmark 삭제
    @Override
    public void deleteBookmark(@NonNull String id) {
        // 로컬 , 원격
        mLocalDataSource.deleteBookmark(checkNotNull(id));
        //mRemoteDataSource.deleteBookmark(checkNotNull(id));
        // 캐시메모리
        mCachedBookmarks.remove(id);
    }

    @Override
    public void deleteAllInCategory(@NonNull String category) {
        // 입력된 카테고리의 아이템 모두 삭제
        mLocalDataSource.deleteAllInCategory(checkNotNull(category));
        //mRemoteDataSource.deleteAllInCategory(checkNotNull(category));

        for(Bookmark bookmark : new ArrayList<>(mCachedBookmarks.values())){
            if(bookmark.getCategory().equals(category)){
                mCachedBookmarks.remove(bookmark.getId());
            }
        }

    }

    // bookmark 포지션 변경
    @Override
    public void updatePosition(@NonNull Bookmark bookmark, int position) {
        checkNotNull(bookmark);
        checkNotNull(position);
        // 로컬, 원격
        mLocalDataSource.updatePosition(bookmark,position);
        //mRemoteDataSource.updatePosition(bookmark,position);
        Bookmark updateBookmark =
                new Bookmark(bookmark.getId(),bookmark.getTitle(), bookmark.getUrl(),
                        bookmark.getAction(),bookmark.getCategory(),position,
                        bookmark.getFaviconUrl());

        // 캐시메모리 업데이트
        if (mCachedBookmarks == null){
            mCachedBookmarks = new LinkedHashMap<>();
        }
        mCachedBookmarks.put(bookmark.getId(),updateBookmark);
    }

    /**
     * id 를 입력받아 캐시 메모리에서 Bookmark 객체를 가져와 updatePosition(Bookmark,int) 을 호출한다.
     * - test 에서 활용
     **/
    @Override
    public void updatePosition(@NonNull String id, int position) {
        checkNotNull(id);
        checkNotNull(position);
        updatePosition(getBookmarkWithId(id),position);
    }
}
