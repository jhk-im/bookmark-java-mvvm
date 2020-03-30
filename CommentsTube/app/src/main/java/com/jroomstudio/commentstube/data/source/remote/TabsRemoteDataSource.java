package com.jroomstudio.commentstube.data.source.remote;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 네트워크 지연시간 시뮬레이션을 테스트하는 데이터소스 구현
 **/
public class TabsRemoteDataSource implements TabsDataSource {

    /**
     * - TabsRemoteDataSource 의 INSTANCE
     **/
    private static TabsRemoteDataSource INSTANCE;

    /**
     * - 네트워크 지연시간 시뮬레이션에 사용될 Mills int 변수
     * - 클래스에 존재하는 단하나의 상수 static final int
     **/
    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    /**
     * - test 로 생성된 Tab 객체를 캐시 메모리에 저장
     * key - String name
     * value - tab
     **/
    private static final Map<String, Tab> TAB_SERVICE_DATA = new LinkedHashMap<>(2);
    static {
        addTab("SUBSCRIBE","SUB_FRAGMENT",0);
        addTab("BEST","DEFAULT_FRAGMENT",1);
        addTab("GAME","DEFAULT_FRAGMENT",2);
        addTab("MUSIC","DEFAULT_FRAGMENT",3);
        addTab("MOVIE","DEFAULT_FRAGMENT",4);
        addTab("SPORTS","DEFAULT_FRAGMENT",5);
        addTab("NEWS","DEFAULT_FRAGMENT",6);
        addTab("DOCUMENTARY","DEFAULT_FRAGMENT",7);
    }

    // 다이렉트 인스턴스 방지
    private TabsRemoteDataSource() {}

    /**
     * - TabsRemoteDataSource 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static TabsRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new TabsRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * TEST 용 Tab 객체를 생성하고 Map<String(name), Tab> 으로 추가
     **/
    private static void addTab(String name, String viewType,int position){
        Tab newTab = new Tab(name,viewType, position);
        TAB_SERVICE_DATA.put(newTab.getName(), newTab);
    }

    /*
     * TabsDataSource 오버라이드 메소드 구현
     */

    /**
     * {@link LoadTabsCallback#onDataNotAvailable()} fired 되지 않는다.
     *  - 실제 원격 데이터 소스에서 서버에 접속할 수 없거나 오류를 리턴하면 발생한다.
     **/
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onTabsLoaded(Lists.newArrayList(TAB_SERVICE_DATA.values()));
        }, SERVICE_LATENCY_IN_MILLIS);

    }

    /**
     * {@link GetTabCallback#onDataNotAvailable()} fired 되지 않는다.
     *  - 실제 원격 데이터 소스에서 서버에 접속할 수 없거나 오류를 리턴하면 발생한다.
     **/
    @Override
    public void getTab(@NonNull String tabId ,@NonNull GetTabCallback callback) {
        final Tab tab = TAB_SERVICE_DATA.get(tabId);

        // 실행을 지연시켜 네트워크를 시뮬레이션 한다.
        // Handler -> android.os
        Handler handler = new Handler();
        // Handler.postDelayed(Runnable, delayMillis)
        handler.postDelayed(() -> {
            callback.onTabLoaded(tab);
        }, SERVICE_LATENCY_IN_MILLIS);

    }

    /**
     * 입력받은 Tab 객체를 Map<String, Tab> 캐시 메모리에서 id 로 찾아 저장한다.
     **/
    @Override
    public void saveTab(@NonNull Tab tab) { TAB_SERVICE_DATA.put(tab.getId(), tab); }


    @Override
    public void useTab(@NonNull String tabId) {
        // {@link TabsRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
        // {@code} tabId 로 {@link tab} 을 가져온다
    }

    /**
     * 입력받은 Tab 객체를 Map<String, Tab> 캐시 메모리에서 id 로 찾아 used 를 true 로 저장한다.
     **/
    @Override
    public void useTab(@NonNull Tab tab) {
        Tab useTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),true,tab.getPosition());
        TAB_SERVICE_DATA.put(tab.getId(),useTab);
    }

    @Override
    public void disableTab(@NonNull String tabId) {
        // {@link TabsRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
        // {@code} tabId 로 {@link tab} 을 가져온다
    }

    /**
     * 입력받은 Tab 객체를 Map<String, Tab> 캐시 메모리에서 id 로 찾아 used 를 false 로 저장한다.
     **/
    @Override
    public void disableTab(@NonNull Tab tab) {
        Tab disableTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),false,tab.getPosition());
        TAB_SERVICE_DATA.put(tab.getId(), disableTab);
    }

    @Override
    public void updatePosition(@NonNull Tab tab, int position) {
        //
    }

    @Override
    public void updatePosition(@NonNull String tabId, int position) {
        //
    }

    @Override
    public void deleteAllTabs() {
        TAB_SERVICE_DATA.clear();
    }

}
