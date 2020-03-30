package com.jroomstudio.commentstube.tabedit.itemtouch;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperListener listener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener){
        this.listener = listener;
    }

    // flags
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        int drag_flags;
        int swipe_flags;
        // 첫번째 구독 tab 은 고정
        if(viewHolder.getAdapterPosition() > 0){
            drag_flags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
            swipe_flags = ItemTouchHelper.START|ItemTouchHelper.END;
        }else{
            drag_flags = 0;
            swipe_flags = 0;
        }

        return makeMovementFlags(drag_flags,swipe_flags);
    }

    //롱클릭
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // 무브
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        boolean move;
        // 첫번째 구독 tab 은 고정
        if(viewHolder.getAdapterPosition() > 0 && target.getAdapterPosition() > 0){
            move = listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        }else{
            move = false;
        }

        return move;
    }

    //스와이프
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            listener.onItemSwipe(viewHolder.getAdapterPosition());
    }
}
