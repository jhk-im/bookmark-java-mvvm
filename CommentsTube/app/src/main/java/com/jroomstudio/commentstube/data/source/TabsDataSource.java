package com.jroomstudio.commentstube.data.source;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

/**
 * Tab 데이터에 액세스 하기 위한 진입점
 **/
public interface TabsDataSource {

    /**
     * Tab 리스트 데이터에 액세스 할 때 구현하는 메소드
     * - TabsDataSource 를 상속받은 클래스에서 getTabs() 를 구현할 때
     *   loadTabsCallback 을 입력하여 2개의 메소드를 오버라이딩 한다.
     *   - onTabsLoaded() 는 데이터 베이스로부터 Tab 리스트를 load 하는 것을 구현한다.
     *   - onDataNotAvailable() 는 액세스에 실패 했을 경우를 구현한다.
     **/
    interface LoadTabsCallback {
        void onTabsLoaded(List<Tab> tabs);
        void onDataNotAvailable();
    }

    /**
     * Tab 객체의 데이터에 액세스 할 때 구현하는 메소드
     * - TabsDataSource 를 상속받은 클래스에서 getTab() 를 구현할 때
     *   GetTabCallback 을 입력하여 2개의 메소드를 오버라이딩 한다.
     *   - onTabLoaded() 는 데이터 베이스로부터 Tab 객체를 Load 하는것을 구현한다.
     *   - onDataNotAvailable() 는 액세스에 실패 했을 경우를 구현한다.
     **/
    interface GetTabCallback{
        void onTabLoaded(Tab tab);
        void onDataNotAvailable();
    }

    // Tabs 리스트 데이터에 액세스
    void getTabs(@NonNull LoadTabsCallback callback);

    // Tab 단일 객체 데이터에 액세스
    void getTab(@NonNull String name, @NonNull GetTabCallback callback);

    // Tab 단일 객체를 데이터 베이스에 저장
    void saveTab(@NonNull Tab tab);

    // Tab 단일 객체 중 used 가 true 면서 입력된 tabName 에 해당하는 객체에 액세스
    void useTab(@NonNull String tabId);

    // Tab 단일 객체 중 used 가 true 인 객체에 액세스
    void useTab(@NonNull Tab tab);


    // Tab 단일 객체 중 used 가 false 인 객체에 액세스
    void disableTab(@NonNull Tab tab);
    // Tab 단일 객체 중 used 가 false 면서 입력된 tabName 에 해당하는 객체에 액세스
    void disableTab(@NonNull String tabId);

    // tab 포지션값 변경
    void updatePosition(@NonNull Tab tab , int position);

    void updatePosition(@NonNull String tabId, int position);

    // 모든 Tab 테이블에서 삭제
    void deleteAllTabs();

}
