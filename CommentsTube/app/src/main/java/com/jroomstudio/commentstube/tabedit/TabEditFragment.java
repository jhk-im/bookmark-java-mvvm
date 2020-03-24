package com.jroomstudio.commentstube.tabedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jroomstudio.commentstube.databinding.TabeditFragBinding;
import com.jroomstudio.commentstube.databinding.TabeditItemBinding;
import com.jroomstudio.commentstube.tabedit.itemtouch.ItemTouchHelperCallback;
import com.jroomstudio.commentstube.tabedit.itemtouch.ItemTouchHelperListener;

import java.util.ArrayList;


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

        setupListAdapter(container);

        View root = mTabEditFragBinding.getRoot();
        return root;
    }

    public void setTabEditViewModel(TabEditViewModel viewModel) { mTabEditViewModel = viewModel; }

    // 임시
    public void setupListAdapter(ViewGroup container){
        //adapter 임시 데이터 추가
        ArrayList<TabItem> items = new ArrayList<>();
        TabItem item1 = new TabItem("SUBSCRIBE",1);
        TabItem item2 = new TabItem("BEST",2);
        TabItem item3 = new TabItem("MUSIC",3);
        TabItem item4 = new TabItem("SPORTS",4);
        TabItem item5 = new TabItem("GAME",5);
        TabItem item6 = new TabItem("MOVIE",6);
        TabItem item7 = new TabItem("NEWS",7);
        TabItem item8 = new TabItem("LIVE",8);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        //어댑터 셋팅
        mAdapter = new TabListAdapter();
        mTabEditFragBinding.rvTabList.setAdapter(mAdapter);

        //Item Touch Helper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter));

        //리사이클러뷰 아이템 터치 헬퍼 연결
        helper.attachToRecyclerView(mTabEditFragBinding.rvTabList);

        // Adapter 데이터 추가
        mAdapter.setList(items);

    }

    /**
     * Tab Item 리사이클러뷰
    * */
    public static class TabListAdapter extends RecyclerView.Adapter<TabListAdapter.ItemViewHolder>
            implements ItemTouchHelperListener {

        // tab item 리스트
        private ArrayList<TabItem> items = new ArrayList<>();

        //데이터 바인딩
        private TabeditItemBinding mTabItemBinding;

        //뷰모델
        private TabItemViewModel mTabItemViewModel;

        public TabListAdapter(){}

        @NonNull
        @Override
        public TabListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            mTabItemBinding = TabeditItemBinding.inflate(inflater, parent, false);
            mTabItemViewModel = new TabItemViewModel(parent.getContext().getApplicationContext());
            mTabItemBinding.setViewmodel(mTabItemViewModel);
            View view = mTabItemBinding.getRoot();
            return new TabListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TabListAdapter.ItemViewHolder holder, int position) {
            //item view holder 가 생성되고 넣어야 할 코드들을 넣어준다
            holder.onBind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public boolean onItemMove(int form_position, int to_position) {
            //이동할 객체저장
            TabItem item = items.get(form_position);
            //이동할 객체 삭제
            items.remove(form_position);
            //이동하고 싶은 position 추가
            items.add(to_position,item);
            //Adapter 에 데이터 이동알림
            notifyItemMoved(form_position, to_position);
            return true;
        }

        @Override
        public void onItemSwipe(int position) {
            items.remove(position);
            notifyItemRemoved(position);
        }

        private void setList(ArrayList<TabItem> itemList) {
            items = itemList;
            notifyDataSetChanged();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            //TextView tabName;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void onBind(TabItem item){
                mTabItemViewModel.setTabItem(item);
                mTabItemBinding.name.setText(item.getTabName());
                //tabName.setText(item.getTabName());
            }
        }
    }
}
