package com.jroomstudio.commentstube.data;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.jroomstudio.commentstube.data.source.TabsDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

public class FakeTabsRemoteDataSource implements TabsDataSource {

    private static FakeTabsRemoteDataSource INSTANCE;

    private static final Map<String, Tab> TABS_SERVICE_DATA = new LinkedHashMap<>();

    private FakeTabsRemoteDataSource() {}

    public static FakeTabsRemoteDataSource getInstance() {
        if(INSTANCE == null){
            INSTANCE = new FakeTabsRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        callback.onTabsLoaded(Lists.newArrayList(TABS_SERVICE_DATA.values()));
    }

    @Override
    public void getTabs(@NonNull String tabName, @NonNull GetTabCallback callback) {
        Tab tab = TABS_SERVICE_DATA.get(tabName);
        callback.onTabLoaded(tab);
    }

    @Override
    public void saveTab(@NonNull Tab tab) { TABS_SERVICE_DATA.put(tab.getName(),tab); }

    @Override
    public void usedTab(@NonNull Tab tab) {
        Tab usedTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),true);
        TABS_SERVICE_DATA.put(tab.getName(),usedTab);
    }

    @Override
    public void usedTab(@NonNull String tabName) {
        // Not required for the remote data source.
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {
        Tab disalbeTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),false);
        TABS_SERVICE_DATA.put(tab.getName(),disalbeTab);
    }

    @Override
    public void disabledTab(@NonNull String tabName) {
        // Not required for the remote data source.
    }

    @Override
    public void refreshTabs() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @VisibleForTesting
    public void addTabs(Tab... tabs){
        if(tabs != null){
            for(Tab tab : tabs){
                TABS_SERVICE_DATA.put(tab.getName(), tab);
            }
        }
    }
}
