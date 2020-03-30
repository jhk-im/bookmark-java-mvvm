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
import java.util.Collections;
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
     * 해당 뷰모델과 연결된 액티비티의 UI 를 관찰하고 컨트롤한다.
     *
     * main_act.xml 의 데이터 바인딩 뷰모델로 지정이 되어있기 때문에
     * Observable 로 선언한 변수를 main_act.xml 에서 연결 할 수 있다.
    **/
    // 뷰페이저에 추가되는 tab 아이템 리스트
    public final ObservableList<Tab> items = new ObservableArrayList<>();



    /**
     * 해당 뷰모델과 연결될 액티비티,프래그먼트 의 Context
     *
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

    /**
     * 관찰하고 있는 tabItem 리스트의 isEmpty 결과를 반환한다.
     **/
    @Bindable
    public  boolean isEmpty() { return items.isEmpty(); }

    /**
     * MainActivity 의 onResume 에서 호출
     *
     * loadTabs(boolean forceUpdate) false 입력하여
     * 로컬, 원격 데이터베이스로부터 Tabs 리스트를 받아온다.
     **/
    public void start(){
        loadTabs(false);
    }

    public void loadTabs(boolean forceUpdate){ loadTabs(forceUpdate, true); }

    /**
     * MainActivity 뷰페이저에 등록될 Tab(프래그먼트) 의 데이터를 갱신한다.
     * 로컬과 원격 데이터베이스로 부터 Tab 객체 리스트를 받아온다.
     *
     * 받아온 Tab 리스트는 Observable 로 선언된 tab 리스트인 tabItems 에 셋팅한다.
     * notifyPropertyChanged 로 알려주면 각각의 Tab 정보에 맞게 ui가 셋팅된다.
     **/
    private void loadTabs(boolean forceUpdate, final boolean showLoadingUI){

        /**
         * {@link TabsRepository} - 로컬, 원격 데이터베이스 액세스 구현
         * 현재는 로컬에서 정보를 받아오는 것만 구현되어 있다.
         * 로컬과 원격 모두 TabsDataSource 를 상속받기 때문에 같은 메소드가 오버라이딩 되어있고
         * 각자의 상황에 맞게 데이터를 받아오도록 구현하면된다.
         *
         * ex)
         * LoadTabsCallback 과 onTabsLoaded , onDataNotAvailable 을 활용하여
         * 1. 로컬 데이터 액세스 - 실패
         * 2. 원격 데이터 액세스 - 성공
         * 3. 로컬 데이터 액세스 - 원격 데이터로 갱신
         * ...
         * 등으로 다양한 순서로 로직을 구현할 수 있다.
         **/
        mTabsRepository.getTabs(new TabsDataSource.LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                List<Tab> tabsToShow = new ArrayList<Tab>();

                // 활성 상태인  tab 을 메인액티비티 뷰페이저 아이템 리스트에 추가한다.
                for(Tab tab : tabs){
                    if(tab.isUsed()){
                        tabsToShow.add(tab);
                    }
                }
                // 옵저버블 변수에 추가
                items.clear();
                items.addAll(sortToTabList(tabsToShow));
                notifyPropertyChanged(BR.empty);
            }
            @Override
            public void onDataNotAvailable() {
                /**
                 * Tab 객체 정보를 받아오는데 실패했을 때
                 **/
            }
        });

    }

    /**
     * tab 리스트를 position 값에 맞게 정렬
     **/
    public List<Tab> sortToTabList(List<Tab> tabs){
        Collections.sort(tabs, (o1, o2) -> {
            if(o1.getPosition() < o2.getPosition()){
                return -1;
            } else if (o1.getPosition() > o2.getPosition()){
                return 1;
            }
            return 0;
        });
        return tabs;
    }

}
