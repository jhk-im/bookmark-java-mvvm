package com.jroomstudio.commentstube.data.source;

import androidx.annotation.NonNull;

import com.jroomstudio.commentstube.data.Tab;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TabsRepository implements TabsDataSource {

    private static TabsRepository INSTANCE = null;
    private final TabsDataSource mTabsLocalDataSource;

    private final TabsDataSource mTabsRemoteDataSource;

    /**
     * 해당 변수에 로컬 변수가 있으므로 테스트에서 액세스할 수 있다.
     * */
    Map<String, Tab> mChacedTabs;

    /**
     * 데이터 요청 시 강제로 업데이트 하도록 캐시를 유효하지 않은 것으로 표시.
     * 테스트에서 액세스 할 수 있도록 로컬 변수가 있다.
     * */
    boolean mCacheIsDirty = false;

    // 직접 인스턴스화를 방지한다.
    public TabsRepository(@NonNull TabsDataSource tabsLocalDataSource, @NonNull TabsDataSource tabsRemoteDataSource) {
        mTabsLocalDataSource = checkNotNull(tabsLocalDataSource);
        mTabsRemoteDataSource = checkNotNull(tabsRemoteDataSource);
    }

    /**
     * 단일 인스턴스를 리턴하는 것이 필요할 경우 작성한다.
     * @param tabsLocalDataSource 로컬 데이터베이스
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
    }

    /**
     * 테이블이 비어있거나 비어있지 않은 경우 로컬 데이터 소스에서 작업을 가져온다.
     * 샘플을 단순화하기 위해 실행된다.
     * 데이터 소스를 가져오는데 실패하면
     * {@link GetTabCallback # onDataNotAvailable ()}이 실행된다.
    * */
    @Override
    public void getTabs(@NonNull String tabId, @NonNull final GetTabCallback callback) {
        checkNotNull(tabId);
        checkNotNull(callback);
    }

    @Override
    public void saveTab(@NonNull Tab tab) {
        checkNotNull(tab);
    }

    @Override
    public void usedTab(@NonNull Tab tab) {
        checkNotNull(tab);
    }

    @Override
    public void usedTab(@NonNull String tabId) {
        checkNotNull(tabId);
    }

    @Override
    public void disabledTab(@NonNull Tab tab) {
        checkNotNull(tab);
    }

    @Override
    public void disabledTab(@NonNull String tabId) {
        checkNotNull(tabId);
    }

    @Override
    public void refreshTabs() {

    }

    @Override
    public void deleteAllTabs() {

    }
}
