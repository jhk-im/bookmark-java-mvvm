package com.jroomstudio.commentstube.data;


import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.jroomstudio.commentstube.data.source.TabsDataSource;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 쉬운 테스트를 위해 데이터에 정적으로 액세스 할 수 있는 원격 데이터 소스 구현
 **/
public class FakeTabsRemoteDataSource implements TabsDataSource {

    /**
     * - FakeRemoteDataSource 의 INSTANCE
     **/
    private static FakeTabsRemoteDataSource INSTANCE;

    /**
     * 생성된 Tab 객체를 캐시 메모리에 저장
     * key - String name
     * value - tab
     **/
    private static final Map<String, Tab> TABS_SERVICE_DATA = new LinkedHashMap<>();

    // 다이렉트 인스턴스 방지
    private FakeTabsRemoteDataSource() {}

    /**
     * - FakeRemoteDataSource 인스턴스 생성 메소드
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     **/
    public static FakeTabsRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new FakeTabsRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * TEST 용 Tab 객체를 생성하고 Map<String(name), Tab> 에 추가
     **/
    @VisibleForTesting
    public void addTabs(Tab... tabs){
        if(tabs != null){
            for(Tab tab : tabs){
                TABS_SERVICE_DATA.put(tab.getId(), tab);
            }
        }
    }

    /*
     * TabsDataSource 오버라이드 메소드 구현
     */

    /**
     * Map<String, Tab> 을 리스트로 만들어서 onTabsLoaded() 에 입력
     **/
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        callback.onTabsLoaded(Lists.newArrayList(TABS_SERVICE_DATA.values()));
    }

    /**
     * Map<String, Tab> 에서 id 로 tab 을 가져와 서 onTabLoaded() 에 입력
     **/
    @Override
    public void getTab(@NonNull String tabId, @NonNull GetTabCallback callback) {
        Tab tab = TABS_SERVICE_DATA.get(tabId);
        callback.onTabLoaded(tab);
    }

    /**
     * Map<String, Tab> 에 입력받은 Tab 의 id 와 Tab 객체를 저장
     **/
    @Override
    public void saveTab(@NonNull Tab tab) { TABS_SERVICE_DATA.put(tab.getId(), tab); }

    @Override
    public void useTab(@NonNull String tabId) {
        //원격 데이터 소스에는 필요하지 않음
    }

    /**
     * 입력받은 객체의 used 를 true 로 update 한다.
     **/
    @Override
    public void useTab(@NonNull Tab tab) {
        Tab useTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),true);
        TABS_SERVICE_DATA.put(tab.getId(), useTab);
}

    @Override
    public void disableTab(@NonNull String tabId) {
        //원격 데이터 소스에는 필요하지 않음
    }

    /**
     * 입력받은 객체의 used 를 false 로 update 한다.
     **/
    @Override
    public void disableTab(@NonNull Tab tab) {
        Tab disableTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),false);
        TABS_SERVICE_DATA.put(tab.getId(), disableTab);
    }


    @Override
    public void deleteAllTabs() {

    }

}
