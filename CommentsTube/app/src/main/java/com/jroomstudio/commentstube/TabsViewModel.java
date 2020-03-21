package com.jroomstudio.commentstube;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.jroomstudio.commentstube.tabedit.TabEditNavigator;
import com.jroomstudio.commentstube.tabedit.TabItem;

public abstract class TabsViewModel extends BaseObservable {

    public final ObservableField<TabItem> mTabItemObservable = new ObservableField<>();

    private final Context mContext;

    private TabEditNavigator mNavigator;

    public TabsViewModel(Context context) {
        mContext = context.getApplicationContext();
    }

    void onActivityDestroyed(){
        mNavigator = null;
    }

    // This could be an observable, but we save a call to Task.getTitleForList() if not needed.
    @Bindable
    public String getTabNameForList() {
        return mTabItemObservable.get().getNameForList();
    }

    public void setTabItem(TabItem item){
        mTabItemObservable.set(item);
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void tabEditComplete(){
        if(mNavigator != null){
            mNavigator.tabEditComplete();
        }
        // 플로팅 버튼 구현
    }

}
