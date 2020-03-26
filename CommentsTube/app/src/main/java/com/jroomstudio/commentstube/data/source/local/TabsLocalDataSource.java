package com.jroomstudio.commentstube.data.source.local;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Tab> tabs = mTabsDao.getAllTabs();
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(tabs.isEmpty()){
                            callback.onDataNotAvailable();
                        }else{
                            callback.onTabsLoaded(tabs);
                        }
                    }
                });
            }
        };
    }

    /**
     * {@link Tab}를 찾을 수 없으면
     *  {@link GetTabCallback # onDataNotAvailable ()}이 시작된다
     * */
    @Override
    public void getTabs(@NonNull String tabName, @NonNull GetTabCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Tab tab = mTabsDao.getTabByName(tabName);

                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tab != null){
                            callback.onTabLoaded(tab);
                        }else{
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void saveTab(@NonNull Tab tab) {
        checkNotNull(tab);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTabsDao.insertTab(tab);
            }
        };
        mAppExecutors.getDiskIO().execute(saveRunnable);
    }

    @Override
    public void usedTab(@NonNull Tab tab) {
        Runnable usedRunnable = new Runnable() {
            @Override
            public void run() {
                mTabsDao.updateUsed(tab.getName(),true);
            }
        };
        mAppExecutors.getDiskIO().execute(usedRunnable);
    }

    @Override
    public void usedTab(@NonNull String tabName) {
        // {@link TabsRepository}가 처리하기 때문에 로컬 데이터 소스에는 필요하지 않음
        // 캐시 된 데이터를 사용하여 {@code tabName}에서 {@link task}로 변환
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {
        Runnable disableRunnable = new Runnable() {
            @Override
            public void run() {
                mTabsDao.updateUsed(tab.getName(), false);
            }
        };
        mAppExecutors.getDiskIO().execute(disableRunnable);
    }

    @Override
    public void disabledTab(@NonNull String tabId) {
        // {@link TabsRepository}가 처리하기 때문에 로컬 데이터 소스에는 필요하지 않음
        // 캐시 된 데이터를 사용하여 {@code tabName}에서 {@link task}로 변환
    }

    @Override
    public void refreshTabs() {
        // {@link TabsRepository}가 리프레시 로직을 처리하므로 필요하지 않음
    }

    @VisibleForTesting
    static void clearInstance() { INSTANCE = null; }
}
