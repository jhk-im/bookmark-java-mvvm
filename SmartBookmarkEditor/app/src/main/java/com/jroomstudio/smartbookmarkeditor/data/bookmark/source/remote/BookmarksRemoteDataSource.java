package com.jroomstudio.smartbookmarkeditor.data.bookmark.source.remote;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksDataSource;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 네트워크 지연시간 시뮬레이션을 테스트하는 데이터소스 구현
 **/
public class BookmarksRemoteDataSource implements BookmarksDataSource {

    /**
     * - BookmarksRemoteDataSource 의 INSTANCE
     **/
    private static BookmarksRemoteDataSource INSTANCE;

    /**
     * - 네트워크 지연시간 시뮬레이션에 사용될 Mills int 변수
     * - 클래스에 존재하는 단하나의 상수 static final int
     **/
    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    /**
     * 회원 / 비회원 구분
     * true 경우에만 원격 데이터베이스에 데이터를 요청한다.
     **/
    boolean mLoginUser = false;


    /**
     * - test 로 생성된 Tab 객체를 캐시 메모리에 저장
     * key - String name
     * value - Bookmark
     **/
    private static final Map<String, Bookmark> BOOKMARK_SERVICE_DATA = new LinkedHashMap<>();

    // Test 용 데이터
    private static void addBookmark(String title, String url,String action,
                                    String category, int position,String faviconUrl){
        Bookmark bookmark = new Bookmark(title,url,action,category,position,faviconUrl);
        BOOKMARK_SERVICE_DATA.put(bookmark.getId(),bookmark);
    }
    /*
    static {
        BOOKMARK_SERVICE_DATA = new LinkedHashMap<>(2);
        addBookmark("Naver","https://www.naver.com/","APP","Bookmark",0);
        addBookmark("Youtube","https://www.youtube.com/","APP","Bookmark",1);
        addBookmark("Daum","https://www.daum.net/","APP","Bookmark",2);
        addBookmark("뽐뿌","http://www.ppomppu.co.kr/","WEB_VIEW","Community",0);
        addBookmark("보배드림","https://www.bobaedream.co.kr/","WEB_VIEW","Community",1);
    }
    */

    // 다이렉트 인스턴스 방지
    private BookmarksRemoteDataSource() {}

    /**
     * - TabsRemoteDataSource 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static BookmarksRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new BookmarksRemoteDataSource();
        }
        return INSTANCE;
    }

    /*
     * BookmarksDataSource 오버라이드 메소드 구현
     */

    @Override
    public void refreshBookmarks() {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }

    // bookmark 리스트를 찾아 callback
    @Override
    public void getBookmarks(@NonNull LoadBookmarksCallback callback) {
        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onBookmarksLoaded(Lists.newArrayList(BOOKMARK_SERVICE_DATA.values()));
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    // 북마크 리스트를 카테고리로 찾아서 콜백
    @Override
    public void getBookmarks(@NonNull String category, @NonNull LoadBookmarksCallback callback) {
        //
    }

    // bookmark 객체 찾아 callback
    @Override
    public void getBookmark(@NonNull String id, @NonNull GetBookmarkCallback callback) {
        final Bookmark bookmark = BOOKMARK_SERVICE_DATA.get(id);
        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onBookmarkLoaded(bookmark);
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    // bookmark 저장
    @Override
    public void saveBookmark(@NonNull Bookmark bookmark) {
        BOOKMARK_SERVICE_DATA.put(bookmark.getId(), bookmark);
    }

    // 모든 bookmark 삭제
    @Override
    public void deleteAllBookmark() { BOOKMARK_SERVICE_DATA.clear(); }

    // 입력된 id 의 bookmark 삭제
    @Override
    public void deleteBookmark(@NonNull String id) { BOOKMARK_SERVICE_DATA.remove(id); }

    @Override
    public void deleteAllInCategory(@NonNull String category) {
        // 입력된 카테고리의 아이템 모두삭제
        for(Bookmark bookmark : Lists.newArrayList(BOOKMARK_SERVICE_DATA.values())){
            if(bookmark.getCategory().equals(category)){
                BOOKMARK_SERVICE_DATA.remove(bookmark.getId());
            }
        }
    }


    /**
     * 입력받은 탭 객체를 Map<String, Bookmark> 캐시 메모리에서 id로 찾아
     * 입력받은 position 값으로 position 값을 갱신한다.
     **/
    @Override
    public void updatePosition(@NonNull Bookmark bookmark, int position) {
        Bookmark newBookmark = new Bookmark(bookmark.getId(),bookmark.getTitle(),
                bookmark.getUrl(),bookmark.getAction(),bookmark.getCategory(),
                position,bookmark.getFaviconUrl());
        BOOKMARK_SERVICE_DATA.put(bookmark.getId(), newBookmark);
    }

    @Override
    public void updatePosition(@NonNull String id, int position) {
        // {@link BookmarksRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
    }
}
