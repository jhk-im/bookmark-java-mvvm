package com.jroomstudio.smartbookmarkeditor.data;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 쉬운 테스트를 위해 데이터에 정적으로 액세스 할 수 있는 원격 데이터 소스 구현
 **/
public class FakeBookmarksRemoteDataSource implements BookmarksDataSource {

    private static FakeBookmarksRemoteDataSource INSTANCE;

    /**
     * Bookmark 객체를 캐시 메모리에 저장
     * key - String name
     * value - bookmark
     **/
    private static final Map<String, Bookmark> BOOKMARKS_SERVICE_DATA = new LinkedHashMap<>();

    // 다이렉트 인스턴스 방지
    private FakeBookmarksRemoteDataSource() {}

    /**
     * 싱글 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static FakeBookmarksRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new FakeBookmarksRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * TEST 용 Tab 객체를 생성하고 Map<String(id), Bookmark> 에 추가
     **/
    @VisibleForTesting
    public void addBookmarks(Bookmark... bookmarks){
        if(bookmarks != null){
            for(Bookmark bookmark : bookmarks){
                BOOKMARKS_SERVICE_DATA.put(bookmark.getId(), bookmark);
            }
        }
    }

    /*
     * BookmarksDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshBookmarks() {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // 리스트 callback
    @Override
    public void getBookmarks(@NonNull LoadBookmarksCallback callback) {
        callback.onBookmarksLoaded(Lists.newArrayList(BOOKMARKS_SERVICE_DATA.values()));
    }

    @Override
    public void getBookmarks(@NonNull String category,@NonNull LoadBookmarksCallback callback) {
        //
    }

    // 객체 콜백
    @Override
    public void getBookmark(@NonNull String id, @NonNull GetBookmarkCallback callback) {
        Bookmark bookmark = BOOKMARKS_SERVICE_DATA.get(id);
        callback.onBookmarkLoaded(bookmark);
    }

    // 객체 저장
    @Override
    public void saveBookmark(@NonNull Bookmark bookmark) {
        BOOKMARKS_SERVICE_DATA.put(bookmark.getId(),bookmark);
    }

    // 리스트삭제
    @Override
    public void deleteAllBookmark() {
        BOOKMARKS_SERVICE_DATA.clear();
    }

    // id 로 객체 삭제
    @Override
    public void deleteBookmark(@NonNull String id) {
        BOOKMARKS_SERVICE_DATA.remove(id);
    }

    @Override
    public void deleteAllInCategory(@NonNull String category) {
        // 입력된 카테고리 아이템 모두 삭제
        for(Bookmark bookmark : Lists.newArrayList(BOOKMARKS_SERVICE_DATA.values())){
            if(bookmark.getCategory().equals(category)){
                BOOKMARKS_SERVICE_DATA.remove(bookmark.getId());
            }
        }
    }

    // 입력받은 객체 포지션값 업데이트
    @Override
    public void updatePosition(@NonNull Bookmark bookmark, int position) {
        Bookmark updateBookmark = new Bookmark(bookmark.getId(),bookmark.getTitle(),
                bookmark.getTitle(),bookmark.getAction(),
                bookmark.getCategory(),position,bookmark.getFaviconUrl());
        BOOKMARKS_SERVICE_DATA.put(bookmark.getId(),updateBookmark);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }
}
