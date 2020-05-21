package com.jroomstudio.smartbookmarkeditor.itemtouch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.itemtouch.ItemTouchEditViewModel;
import com.jroomstudio.smartbookmarkeditor.util.ItemTouchHelperListener;

import java.util.List;

public class BookmarkItemTouchRecyclerAdapter
        extends RecyclerView.Adapter<BookmarkItemTouchRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperListener {

    // 북마크 리스트 멤버변수
    private List<Bookmark> mBookmarks;

    // 북마크 원격과 로컬 데이터소스 액세스
    private BookmarksLocalRepository mBookmarksLocalRepository;

    // 메인프래그먼트 뷰모델
    private ItemTouchEditViewModel mViewModel;

    // item move 상태 구분
    private boolean isItemMove = false;

    /**
     * 어댑터 생성자
     **/
    public BookmarkItemTouchRecyclerAdapter(List<Bookmark> bookmarks,
                                            BookmarksLocalRepository bookmarksLocalRepository,
                                            ItemTouchEditViewModel viewModel){
        setBookmarks(bookmarks);
        mBookmarksLocalRepository = bookmarksLocalRepository;
        mViewModel = viewModel;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_touch_bookmark_item, parent, false);
        return new ItemViewHolder(view);
    }

    // 포지션 입력하여 아이템 구분하여 값 지정 (text, img 등)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(mBookmarks.get(position));
    }

    // 리스트 사이즈만큼 반복실행
    @Override
    public int getItemCount() {
        return mBookmarks != null ? mBookmarks.size() : 0;
    }

    // 북마크 멤버리스트 갱신
    private void setBookmarks(List<Bookmark> bookmarks) {
        mBookmarks = bookmarks;
        notifyDataSetChanged();
    }

    // 변화감지후 리스트 갱신
    public void replaceBookmarks(List<Bookmark> bookmarks){
        setBookmarks(bookmarks);
    }

    // 아이템 무브
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        // 움직이고 있을 때에는 view model 에서 갱신하지 못하도록함
        isItemMove = true;
        // 이동할 객체 포지션으로 가져와 생성
        Bookmark bookmark = mBookmarks.get(from_position);
        // 이동할 객체 삭제
        mBookmarks.remove(from_position);
        // 새로생성한 객체 저장
        mBookmarks.add(to_position,bookmark);
        // 알림
        notifyItemMoved(from_position, to_position);
        return true;
    }

    // 움직이고 있는지 아닌지 반환
    public boolean isMove() {
        return isItemMove;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, url;
        ImageView bookmarkFavicon;
        @SuppressLint("CutPasteId")
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // 제목 텍스트
            title = itemView.findViewById(R.id.tv_bookmark_title);
            // url 텍스트
            url = itemView.findViewById(R.id.tv_bookmark_url);
            // 파비콘 이미지뷰
            bookmarkFavicon = itemView.findViewById(R.id.iv_url_image);
        }

        public void onBind(Bookmark bookmark) {
            title.setText(bookmark.getTitle());
            url.setText(bookmark.getUrl());
            // 파비콘 셋팅
            // 로드 실패하면 기본 로고 이미지 셋팅
            // 로드 성공하면 파비콘을 셋팅
            // error()  실패했을 때 이미지 지정할 수 있음
            Glide.with(itemView)
                    .load(bookmark.getFaviconUrl())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(bookmarkFavicon);
        }
    }
}
