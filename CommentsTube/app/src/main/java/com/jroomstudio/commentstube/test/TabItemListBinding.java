package com.jroomstudio.commentstube.test;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TabItemListBinding {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(RecyclerView rv, ArrayList<TabItem> items){
        TabListAdapter adapter = (TabListAdapter) rv.getAdapter();
        if(adapter != null){
            adapter.replaceData(items);
        }
    }
}
