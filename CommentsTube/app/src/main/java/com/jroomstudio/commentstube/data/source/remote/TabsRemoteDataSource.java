package com.jroomstudio.commentstube.data.source.remote;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 네트워크에서 데이터를 받아오는 시뮬레이션 구현
 */
public class TabsRemoteDataSource implements TabsDataSource {

    private static TabsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    private final static Map<String, Tab> TAB_SERVICE_DATA;

    static {
        TAB_SERVICE_DATA = new LinkedHashMap<>(2);
        addTabs("1", "NEWS", "0");
        addTabs("2", "MUSIC", "1");
        addTabs("3", "SPORTS", "2");
        addTabs("4", "MOVIE", "3");
        addTabs("5", "BEST", "4");
        addTabs("6", "LIVE", "5");
        addTabs("8", "SUBSCRIBE", "6");
        addTabs("7", "GAME", "7");
        addTabs("0", "DOCUMENTARY", "8");

    }

    public static TabsRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TabsRemoteDataSource();
        }
        return INSTANCE;
    }

    private TabsRemoteDataSource(){}

    private static void addTabs(String number, String name, String id){
        Tab newTab = new Tab(id,number,name,false);
        TAB_SERVICE_DATA.put(newTab.getId(), newTab);
    }

    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {

    }

    @Override
    public void getTabs(@NonNull String tabId, @NonNull GetTabCallback callback) {

    }

    @Override
    public void saveTab(@NonNull Tab tab) {

    }

    @Override
    public void usedTab(@NonNull Tab tab) {

    }

    @Override
    public void usedTab(@NonNull String tabId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {

    }

    @Override
    public void disabledTab(@NonNull String tabId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void refreshTabs() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTabs() {

    }
}
