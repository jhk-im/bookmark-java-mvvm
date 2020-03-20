package com.jroomstudio.commentstube.tabedit;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.jroomstudio.commentstube.test.TabItem;

public class TabEditViewModel extends BaseObservable {

    public final ObservableField<String> tvTest = new ObservableField<>();

    public final ObservableArrayList<TabItem> items = new ObservableArrayList<>();

    private Context mContext;

    private TabEditNavigator mNavigator;

    public TabEditViewModel(Context context) { mContext = context.getApplicationContext(); }

    void onActivityDestroyed(){
        mNavigator = null;
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
