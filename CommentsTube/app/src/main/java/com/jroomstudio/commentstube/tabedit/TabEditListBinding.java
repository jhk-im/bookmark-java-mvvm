package com.jroomstudio.commentstube.tabedit;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

public class TabEditListBinding {

    /**
     * TabEditFrag 의 리사이클러뷰에 활성화된 탭 아이템 리스트를 관찰함
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:setUseItems")
    public static void setUseItems(RecyclerView recyclerView, List<Tab> useTabs){
        TabEditFragment.TabListAdapter tabListAdapter =
                (TabEditFragment.TabListAdapter) recyclerView.getAdapter();
        if(tabListAdapter != null){
            if(!tabListAdapter.isMove()){
                tabListAdapter.replaceUseData(useTabs);
            }
        }
    }

    /**
     * TabEditFrag 의 리사이클러뷰에 비활성화 된 탭 아이템 리스트를 관찰함
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:setDisableItems")
    public static void setDisableItems(RecyclerView recyclerView, List<Tab> disableTabs){
        TabEditFragment.TabListAdapter tabListAdapter =
                (TabEditFragment.TabListAdapter) recyclerView.getAdapter();
        if(tabListAdapter != null){
            tabListAdapter.replaceDisableTabs(disableTabs);
        }
    }


}
