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
        addTabs("1", "NEWS", "DEFAULT_VIEW",true);
        addTabs("2", "MUSIC", "DEFAULT_VIEW",true);
        addTabs("3", "SPORTS", "DEFAULT_VIEW",true);
        addTabs("4", "MOVIE", "DEFAULT_VIEW",true);
        addTabs("5", "BEST", "DEFAULT_VIEW",true);
        addTabs("6", "LIVE", "DEFAULT_VIEW",true);
        addTabs("8", "SUBSCRIBE", "SECOND_VIEW",true);
        addTabs("7", "GAME", "DEFAULT_VIEW",true);
        addTabs("0", "DOCUMENTARY","DEFAULT_VIEW",true);

    }

    public static TabsRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TabsRemoteDataSource();
        }
        return INSTANCE;
    }

    private TabsRemoteDataSource(){}

    private static void addTabs(String name,
                                String id,
                                String viewType,
                                boolean used){
        Tab newTab = new Tab(id,name,viewType,used);
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
    public void usedTab(@NonNull String tabName) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {

    }

    @Override
    public void disabledTab(@NonNull String tabName) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void refreshTabs() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

}
