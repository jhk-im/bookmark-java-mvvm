package com.jroomstudio.commentstube;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsDataSource;
import com.jroomstudio.commentstube.data.source.TabsRepository;

public abstract class TabsViewModel extends BaseObservable
        implements TabsDataSource.GetTabCallback {

    public final ObservableField<Tab> mTabObservable = new ObservableField<>();

    private final Context mContext;

    private final TabsRepository mTabsRepository;

    public TabsViewModel(Context context, TabsRepository repository) {
        mContext = context.getApplicationContext();
        mTabsRepository = repository;

    }

    // This could be an observable, but we save a call to Task.getTitleForList() if not needed.
    @Bindable
    public String getTabNameForList() {
        return mTabObservable.get().getNameForList();
    }

    public void setTab(Tab tab){
        if(mTabObservable.get() != null){
            start(mTabObservable.get().getId());
        }else{
            mTabObservable.set(tab);
        }

    }


    @Override
    public void onTabLoaded(Tab tab) {
        mTabObservable.set(tab);
        notifyChange();
    }

    @Override
    public void onDataNotAvailable() {
        mTabObservable.set(null);
    }


    public void start(String tabId) {
        if (tabId != null) {
            mTabsRepository.getTab(tabId, this);
        }
    }

    public void onRefresh(){
        if(mTabObservable.get() != null){
            start(mTabObservable.get().getId());
        }
    }
}
