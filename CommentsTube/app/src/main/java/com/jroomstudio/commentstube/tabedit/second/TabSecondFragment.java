package com.jroomstudio.commentstube.tabedit.second;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class TabSecondFragment extends Fragment {

    // 뷰모델
    private TabSecondViewModel mTabSecondViewModel;

    // 데이터 바인딩

    // 어댑터


    public TabSecondFragment() {}

    public static TabSecondFragment newInstance() { return new TabSecondFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static class TabSecondAdapter extends RecyclerView.Adapter<TabSecondAdapter.ItemViewHolder> {


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

}
