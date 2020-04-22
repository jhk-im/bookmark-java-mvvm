package com.jroomstudio.smartbookmarkeditor.itemtouch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jroomstudio.smartbookmarkeditor.Injection;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.databinding.ItemTouchEditFragBinding;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.BookmarkItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.CategoriesItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.util.ItemTouchHelperCallback;

import java.util.ArrayList;

public class ItemTouchEditFragment extends Fragment {

    // 뷰모델
    private ItemTouchEditViewModel mViewModel;

    // 프래그먼트 xml 데이터 바인딩
    private ItemTouchEditFragBinding mDataBinding;

    // 다이렉트 인스턴스 생성 방지
    public ItemTouchEditFragment() {}

    // 프래그먼트 인스턴스 생성
    public static ItemTouchEditFragment newInstance() { return  new ItemTouchEditFragment(); }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start();
    }

    // 데이터바인딩 -> 뷰 표시
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("fragment on create view","ItemTouchEditFragment");
        // 프래그먼트 뷰 생성
        mDataBinding = ItemTouchEditFragBinding.inflate(inflater,container,false);
        // 현재 프래그먼트를 xml view 로 지정
        mDataBinding.setView(this);
        // 현재 프래그먼트의 뷰모델 지정
        mDataBinding.setViewmodel(mViewModel);
        // 프래그먼트의 옵션메뉴 활성화
        setHasOptionsMenu(true);
        // 리사이클러뷰 셋팅
        setupRecyclerAdapter();

        //최종 뷰 생성
        return mDataBinding.getRoot();
    }

    // 액티비티가 생성될 때
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

    }

    // 메인액티비티에서 호출
    // 프래그먼트 뷰와 뷰모델을 매치
    void setViewModel(ItemTouchEditViewModel viewModel) { mViewModel = viewModel; }

    // Floating Action Button 셋팅
    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_setting_done);

        fab.setOnClickListener(v -> {
            mViewModel.onItemsSaved();
        });

    }

    // 북마크, 카테고리 리사이클러뷰 어댑터
    private void setupRecyclerAdapter(){

        // 북마크 어댑터 생성
        BookmarkItemTouchRecyclerAdapter bookmarkAdapter = new BookmarkItemTouchRecyclerAdapter(
                new ArrayList<>(0),
                Injection.provideBookmarksRepository(getContext().getApplicationContext()),
                mViewModel
        );
        // 깜빡임 방지
        bookmarkAdapter.setHasStableIds(true);
        // 리사이클러뷰 레이아웃 매니져
        mDataBinding.rvBookmarks.setLayoutManager(
                new LinearLayoutManager(getContext().getApplicationContext())
        );
        // 리사이클러뷰 어댑터 셋팅
        mDataBinding.rvBookmarks.setAdapter(bookmarkAdapter);
        // 북마크 리사이클러뷰 터치헬퍼 연결
        ItemTouchHelper bookmarkHelper =
                new ItemTouchHelper(new ItemTouchHelperCallback(bookmarkAdapter));
        bookmarkHelper.attachToRecyclerView(mDataBinding.rvBookmarks);


        // 카테고리 어댑터 생성
        CategoriesItemTouchRecyclerAdapter categoryAdapter = new CategoriesItemTouchRecyclerAdapter(
                new ArrayList<>(0),
                Injection.provideCategoriesRepository(getContext().getApplicationContext()),
                mViewModel
        );
        // 깜빡임 방지
        categoryAdapter.setHasStableIds(true);
        // 리사이클러뷰 레이아웃 매니져
        mDataBinding.rvCategories.setLayoutManager(
                new LinearLayoutManager(getContext().getApplicationContext())
        );
        // 리사이클러뷰 어댑터 셋팅
        mDataBinding.rvCategories.setAdapter(categoryAdapter);
        // 북마크 리사이클러뷰 터치헬퍼 연결
        ItemTouchHelper categoryHelper =
                new ItemTouchHelper(new ItemTouchHelperCallback(categoryAdapter));
        categoryHelper.attachToRecyclerView(mDataBinding.rvCategories);

    }


}
