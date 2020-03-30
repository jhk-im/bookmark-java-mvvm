package com.jroomstudio.commentstube.data.source;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jroomstudio.commentstube.data.Tab;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * 데이터 소스에서 Tab 데이터를 캐시로 로드하는 것을 구체적으로 구현
 * - 로컬데이터가 없거나 비어있는 경우 원격 데이터 소스를 사용한다.
 * - 로컬과 서버 데이터 간의 동기화를 구현한다.
 **/
public class TabsRepository implements TabsDataSource {

    /**
     * - TabsRepository 의 INSTANCE
     **/
    private static TabsRepository INSTANCE = null;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * TabsDataSource 를 상속받는 TabsLocalDataSource 인스턴스를 입력받아 셋팅된다.
     **/
    private final TabsDataSource mLocalDataSource;

    /**
     * 해당 클래스가 getInstance 로 인스턴스가 생성될 때
     * TabsDataSource 를 상속받는 TabsRemoteDataSource 인스턴스를 입력받아 셋팅된다.
     **/
    private final TabsDataSource mRemoteDataSource;

    /**
     * 이 변수에 패키지 로컬 visibility(가시성)이 있으므로 테스트에서 액세스할 수 있다.
     **/
    Map<String,Tab> mCachedTabs;

    /**
     * 데이터를 재요청할 때 강제로 업데이트 하도록 캐시를 유효하지 않은것으로 표시한다.
     * 이 변수는 패키지 local visibility 에 있으므로 테스트에서 액세스 할 수 있다.
     **/
    boolean mCacheIsDirty = false;

