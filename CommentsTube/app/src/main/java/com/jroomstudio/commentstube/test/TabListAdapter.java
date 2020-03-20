package com.jroomstudio.commentstube.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.commentstube.R;

import java.io.PipedOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TabListAdapter extends RecyclerView.Adapter<TabListAdapter.ItemViewHolder>
                                implements ItemTouchHelperListener{

    ArrayList<TabItem> items = new ArrayList<>();

    public TabListAdapter(){}

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 띄우게 될 레이아웃 지정
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tabedit_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //item view holder 가 생성되고 넣어야 할 코드들을 넣어준다
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(TabItem item){
        items.add(item);
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

    public void replaceData(ArrayList<TabItem> item) {
        setList(items);
    }
    private void setList(ArrayList<TabItem> item) {
        items = item;
        notifyDataSetChanged();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tabName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tabName = itemView.findViewById(R.id.name);
        }

        public void onBind(TabItem item){
            tabName.setText(item.getTabName());
        }
    }
}
