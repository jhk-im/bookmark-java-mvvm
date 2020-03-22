package com.jroomstudio.commentstube.data.source.local;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.util.AppExecutors;

public class TabsLocalDataSource implements TabsDataSource {

    private static volatile TabsLocalDataSource INSTANCE;

    private TabsDao mTabsDao;

    private AppExecutors mAppExecutors;

    // 직접 인스턴스화를 방지한다.
    private TabsLocalDataSource(@NonNull AppExecutors appExecutors, TabsDao tabsDao){
        mAppExecutors = appExecutors;
        mTabsDao = tabsDao;
    }

    public static TabsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull TabsDao tabsDao){
        if(INSTANCE == null){
            synchronized (TabsLocalDataSource.class){
                if(INSTANCE == null){
                    INSTANCE =  new TabsLocalDataSource(appExecutors,tabsDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 데이터베이스가 존재하지 않거나 테이블이 비어있으면
     * {@link LoadTabsCallback # onDataNotAvailable ()}이 시작된다
     * */
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {

    }

    /**
     * {@link Tab}를 찾을 수 없으면
     *  {@link GetTabCallback # onDataNotAvailable ()}이 시작된다
     * */
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
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {

    }

    @Override
    public void disabledTab(@NonNull String tabId) {
        // Not required for the local data source because the {@link TasksRepository} handles
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

    @VisibleForTesting
    static void clearInstance() { INSTANCE = null; }
}