    // 다이렉트 인스턴스 방지
    private TabsRepository(@NonNull TabsDataSource localDataSource,
                          @NonNull TabsDataSource remoteDataSource) {
        mLocalDataSource = checkNotNull(localDataSource);
        mRemoteDataSource = checkNotNull(remoteDataSource);
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     *
     * @param localDataSource 디바이스 저장소 데이터 소스
     * @param remoteDataSource 백엔드 데이터 소스
     **/
    public static TabsRepository getInstance(TabsDataSource localDataSource,
                                             TabsDataSource remoteDataSource) {
        if(INSTANCE == null){
            INSTANCE = new TabsRepository(localDataSource,remoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * {@link #getInstance(TabsDataSource, TabsDataSource)}
     * 호출될 때 새 인스턴스를 작성하도록 강제하는 데 사용된다.
     **/
    public static void destroyIntance() { INSTANCE = null; }

    /**
     * - TabsRemoteDataSource 에서 Tab 데이터를 액세스한다.
     **/
    private void getTabsFromRemoteDataSource(@NonNull final LoadTabsCallback callback){
        mRemoteDataSource.getTabs(new LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                refreshCache(tabs);
                callback.onTabsLoaded(new ArrayList<>(mCachedTabs.values()));
            }

            @Override
            public void onDataNotAvailable() { callback.onDataNotAvailable(); }
        });
    }

    /**
     * 입력받은 Tab 리스트로 캐시메모리 refresh
     *  mCacheIsDirty = false
     *
     * 캐시 메모리
     * - Map<String, Tab>
     **/
    private void refreshCache(List<Tab> tabs){
        if(mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.clear();
        for(Tab tab : tabs){
            mCachedTabs.put(tab.getId(), tab);
        }
        mCacheIsDirty = false;
    }

    /**
     * - Tab 의 id 를 입력하여 캐시 메모리 에서 Tab 객체를 찾아 반환한다.
     * - Test 시 사용된다.
     *
     * 캐시메모리
     * - Map<String, Tab> mCachedTabs
     **/
    @Nullable
    private Tab getTabWithId(@NonNull String tabId) {
        checkNotNull(tabId);
        if(mCachedTabs == null || mCachedTabs.isEmpty()){
            return null;
        }else{
            return mCachedTabs.get(tabId);
        }
    }


    /*
     * TabsDataSource 오버라이드 메소드 구현
     */

    /**
     * - 저장소 테이블에서 모든 Tab 객체의 정보를 가져온다.
     * - 캐시, 로컬 데이터소스(SQLite) 또는 원격 데이터 소스 중 먼저 사용 가능한 작업을 가져온다.
     *
     * {@link LoadTabsCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        checkNotNull(callback);

        // Map<String, Tab> 이 null 이 아니고 mCacheIsDirty 가 false 일때 즉시 캐시로 응답
        // 즉, remote 나 local 로 부터 데이터를 받아오는데 성공 한 후 캐시 메모리에 저장이 되어있는 상태
        /*
        if (mCachedTabs != null && !mCacheIsDirty) {
            callback.onTabsLoaded(new ArrayList<>(mCachedTabs.values()));
            return;
        }
        */
        // mCacheIsDirty 가 true 이면 TabsRemoteDataSource 로 부터 데이터를 가져온다.
        /*
        if(mCacheIsDirty){
            getTabsFromRemoteDataSource(callback);
        }
        */

        // mCacheIsDirty 가 false 이면 TabsLocalDataSource 로 부터 데이터를 가져온다.
        mLocalDataSource.getTabs(new LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                // 받아온 데이터를 캐쉬 메모리인 Map<String, Tap> 에 refresh 한다.
                refreshCache(tabs);
                callback.onTabsLoaded(new ArrayList<>(mCachedTabs.values()));
            }
            // 로컬에서 데이터 액세스에 실패하면 Remote 로 부터 데이터를 가져온다.
            @Override
            public void onDataNotAvailable() {
                //원격 구현
                //getTabsFromRemoteDataSource(callback);
                callback.onDataNotAvailable();
            }
        });
    }



    /**
     * - 로컬 데이터 저장소 에 액세스 한다
     * - 로컬 테이블이 비어있거나 없으면 네트워크 데이터 소스를 사용한다.
     * - 샘플을 단순화하기 위해 수행된다.
     *
     * {@link GetTabCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getTab(@NonNull final String tabId ,@NonNull final GetTabCallback callback) {
        checkNotNull(tabId);
        checkNotNull(callback);

        // 탭 이름으로 Map<String, Tab> 에서 Tab 을 가져온다.
        Tab cachedTab = getTabWithId(tabId);

        // 캐시에서 응답 가능한 경우 즉시 응답
        if(cachedTab != null){
            callback.onTabLoaded(cachedTab);
            return;
        }

        // 로컬 데이터 소스로부터 단일 Tab 객체를 가져온다.
        mLocalDataSource.getTab(tabId, new GetTabCallback() {
            @Override
            public void onTabLoaded(Tab tab) {
                // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
                if(mCachedTabs == null){
                    mCachedTabs = new LinkedHashMap<>();
                }
                mCachedTabs.put(tab.getId(),tab);
                callback.onTabLoaded(tab);
            }

            // 로컬 데이터 소스가 비어있다면 remote 데이터 소스를 사용한다.
            @Override
            public void onDataNotAvailable() {
                // 원격
                /*
                mRemoteDataSource.getTab(tabId, new GetTabCallback() {
                    @Override
                    public void onTabLoaded(Tab tab) {
                        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
                        if (mCachedTabs == null) {
                            mCachedTabs = new LinkedHashMap<>();
                        }
                        mCachedTabs.put(tab.getId(),tab);
                        callback.onTabLoaded(tab);
                    }

                    @Override
                    public void onDataNotAvailable() { callback.onDataNotAvailable(); }
                });
                */
            }
        });
    }

    /**
     * - 로컬과 원격 데이터 소스에 각각 Tab 객체를 저장한다.
     **/
    @Override
    public void saveTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mLocalDataSource.saveTab(tab);
        mRemoteDataSource.saveTab(tab);

        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
        if (mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getId(),tab);
    }

    /**
     * tabId 를 입력받아 캐시 메모리에서 Tab 객체를 가져와 useTab(Tab) 을 호출한다.
     * - test 에서 활용
     **/
    @Override
    public void useTab(@NonNull String tabId) {
        checkNotNull(tabId);
        useTab(getTabWithId(tabId));
    }

    @Override
    public void useTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mLocalDataSource.useTab(tab);
        mRemoteDataSource.useTab(tab);

        Tab usedTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),true,tab.getPosition());

        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
        if (mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getId(),usedTab);
    }

    /**
     * tabId 를 입력받아 캐시 메모리에서 Tab 객체를 가져와 disableTab(Tab) 을 호출한다.
     * - test 에서 활용
     **/
    @Override
    public void disableTab(@NonNull String tabId) {
        checkNotNull(tabId);
        disableTab(getTabWithId(tabId));
    }

    @Override
    public void disableTab(@NonNull Tab tab) {
        checkNotNull(tab);
        mLocalDataSource.disableTab(tab);
        mRemoteDataSource.disableTab(tab);

        Tab disableTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),false,tab.getPosition());

        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
        if (mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getId(), disableTab);
    }

    /**
     * tabId 를 입력받아 캐시 메모리에서 Tab 객체를 가져와 updatePosition(Tab,int) 을 호출한다.
     * - test 에서 활용
     **/
    @Override
    public void updatePosition(@NonNull String tabId, int position) {
        checkNotNull(tabId);
        checkNotNull(position);
        updatePosition(getTabWithId(tabId),position);
    }

    @Override
    public void updatePosition(@NonNull Tab tab, int position) {
        checkNotNull(tab);
        mLocalDataSource.updatePosition(tab,position);
        mRemoteDataSource.updatePosition(tab,position);
        Tab usedTab = new Tab(tab.getId(),tab.getName(),tab.getViewType(),tab.isUsed(),position);

        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
        if (mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.put(tab.getId(),usedTab);
    }


    @Override
    public void deleteAllTabs() {
        mLocalDataSource.deleteAllTabs();
        mRemoteDataSource.deleteAllTabs();

        // 앱 UI 를 최신 상태로 유지하기 위해 메모리 캐시 업데이트 수행
        if(mCachedTabs == null){
            mCachedTabs = new LinkedHashMap<>();
        }
        mCachedTabs.clear();


    }


}
