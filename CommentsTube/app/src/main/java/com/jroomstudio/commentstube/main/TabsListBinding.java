package com.jroomstudio.commentstube.main;


import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

/**
 * {@link Tab} 목록에 대한 {@link BindingAdapter}를 포함한다.
 **/
public class TabsListBinding {

    /**
     * MainActivity 에 셋팅되어있는 ViewPager 에 셋팅되는 Tab Item 의 리스트들을 관찰한다.
     * 변화가 감지되면 replaceData 로 어댑터 내부의 Tab 아이템의 변화를 업데이트한다.
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ViewPager viewPager, List<Tab> tabItems){
        MainActivity.MainPagerAdapter mainPagerAdapter =
                (MainActivity.MainPagerAdapter) viewPager.getAdapter();
        if(mainPagerAdapter != null){
            mainPagerAdapter.replaceData(tabItems);
        }
    }

}
