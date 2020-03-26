package com.jroomstudio.commentstube.main;


import android.util.Log;
import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

public class TabsListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:tabs")
    public static void setTabs(ViewPager viewPager, List<Tab> tabs){
        MainActivity.MainPagerAdapter adapter =
                (MainActivity.MainPagerAdapter) viewPager.getAdapter();
        tabs = adapter.getTabs();
        Log.e("tabs",String.valueOf(tabs.size()));
    }

}
