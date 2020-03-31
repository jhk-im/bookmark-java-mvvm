package com.jroomstudio.commentstube.data.source;

import com.google.common.collect.Lists;
import com.jroomstudio.commentstube.data.Tab;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * 캐시를 사용하여 메모리 내 저장소를 구현하기위한 단위 테스트.
 **/
public class TabsRepositoryTest {

    private final static String TAB_NAME = "SUBSCRIBE";
    private final static String TAB_NAME1 = "GAME";
    private final static String TAB_NAME2 = "BEST";


    private static List<Tab> TABS =
            Lists.newArrayList(new Tab("SUBSCRIBE","SUB_FRAG",0),
                    new Tab("BEST","MAIN_FRAG",1));

    private TabsRepository mTabsRepository;

    @Mock
    private TabsDataSource mLocalDataSource;

    @Mock
    private TabsDataSource mRemoteDataSource;

    @Mock
    private TabsDataSource.GetTabCallback mGetTabCallback;

    @Mock
    private TabsDataSource.LoadTabsCallback mLoadTabsCallback;

    @Captor
    private ArgumentCaptor<TabsDataSource.LoadTabsCallback> mLoadTabsCallbackCaptor;

    @Captor ArgumentCaptor<TabsDataSource.GetTabCallback> mGetTabCallbackCaptor;

    /**
     * Mockito 는 @Mock 주석을 사용하여 mock 을 주입하는 편리한 방법을 제공한다.
     * 테스트에 mock 을 주입하여 initMocks 메소드를 호출해야한다.
     **/
    @Before
    public void setupTabsRepository() {
        MockitoAnnotations.initMocks(this);
        // 테스트중인 클래스에 대해 참조를 얻는다.
        mTabsRepository = TabsRepository.getInstance(mLocalDataSource,mRemoteDataSource);
    }

    @After
    public void destroyRepositoryInstance() { TabsRepository.destroyInstance(); }

    /**
     * 두개의 호출이 Tab 저장소에 발행된 경우
     * Callback 을 캡처하는 설정 Captor 가 주어진다.
     **/
    @Test
    public void getTabs_repositoryCachesAfterFirstApiCall(){
        towTabsLoadCallsToRepository(mLoadTabsCallback);

        // 서비스 API 에 데이터를 한번만 요청
        verify(mRemoteDataSource).getTabs(any(TabsDataSource.LoadTabsCallback.class));
    }

    /**
     * getTabs
     **/
    @Test
    public void getTabs_requestsAllTabsFromLocalDataSource(){

        // 데이터 저장소에서 작업이 요청됨
        mTabsRepository.getTabs(mLoadTabsCallback);
        // 로컬 데이터베이스 소스에서 데이터가 로드된다.
        verify(mLocalDataSource).getTabs(any(TabsDataSource.LoadTabsCallback.class));

    }

    /**
     * getTab
     **/
    @Test
    public void getTab_requestsSingleTabFromLocalDataSource(){
        // 로컬 저장소에 데이터가 요청된경우
        mTabsRepository.getTab(TAB_NAME, mGetTabCallback);

        // 데이터베이스에서 Tab 데이터가 로드된다.
        verify(mLocalDataSource).getTab(eq(TAB_NAME), any(
                TabsDataSource.GetTabCallback.class
        ));
    }

