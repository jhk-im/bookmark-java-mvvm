package com.jroomstudio.commentstube.data.source;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

public interface TabsDataSource {

    interface LoadTabsCallback {
        void onTabsLoaded(List<Tab> tabs);
        void onDataNotAvailable();
    }

    interface GetTabCallback{
        void onTabLoaded(Tab tab);
        void onDataNotAvailable();
    }

    void getTabs(@NonNull LoadTabsCallback callback);
    void getTabs(@NonNull String tabName, @NonNull GetTabCallback callback);
    void saveTab(@NonNull Tab tab);
    void usedTab(@NonNull Tab tab);
    void usedTab(@NonNull String tabName);
    void disabledTab(@NonNull Tab tab);
    void disabledTab(@NonNull String tabName);
    void refreshTabs();

}
