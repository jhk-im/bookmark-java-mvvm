package com.jroomstudio.smartbookmarkeditor.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;

import java.util.List;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeRecyclerAdapter.ItemViewHolder> {

    // 알림 리스트 멤버변수
    private List<Notice> mNotifications;

    // Notice ViewModel
    private NoticeViewModel mNoticeViewModel;

    //생성자
    NoticeRecyclerAdapter(List<Notice> notifications,
                          NoticeViewModel noticeViewModel){
        setNotifications(notifications);
        mNoticeViewModel = noticeViewModel;
    }

    // 뷰
    @NonNull
    @Override
    public NoticeRecyclerAdapter.ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.notice_item, parent, false) ;

        return new NoticeRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeRecyclerAdapter.ItemViewHolder holder, int position) {
        holder.onBind(mNotifications.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    private void setNotifications(List<Notice> notifications){
        mNotifications = notifications;
        notifyDataSetChanged();
    }

    // 변화감지후 리스트 갱신
    public void replaceNotifications(List<Notice> notifications){
        setNotifications(notifications);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title,body,date;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_notice_title);
            body = itemView.findViewById(R.id.tv_notice_body);
            date = itemView.findViewById(R.id.tv_notice_date);
        }
        void onBind(Notice notice){
            // 알림 제목
            title.setText(notice.getTitle());
            // 알림 본문
            body.setText(notice.getDescription());
            // 알림 등록 날짜
            date.setText(notice.getDate());
            // 읽은 알림인지 구분
            itemView.setSelected(notice.isRead());
            // 아이템 클릭이벤트
            itemView.setOnClickListener(v -> {
                mNoticeViewModel.moveToDetailClickItem(notice);
            });
            // 아이템 롱클릭 이벤트
            itemView.setOnLongClickListener(v -> {
                mNoticeViewModel.deleteLongClickItem(notice);
                return false;
            });
        }
    }

}