    /**
     * saveTabs
     **/
    @Test
    public void saveTabs_savesTabToServiceAPI() {

        // Tab 이름, ViewType, position 값이 있는 Tab 객체
        Tab newTab = new Tab(TAB_NAME,"SUB_FRAG",0);

        // 생성된 Tab 이 데이터 저장소에 저장된 경우
        mTabsRepository.saveTab(newTab);

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).saveTab(newTab);
        verify(mLocalDataSource).saveTab(newTab);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));

    }

    /**
     * useTab
     **/
    @Test
    public void useTab_usesTabToServiceAPIUpdatesCache(){

        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 을 활성화 상태로 업데이트
        mTabsRepository.useTab(newTab);

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).useTab(newTab);
        verify(mLocalDataSource).useTab(newTab);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).isUsed(), is(true));

    }

    /**
     * useTabId
     **/
    @Test
    public void useTabId_usesTabToServiceAPIUpdatesCache(){

        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 의 ID 를 사용하여 활성화 상태로 업데이트
        mTabsRepository.useTab(newTab.getId());

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).useTab(newTab);
        verify(mLocalDataSource).useTab(newTab);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).isUsed(), is(true));
    }

    /**
     * disableTab
     **/
    @Test
    public void disableTab_disablesTabToServiceAPIUpdatesCache(){

        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 객체를 비활성화 상태로 업데이트
        mTabsRepository.disableTab(newTab);

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).disableTab(newTab);
        verify(mLocalDataSource).disableTab(newTab);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).isUsed(), is(false));
    }

    /**
     * disableTabId
     **/
    @Test
    public void disableTabId_disablesTabToServiceAPIUpdatesCache(){

        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 의 ID 를 사용하여 비활성화 상태로 업데이트
        mTabsRepository.disableTab(newTab.getId());

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).disableTab(newTab);
        verify(mLocalDataSource).disableTab(newTab);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).isUsed(), is(false));

    }

    /**
     * updatePosition
     **/
    @Test
    public void updatePosition_updatePositionTabToServiceAPIUpdatesCache(){
        //Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME,"SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 객체의 POSITION 값을 변경
        mTabsRepository.updatePosition(newTab,1);

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).updatePosition(newTab,1);
        verify(mLocalDataSource).updatePosition(newTab,1);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).getPosition(), is(1));

    }

    /**
     * updatePositionId
     **/
    @Test
    public void updatePositionId_updatesPositionTabToServiceAPIUpdatesCache(){
        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);

        // Tab 의 id 로 객체의 position 값을 변경
        mTabsRepository.updatePosition(newTab.getId(),1);

        // 서비스 api 및 영구 저장소가 호출되고 캐시가 업데이트 된다.
        verify(mRemoteDataSource).updatePosition(newTab,1);
        verify(mLocalDataSource).updatePosition(newTab,1);
        assertThat(mTabsRepository.mCachedTabs.size(), is(1));
        assertThat(mTabsRepository.mCachedTabs.get(newTab.getId()).getPosition(), is(1));
    }

    /**
     * deleteAllTabs
     **/
    @Test
    public void deleteAllTabs_deleteTabsToServiceAPIUpdatesCache(){
        // Tab 객체 생성하고 추가
        Tab newTab = new Tab(TAB_NAME, "SUB_FRAG",0);
        mTabsRepository.saveTab(newTab);
        Tab newTab1 = new Tab(TAB_NAME1, "MAIN_FRAG",1);
        mTabsRepository.saveTab(newTab1);
        Tab newTab2 = new Tab(TAB_NAME2, "MAIN_FRAG",2);
        mTabsRepository.saveTab(newTab2);

        // 모든 Tab 정보를 데이터에서 삭제한다.
        mTabsRepository.deleteAllTabs();

        // 데이터 소스가 호출되었는지 확인
        verify(mRemoteDataSource).deleteAllTabs();
        verify(mLocalDataSource).deleteAllTabs();

        assertThat(mTabsRepository.mCachedTabs.size(), is(0));
    }

    /**
     * CacheRefresh 가 false 이면 데이터베이스에서 탭정보를 받아와서
     * mCacheTabs 를 업데이트 해야한다.
     **/
    @Test
    public void getTabsWithCacheRefresh_tabsCachedTabsRefresh(){

        //로컬 혹은 원격에서 데이터를 가져온 후 캐시 메모리에 refresh 완료가 필요함을 알림
        mTabsRepository.refreshTabs();
        mTabsRepository.getTabs(mLoadTabsCallback);

        setTabsAvailable(mLocalDataSource, TABS);

        // 로컬에서 데이터가 반환되는지 확인
        verify(mLocalDataSource, never()).getTabs(mLoadTabsCallback);
        verify(mLoadTabsCallback).onTabsLoaded(TABS);
    }

    /**
     * 데이터 로드에 실패했을 때 onDataNotAvailable() 의 동작을 테스트
     **/
    @Test
    public void getTabsWithUnavailable_firesOnDataUnavailable(){

        // TEST 아이디 ,
        final String tabId = "123";

        // getTab 호출
        mTabsRepository.getTab(tabId, mGetTabCallback);

        // 로컬 및 원격 데이터 소스에 사용가능한 데이터가 없음을 반환
        setTabNotAvailable(mLocalDataSource,tabId);
        setTabNotAvailable(mRemoteDataSource,tabId);

        // 데이터 반환확인
        verify(mGetTabCallback).onDataNotAvailable();

    }


    // Test 메소드

    private void setTabNotAvailable(TabsDataSource dataSource, String tabId){
        verify(dataSource).getTab(eq(tabId), mGetTabCallbackCaptor.capture());
        mGetTabCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTabsAvailable(TabsDataSource dataSource, List<Tab> tabs){
        verify(dataSource).getTabs(mLoadTabsCallbackCaptor.capture());
        mLoadTabsCallbackCaptor.getValue().onTabsLoaded(tabs);
    }


    /**
     * Tab 저장소에 두번의 호출을 발행하는 메소드
     **/
    private void towTabsLoadCallsToRepository(TabsDataSource.LoadTabsCallback callback){

        // 저장소에 작업을 요청한 경우
        mTabsRepository.getTabs(callback); // 첫번째 API CALL

        // Mockito Captor 를 사용하여 callback 캡쳐
        verify(mLocalDataSource).getTabs(mLoadTabsCallbackCaptor.capture());

        // Local Database 가 아직 비어있을 때
        mLoadTabsCallbackCaptor.getValue().onDataNotAvailable();


        // 원격 데이터 소스가 조회되었는지 확인
        verify(mRemoteDataSource).getTabs(mLoadTabsCallbackCaptor.capture());

        // 데이터가 캐시 되도록 콜백 트리거
        mLoadTabsCallbackCaptor.getValue().onTabsLoaded(TABS);

        mTabsRepository.getTabs(callback); // 두번째 API CALL


    }

}
