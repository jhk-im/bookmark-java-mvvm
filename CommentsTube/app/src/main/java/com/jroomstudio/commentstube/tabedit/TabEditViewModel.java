package com.jroomstudio.commentstube.tabedit;

import android.content.Context;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.databinding.library.baseAdapters.BR;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.data.source.TabsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabEditViewModel extends BaseObservable {

    // 활성화 탭 리스트 옵저버블 변수
    public final ObservableList<Tab> useItems = new ObservableArrayList<>();

    // 비활성화 탭 리스트 옵저버블 변수
    public final ObservableList<Tab> disableItems = new ObservableArrayList<>();

    private final Context mContext;

    private TabsRepository mTabsRepository;

    public TabEditViewModel(TabsRepository repository ,Context context) {
        mTabsRepository = repository;
        mContext = context.getApplicationContext();
    }


    // Edit 완료한 Tab 저장
    // 액티비티 fab 버튼 클릭시 호출
    public void saveTabs(){
        // 활성상태 탭 데이터베이스 업데이트
        for(Tab tab : useItems){
            // 포지션값 변경
            mTabsRepository.updatePosition(tab,useItems.indexOf(tab));
            // 활성상태로 변경
            mTabsRepository.useTab(tab);
        }
        // 비활성상태 탭 데이터베이스 업데이트
        for(Tab tab : disableItems){
            // 포지션값 변경
            mTabsRepository.updatePosition(tab,disableItems.indexOf(tab));
            // 비활성상태로 변경
            mTabsRepository.disableTab(tab);
        }
    }

    // 담당 프래그먼트가 onResume 될때 호출
    public void start(){
        loadTabs();
    }

    // 로컬, 원격 데이터베이스에서 탭 정보 로드
    private void loadTabs(){

        mTabsRepository.getTabs(new TabsDataSource.LoadTabsCallback() {
            @Override
            public void onTabsLoaded(List<Tab> tabs) {
                List<Tab> useTabsToShow = new ArrayList<Tab>();
                List<Tab> disableTabsToShow = new ArrayList<Tab>();
                // 사용 활성상태인 탭만 리스트에 추가
                for(Tab tab : tabs){
                    Log.e("onLoad",tab.getId()+"|"+tab.getName()+"|"+tab.getPosition()+"|"+tab.isUsed());
                    if(tab.isUsed()){
                        useTabsToShow.add(tab);
                    }else{
                        disableTabsToShow.add(tab);
                    }
                }
                // 활성화 탭 리스트
                useItems.clear();
                useItems.addAll(sortToTabList(useTabsToShow));
                // 비활성화 탭 리스트
                disableItems.clear();
                disableItems.addAll(sortToTabList(disableTabsToShow));
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
