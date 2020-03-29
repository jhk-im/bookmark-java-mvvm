package com.jroomstudio.commentstube.data.source.local;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.data.source.remote.TabsRemoteDataSource;
import com.jroomstudio.commentstube.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 데이터 소스를 구체적인 DB로 구현한다.
 **/
public class TabsLocalDataSource implements TabsDataSource {

    /**
     * - TabsLocalDataSource 의 INSTANCE
     * - volatile 은 Java 변수를 Main memory 에 저장하겠다는 것을 명시하는 것
     **/
    private static volatile TabsLocalDataSource INSTANCE;

    /**
     * - 데이터베이스의 tab 테이블을 쿼리문으로 접근하여 데이터를 제어할 수 있게 해주는 인터페이스
     * - TabsLocalDataSource 를 getInstance 메소드로 생성할 때 입력받아 멤버 변수로 셋팅한다.
     **/
    private TabsDao mTabsDao;

    /**
     * - 데이터베이스 작업 시 사용되는 쓰레드를 관리하는 Executor 프레임워크가 구현되어있다.
     * - TabsLocalDataSource 를 getInstance 메소드로 생성할 때 입력받아 멤버 변수로 셋팅한다.
     **/
    private AppExecutors mAppExecutors;

    // 다이렉트 인스턴스 방지
    private TabsLocalDataSource(@NonNull AppExecutors appExecutors,@NonNull TabsDao tabsDao){
        mAppExecutors = appExecutors;
        mTabsDao = tabsDao;
    }

    /**
     * 싱글 인스턴스를 리턴한다.
     * - TabsLocalDataSource 인스턴스 생성 메소드
     * - getInstance 메소드에 AppExecutors, TabsDao 를 입력받는다.
     * - INSTANCE null 체크
     * - null 이면 new 키워드로 private 생성자로 인스턴스를 생성한다.
     * - 생성된 인스턴스를 반환한다.
     *
     * @param appExecutors 데이터 액세스를 실행 할 쓰레드를 관리하는 인스턴스
     * @param tabsDao 데이터 베이스에서 쿼리문으로 제어하는 인터페이스 인스턴스
     **/
    public static TabsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull TabsDao tabsDao){
        if(INSTANCE == null){
            synchronized (TabsLocalDataSource.class) {
                if(INSTANCE == null){
                    INSTANCE = new TabsLocalDataSource(appExecutors, tabsDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * INSTANCE 를 null 로
     **/
    @VisibleForTesting
    static void clearInstance() { INSTANCE = null; }



    /*
     * TabsDataSource 오버라이드 메소드 구현
     */

    /**
     * - AppExecutor 와 TabsDao 가 셋팅된 인스턴스로 로컬 데이터에 접근하여 액세스한다.
     *
     * {@link LoadTabsCallback#onDataNotAvailable()}
     * 모든 데이터 소스가 데이터를 가져오지 못하면 실행된다.
     **/
    @Override
    public void getTabs(@NonNull LoadTabsCallback callback) {
        Runnable runnable = () -> {
            final List<Tab> tabs = mTabsDao.getAllTabs();
            mAppExecutors.getMainThread().execute(() -> {
                // 새 테이블 이거나 비어있는 경우
                if(tabs.isEmpty()){
                    callback.onDataNotAvailable();
                }else{
                    callback.onTabsLoaded(tabs);
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    /**
     * - tabId 로 로컬 데이터에 접근하여 객체를 가져온다
     * - AppExecutor 와 TabsDao 로 액세스 한다.
     *
     * {@link Tab} 을 찾을 수 없으면
     * {@link GetTabCallback#onDataNotAvailable()} 을 실행한다.
     **/
    @Override
    public void getTab(@NonNull String tabId,@NonNull GetTabCallback callback) {
        Runnable runnable = () -> {
            final Tab tab = mTabsDao.getTabById(tabId);
            mAppExecutors.getMainThread().execute(() -> {
                // id 에 해당하는 아이템이 있는경우
                if(tab != null){
                    callback.onTabLoaded(tab);
                }
                else{
                    callback.onDataNotAvailable();
                }
            });
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    /**
     * 데이터 베이스에 입력받은 Tab 객체를 insert 한다.
     **/
    @Override
    public void saveTab(@NonNull Tab tab) {
        checkNotNull(tab);
        Runnable runnable = () -> {
            mTabsDao.insertTab(tab);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    /**
     * Tab 객체를 입력받아 TabsDao 의 updateUsed 메소드에 tabId 로 접근한다.
     * used 를 true 로 update 한다.
     **/
    @Override
    public void useTab(@NonNull Tab tab) {
        Runnable runnable = () -> {
            mTabsDao.updateUsed(tab.getId(),true);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void useTab(@NonNull String tabId) {
         // {@link TabsRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
         // {@code} tabId 로 {@link tab} 을 가져온다
    }

    /**
     * Tab 객체를 입력받아 TabsDao 의 updateUsed 메소드에 tabId 로 접근한다.
     * used 를 false 로 update 한다.
     **/
    @Override
    public void disableTab(@NonNull Tab tab) {
        Runnable runnable = () -> {
          mTabsDao.updateUsed(tab.getId(), false);
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void disableTab(@NonNull String tabId) {
         // {@link TabsRepository} 에서 처리하므로 이곳에서는 필요하지 않다.
         // {@code} tabId 로 {@link tab} 을 가져온다
    }

    @Override
    public void deleteAllTabs() {
        Runnable runnable = () -> {
            mTabsDao.deleteAllTabs();
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

}
