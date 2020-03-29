package com.jroomstudio.commentstube.main;

import android.content.Context;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.google.common.collect.Lists;
import com.jroomstudio.commentstube.BR;
import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.data.source.TabsRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * - 메인 액티비티에서 사용할 데이터를 노출한다.
 *
 * {@link BaseObservable}
 * - 속성이 변경될 때 알림을 받는 리스너 등록 메커니즘을 구현
 * {@link androidx.databinding.Bindable}
 * - 해당 주석을 속성의 getter 메서드에 할당하여 수행한다.
 **/
public class MainActViewModel extends BaseObservable {

    /**
     * Observable
     * - 뷰를 관찰하여 자동으로 뷰를 업데이트한다.
    **/
    // 뷰페이저에 추가되는 tab 아이템 리스트
    public final ObservableList<Tab> tabItems = new ObservableArrayList<>();

    /**
     * leak 을 피하려면 응용 프로그램 context 여야 한다.
     **/
    private Context mContext;

    /**
     * 로컬, 원격 에서 데이터를 액세스
     **/
    private TabsRepository mTabsRepository;

    /**
     * Main Activity ViewModel 생성자
     * @param repository - 로컬, 원격 데이터 액세스
     * @param context - 응용프로그램 context 를 강제로 사용함
     **/
    public MainActViewModel(TabsRepository repository, Context context){
        mTabsRepository = repository;
        mContext = context.getApplicationContext();
    }

    @Bindable
    public  boolean isEmpty() { return tabItems.isEmpty(); }

    /**
     * MainActivity 의 onResume 에서 호출
     * loadTabs(boolean forceUpdate) false 입력
     **/
    public void start(){
        loadTabs(false);
        //mTabsRepository.deleteAllTabs();
    }

    public void loadTabs(boolean forceUpdate){ loadTabs(forceUpdate, true); }

    private void loadTabs(boolean forceUpdate, final boolean showLoadingUI){

        mTabsRepository.getTabs(new TabsDataSource.LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                List<Tab> tabsToShow = new ArrayList<Tab>();

                // 활성 상태인 tab 만 메인액티비티 뷰페이저 아이템 리스트에 추가한다.
                for(Tab tab : tabs){
                    //mTabsRepository.useTab(tab);
                    if(tab.isUsed()){
                        tabsToShow.add(tab);
                    }
                }

                tabItems.clear();
                tabItems.addAll(tabsToShow);
                notifyPropertyChanged(BR.empty);
            }
            @Override
            public void onDataNotAvailable() {
                // 맨 처음 앱 설치 시 Tab 로컬 데이터가 없을 때
                firstTabItem();
                loadTabs(false);
            }
        });

    }

    // 맨 처음 앱 설치시 Tab 데이터베이스가 null 일때 Tab 정보 추가
    public void firstTabItem(){
        addTabs("SUBSCRIBE","SUB_FRAG");
        addTabs("BEST","DEF_FRAG");
        addTabs("GAME","DEF_FRAG");
        addTabs("MUSIC","DEF_FRAG");
        addTabs("MOVIE","DEF_FRAG");
        addTabs("SPORTS","DEF_FRAG");
        addTabs("NEWS","DEF_FRAG");
        addTabs("DOCUMENTARY","DEF_FRAG");
    }

    public void addTabs(String title, String viewType){
        Tab newTab = new Tab(title,viewType);
        mTabsRepository.saveTab(newTab);
    }

}
