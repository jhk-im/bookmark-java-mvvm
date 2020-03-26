package com.jroomstudio.commentstube.data.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jroomstudio.commentstube.data.Tab;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TabsRepository implements TabsDataSource {

    private static TabsRepository INSTANCE = null;
    private final TabsDataSource mTabsLocalDataSource;
    private final TabsDataSource mTabsRemoteDataSource;

    /**
     * 해당 변수에 로컬 변수가 있으므로 테스트에서 액세스할 수 있다.
     * */
    Map<String, Tab> mCachedTabs;

    /**
     * 데이터 요청 시 강제로 업데이트 하도록 캐시를 유효하지 않은 것으로 표시.
     * 테스트에서 액세스 할 수 있도록 로컬 변수가 있다.
     * */
    boolean mCacheIsDirty = false;

    // 직접 인스턴스화를 방지한다.
    public TabsRepository(@NonNull TabsDataSource tabsLocalDataSource,
                          @NonNull TabsDataSource tabsRemoteDataSource) {
        mTabsLocalDataSource = checkNotNull(tabsLocalDataSource);
        mTabsRemoteDataSource = checkNotNull(tabsRemoteDataSource);
    }

    /**
     * 단일 인스턴스를 리턴하는 것이 필요할 경우 작성한다.
     * @param tabsLocalDataSource 로컬 데이터베이스
     * @param tabsRemoteDataSource 원격 데이터 베이스
     * @return the {@link TabsRepository} instance
     * */
    public static TabsRepository getInstance(TabsDataSource tabsLocalDataSource,
                                             TabsDataSource tabsRemoteDataSource){
        if(INSTANCE == null){
            INSTANCE = new TabsRepository(tabsLocalDataSource,tabsRemoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * {@link #getInstance(TabsDataSource,TabsDataSource)}가 새 인스턴스 생성 작성하도록 강제하는데 사용한다.
     * */
    public static void destroyInstance(){ INSTANCE = null; }

    /**
     * 캐시, 로컬 데이터 소스 또는 원격 데이터 소스 중 먼저 사용 가능한 작업을 가져온다.
     * 모든 데이터소스가 데이터를 가져오지 못한다면
     * {@link LoadTabsCallback # onDataNotAvailable()} 이 시작된다.
     * */
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        checkNotNull(callback);

        if(mCachedTabs != null && !mCacheIsDirty){
            callback.onTabsLoaded(new ArrayList<>(mCachedTabs.values()));
            return;
        }

        if(mCacheIsDirty)
        {
            //cache 가 더러운 상태면 네트워크에서 새로운 데이터를 가져온다.
            getTabsFromRemoteDataSource(callback);
        }else{
            // 사용 가능한 경우 로컬 스토리지를 조회한다. 그렇지 않은 경우 네트워크를 조회한다.
            mTabsLocalDataSource.getTabs(new LoadTabsCallback() {

                @Override
                public void onTabsLoaded(List<Tab> tabs) {
                    refreshCache(tabs);
                    callback.onTabsLoaded(new ArrayList<>(mCachedTabs.values()));
                }

                @Override
                public void onDataNotAvailable() { getTabsFromRemoteDataSource(callback); }
            });
        }
    }

    private void getTabsFromRemoteDataSource(@NonNull final LoadTabsCallback callback){
        mTabsRemoteDataSource.getTabs(new LoadTabsCallback() {

            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                refreshCache(tabs);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Tab> tabs){
        if (mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.clear();
        for(Tab tab : tabs){
            mCachedTabs.put(tab.getId(), tab);
        }
        mCacheIsDirty = false;
    }



    /**
     * 테이블이 비어있거나 비어있지 않은 경우 로컬 데이터 소스에서 작업을 가져온다.
     * 샘플을 단순화하기 위해 실행된다.
     * 데이터 소스를 가져오는데 실패하면
     * {@link GetTabCallback # onDataNotAvailable ()}이 실행된다.
    * */
    @Override
    public void getTabs(@NonNull final String tabName, @NonNull final GetTabCallback callback) {
        checkNotNull(tabName);
        checkNotNull(callback);

        Tab cachedTab = getTabWithName(tabName);

        // 가능한 경우 캐시로 즉시 응답
        if (cachedTab != null){
            callback.onTabLoaded(cachedTab);
            return;
        }

        //서버에서 로드가 필요한 경우

        // 로컬데이터소스에 tab 정보가 있는가? 그렇지 않은 경우 네트워크를 쿼리한다.
        mTabsLocalDataSource.getTabs(tabName, new GetTabCallback() {
            @Override
            public void onTabLoaded(Tab tab) {
                //앱 UI를 최신 상태로 유지하려면 메모리 캐시 업데이트를 수행
                if(mCachedTabs == null){
                    mCachedTabs = new LinkedHashMap<>();
                }
                mCachedTabs.put(tab.getName(), tab);
                callback.onTabLoaded(tab);
            }

            @Override
            public void onDataNotAvailable() {
            mTabsRemoteDataSource.getTabs(tabName, new GetTabCallback() {
                @Override
                public void onTabLoaded(Tab tab) {
                    //앱 UI를 최신 상태로 유지하려면 메모리 캐시 업데이트를 수행
                    if(mCachedTabs == null){
                        mCachedTabs = new LinkedHashMap<>();
                    }
                    mCachedTabs.put(tab.getName(), tab);
                    callback.onTabLoaded(tab);
                }

                @Override
                public void onDataNotAvailable() { callback.onDataNotAvailable(); }
            });
            }
        });
    }

    @Override
    public void saveTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mTabsRemoteDataSource.saveTab(tab);
        mTabsLocalDataSource.saveTab(tab);

        //앱 UI를 최신 상태로 유지하려면 메모리 캐시 업데이트를 수행
        if(mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getName(), tab);
    }

    @Override
    public void usedTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mTabsLocalDataSource.usedTab(tab);
        mTabsRemoteDataSource.usedTab(tab);

        Tab usedTab = new Tab(tab.getId(),
                tab.getName(),
                tab.getViewType(),
                false);

        //앱 UI를 최신 상태로 유지하려면 메모리 캐시 업데이트를 수행
        if(mCachedTabs == null) {
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getName(), usedTab);
    }

    @Override
    public void usedTab(@NonNull String tabName) {
        checkNotNull(tabName);
        usedTab(getTabWithName(tabName));
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mTabsLocalDataSource.disabledTab(tab);
        mTabsRemoteDataSource.disabledTab(tab);

        Tab disableTab = new Tab(
                tab.getId(),
                tab.getName(),
                tab.getViewType(),
                true);
        //앱 UI를 최신 상태로 유지하려면 메모리 캐시 업데이트를 수행
        if(mCachedTabs == null) {
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getName(), disableTab);
    }

    @Override
    public void disabledTab(@NonNull String tabName) {
        checkNotNull(tabName);
        disabledTab(getTabWithName(tabName));
    }

    @Nullable
    private Tab getTabWithName(@NonNull String name){
        checkNotNull(name);
        if(mCachedTabs == null || mCachedTabs.isEmpty()){
            return null;
        }else{
            return mCachedTabs.get(name);
        }
    }

    @Override
    public void refreshTabs() { mCacheIsDirty = true; }

}
