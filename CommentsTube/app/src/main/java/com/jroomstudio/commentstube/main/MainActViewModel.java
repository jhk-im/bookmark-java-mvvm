package com.jroomstudio.commentstube.main;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.data.source.TabsRepository;
import com.jroomstudio.commentstube.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

public class MainActViewModel extends BaseObservable {

    //These observable fields will update Views automatically
    public final ObservableList<Tab> tabs = new ObservableArrayList<>();

    private final TabsRepository mTabsRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    //private final TabsRepository mTabsRepository;
    private Context mContext;

    public MainActViewModel (TabsRepository tabsrepository,
                             Context context){
        mTabsRepository = tabsrepository;
        mContext = context.getApplicationContext(); // Force use of Application Context
    }

    public void start(){ loadTabs(false); }

    public void loadTabs(boolean forceUpdate) { loadTabs(forceUpdate, true); }

    private void loadTabs(boolean forceUpdate, final boolean showLoadingUI){

        if(showLoadingUI) {
            // 로딩 ui 셋팅
        }
        if(forceUpdate){
            mTabsRepository.refreshTabs();
        }

        // 네트워크 요청이 다른 스레드에서 처리 될 수 있으므로 Espresso 가 알고있는지 확인
        // 응답 처리 될 때 까지 앱이 사용중임을 나타냄
        // EspressoIdlingResource

        mTabsRepository.getTabs(new TabsDataSource.LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                List<Tab> tabsToShow = new ArrayList<Tab>();

                // 구분
                for(Tab tab : tabs){

                }


            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });

    }

}
