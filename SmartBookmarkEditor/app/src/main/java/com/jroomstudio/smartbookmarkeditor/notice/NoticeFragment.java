package com.jroomstudio.smartbookmarkeditor.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jroomstudio.smartbookmarkeditor.databinding.NoticeFragBinding;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

public class NoticeFragment extends Fragment {

    // 뷰모델
    private NoticeViewModel mViewModel;

    // 데이터 바인딩
    private NoticeFragBinding mDataBinding;

    // 인스턴스 생성
    static NoticeFragment newInstance() { return new NoticeFragment(); }

    // 비어있는 생성자
    public NoticeFragment(){}

    // 뷰모델 셋팅
    void setViewModel(NoticeViewModel viewModel) { mViewModel = checkNotNull(viewModel); }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start();
    }

    // 뷰
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 데이터 바인딩 셋팅
        mDataBinding = NoticeFragBinding.inflate(inflater,container,false);
        // 프래그먼트 set
        mDataBinding.setView(this);
        // 뷰모델 지정
        mDataBinding.setViewmodel(mViewModel);

        // 리사이클러뷰 셋팅
        setupRecyclerAdapter();

        return mDataBinding.getRoot();
    }

    // 액티비티 생성될 때
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    // 리사이클러뷰 어댑터 셋팅
    private void setupRecyclerAdapter(){

        // 알림 리사이클러뷰 어댑터 생성
        NoticeRecyclerAdapter recyclerAdapter = new NoticeRecyclerAdapter(
                new ArrayList<>(0),
                mViewModel
        );

        // 깜빡임 방지
        recyclerAdapter.setHasStableIds(true);

        // 리사이클러뷰 레이아웃 매니져
        mDataBinding.rvNotifications.setLayoutManager(
                new LinearLayoutManager(getContext().getApplicationContext())
        );
        // 리사이클러뷰 어댑터 셋팅
        mDataBinding.rvNotifications.setAdapter(recyclerAdapter);

    }
}
