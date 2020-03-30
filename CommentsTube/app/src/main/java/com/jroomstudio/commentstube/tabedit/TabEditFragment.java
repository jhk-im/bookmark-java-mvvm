package com.jroomstudio.commentstube.tabedit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jroomstudio.commentstube.Injection;
import com.jroomstudio.commentstube.R;
import com.jroomstudio.commentstube.data.Tab;
import com.jroomstudio.commentstube.data.source.TabsRepository;
import com.jroomstudio.commentstube.databinding.TabeditFragBinding;
import com.jroomstudio.commentstube.databinding.TabeditItemBinding;
import com.jroomstudio.commentstube.tabedit.itemtouch.ItemTouchHelperCallback;
import com.jroomstudio.commentstube.tabedit.itemtouch.ItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;


public class TabEditFragment extends Fragment {

    // 뷰모델
    private TabEditViewModel mTabEditViewModel;

    //데이터바인딩
    private TabeditFragBinding mTabEditFragBinding;

    // 리사이클러뷰 - tab 리스트
    private TabListAdapter mAdapter;
    private ItemTouchHelper helper;


    public TabEditFragment() {}

    public static TabEditFragment newInstance() { return  new TabEditFragment(); }

    @Override
    public void onResume() {
        super.onResume();
        mTabEditViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mTabEditFragBinding = TabeditFragBinding.inflate(inflater,container,false);
        mTabEditFragBinding.setView(this);
        mTabEditFragBinding.setViewmodel(mTabEditViewModel);
        setHasOptionsMenu(true);

        // 리사이클러뷰 tab 리스트
        // 리사이클러뷰 레이아웃 방식 지정
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mTabEditFragBinding.rvTabList.setLayoutManager(manager);
        // 리사이클러뷰 어댑터 셋팅
        setupListAdapter(container);


        View root = mTabEditFragBinding.getRoot();
        return root;
    }



    // 뷰모델 연결
    public void setTabEditViewModel(TabEditViewModel viewModel) { mTabEditViewModel = viewModel; }

    /**
     * 리사이클러뷰 어댑터 셋팅
     **/
    public void setupListAdapter(ViewGroup container){

        //어댑터 셋팅
        mAdapter = new TabListAdapter(
                new ArrayList<Tab>(0),
                Injection.provideTabsRepository(getContext().getApplicationContext()),
                mTabEditViewModel,
                mTabEditFragBinding
        );
        mTabEditFragBinding.rvTabList.setAdapter(mAdapter);

        //Item Touch Helper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter));

        //리사이클러뷰 아이템 터치 헬퍼 연결
        helper.attachToRecyclerView(mTabEditFragBinding.rvTabList);

    }


    /**
     * Tab Item 리사이클러뷰 어댑터
     * */
    public static class TabListAdapter extends RecyclerView.Adapter<TabListAdapter.ItemViewHolder>
            implements ItemTouchHelperListener {

        // tab item 리스트
        //private ArrayList<TabItem> items = new ArrayList<>();

        /**
         * TabListAdapter 생성성할때 입력받는 값의 멤버변수
         **/
        private List<Tab> mUseTabs;
        private List<Tab> mDisableTabs = new ArrayList<>();

        private TabsRepository mTabsReository;

        //데이터 바인딩
        private TabeditItemBinding mTabItemBinding;
        private TabeditFragBinding mTabEditFragBinding;

        //뷰모델
        private TabEditViewModel mTabEditViewModel;

        //private TabItemViewModel mTabItemViewModel;

        // tab move 상태 반환
        private boolean isTabMove = false;

        public TabListAdapter(List<Tab> tabs, TabsRepository tabsRepository,
                              TabEditViewModel tabEditViewModel,
                              TabeditFragBinding tabEditFragBinding) {
            mTabsReository = tabsRepository;
            mTabEditViewModel = tabEditViewModel;
            mTabEditFragBinding = tabEditFragBinding;
            setUseTabs(tabs);
        }

        @NonNull
        @Override
        public TabListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            mTabItemBinding = TabeditItemBinding.inflate(inflater, parent, false);
            //mTabItemViewModel = new TabItemViewModel(parent.getContext().getApplicationContext(),mTabsReository);
            //mTabItemBinding.setViewmodel(mTabItemViewModel);
            View view = mTabItemBinding.getRoot();
            return new TabListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TabListAdapter.ItemViewHolder holder, int position) {
            //item view holder 가 생성되고 넣어야 할 코드들을 넣어준다
            //Log.e("onBindHolder",mTabs.toString());
            holder.onBind(mUseTabs.get(position));

        }

        @Override
        public int getItemCount() {
            return mUseTabs != null ? mUseTabs.size() : 0;
        }

        @Override
        public boolean onItemMove(int form_position, int to_position) {
                // 움직이고 있으니 viewModel 이 갱신하지 못하도록
                isTabMove = true;
                //이동할 객체저장
                Tab item = mUseTabs.get(form_position);
                //이동할 객체 삭제
                mUseTabs.remove(form_position);
                //이동하고 싶은 position 추가
                mUseTabs.add(to_position,item);
                //Adapter 에 데이터 이동알림
                notifyItemMoved(form_position, to_position);
                return true;
        }


        // 아이템 삭제
        @Override
        public void onItemSwipe(int position) {
            // 스와이프 할 때에는 움직인다고 판단하지 않음
            isTabMove = false;
            // 구독 tab 은 고정
            if(position > 0){
                mDisableTabs.add(mUseTabs.get(position));
                mUseTabs.remove(position);
                notifyItemRemoved(position);
            }
        }

        // 멤버리스트 갱신
        private void setUseTabs(List<Tab> useTabs) {
            mUseTabs = useTabs;
            notifyDataSetChanged();
        }

        // 변화감지 후 리스트 갱신
        public void replaceUseData(List<Tab> useTabs){
            setUseTabs(useTabs);
        }

        public void replaceDisableTabs(List<Tab> disableTabs){
            mDisableTabs = disableTabs;
            // 비활성화 탭 리스트에 아이템이 있을 경우에만 실행
            if(mDisableTabs.size() > 0){
                // chip 초기화
                mTabEditFragBinding.chipGroup.removeAllViews();
                // chip 생성
                for(Tab tab : mDisableTabs){
                    createChip(tab, mDisableTabs.indexOf(tab));
                }
            }
            notifyDataSetChanged();
        }

        // chip 생성
        public void createChip(Tab tab,int position){
            // chip 인스턴스 생성
            Chip chip = new Chip(mTabEditFragBinding.getRoot().getContext());
            // chip text 지정
            chip.setText(tab.getName());
            // 비활성화 탭 리스트에서 자신의 순번을 저장해둠
            chip.setId(position);
            mTabEditFragBinding.chipGroup.addView(chip);

            // 각각의 chip 클릭 리스너
            chip.setOnClickListener(v -> {
                // 활성화 탭 리스트에 현재 chip 의 tab 정보 추가
                mUseTabs.add(mDisableTabs.get(chip.getId()));
                // 비활성화 탭 리스트에서 현재 chip 의 tab 정보 삭제
                mDisableTabs.remove(mDisableTabs.get(chip.getId()));
                // 만약 비활성화 탭 리스트가 비어있으면 chip 모두 삭제
                if(mDisableTabs.size() == 0){
                    mTabEditFragBinding.chipGroup.removeAllViews();
                }
            });
        }

        // 움직이고 있는지 아닌지 반환
        public boolean isMove() {
            return isTabMove;
        }


        // 각 아이템 홀더로 관리
        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
            }

            public void onBind(Tab tab){
                name.setText(tab.getName());
            }
        }

    }

}
