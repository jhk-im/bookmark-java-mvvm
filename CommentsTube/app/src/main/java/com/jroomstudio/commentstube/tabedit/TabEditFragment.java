package com.jroomstudio.commentstube.tabedit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jroomstudio.commentstube.databinding.TabeditFragBinding;
import com.jroomstudio.commentstube.test.ItemTouchHelperCallback;
import com.jroomstudio.commentstube.test.TabItem;
import com.jroomstudio.commentstube.test.TabListAdapter;



public class TabEditFragment extends Fragment {

    // 뷰모델
    private TabEditViewModel mTabEditViewModel;

    //데이터바인딩
    private TabeditFragBinding mTabEditFragBinding;

    // 리사이클러뷰 - tab 리스트
    private RecyclerView mRecyclerView;
    private TabListAdapter mAdapter;
    private ItemTouchHelper helper;


    public TabEditFragment() {}

    public static TabEditFragment newInstance() { return  new TabEditFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mTabEditFragBinding = TabeditFragBinding.inflate(inflater,container,false);
        mTabEditFragBinding.setView(this);
        mTabEditFragBinding.setViewmodel(mTabEditViewModel);
        setHasOptionsMenu(true);
        //mTabEditFragBinding.tvTest.setText("Tab Edit Fragment");

        // 리사이클러뷰 tab 리스트
        // 리사이클러뷰 레이아웃 방식 지정
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mTabEditFragBinding.rvTabList.setLayoutManager(manager);

        //어댑터 셋팅
        mAdapter = new TabListAdapter();
        mTabEditFragBinding.rvTabList.setAdapter(mAdapter);

        //Item Touch Helper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter));

        //리사이클러뷰 아이템 터치 헬퍼 연결
        helper.attachToRecyclerView(mTabEditFragBinding.rvTabList);

        //adapter 임시 데이터 추가
        TabItem item1 = new TabItem("SUBSCRIBE",1);
        TabItem item2 = new TabItem("BEST",2);
        TabItem item3 = new TabItem("MUSIC",3);
        TabItem item4 = new TabItem("SPORTS",4);
        TabItem item5 = new TabItem("GAME",5);
        TabItem item6 = new TabItem("MOVIE",6);
        TabItem item7 = new TabItem("NEWS",7);
        TabItem item8 = new TabItem("LIVE",8);
        mAdapter.addItem(item1);
        mAdapter.addItem(item2);
        mAdapter.addItem(item3);
        mAdapter.addItem(item4);
        mAdapter.addItem(item5);
        mAdapter.addItem(item6);
        mAdapter.addItem(item7);
        mAdapter.addItem(item8);

        View root = mTabEditFragBinding.getRoot();
        return root;
    }

    public void setTabEditViewModel(TabEditViewModel viewModel) { mTabEditViewModel = viewModel; }



}
