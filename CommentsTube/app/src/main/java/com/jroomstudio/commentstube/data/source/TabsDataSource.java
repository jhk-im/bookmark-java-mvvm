package com.jroomstudio.commentstube.data.source;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.Tab;

import java.util.ArrayList;

public interface TabsDataSource {

    interface LoadTabsCallback {
        void onTabsLoaded(ArrayList<Tab> tabs);
        void onDataNotAvailable();
    }

    interface GetTabCallback{
        void onTabLoaded(Tab tab);
        void onDataNotAvailable();
    }

    void getTabs(@NonNull LoadTabsCallback callback);
    void getTabs(@NonNull String tabId, @NonNull GetTabCallback callback);
    void saveTab(@NonNull Tab tab);
    void usedTab(@NonNull Tab tab);
    void usedTab(@NonNull String tabId);
    void disabledTab(@NonNull Tab tab);
    void disabledTab(@NonNull String tabId);
    void refreshTabs();
    void deleteAllTabs();

}
