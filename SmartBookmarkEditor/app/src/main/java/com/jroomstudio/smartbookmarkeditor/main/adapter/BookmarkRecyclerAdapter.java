package com.jroomstudio.smartbookmarkeditor.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.databinding.MainBookmarkItemBinding;
import com.jroomstudio.smartbookmarkeditor.databinding.MainFragBinding;

import java.util.List;

public class BookmarkRecyclerAdapter
        extends RecyclerView.Adapter<BookmarkRecyclerAdapter.ItemViewHolder> {

    // 북마크 리스트 멤버변수
    private List<Bookmark> mBookmarks;

    // 북마크 원격과 로컬 데이터소스 액세스

    private BookmarksRepository mBookmarksRepository;

    // 메인 프래그먼트 데이터 바인딩
    private MainFragBinding mMainFragBinding;

    // 북마크 아이템 데이터 바인딩
    private MainBookmarkItemBinding mBookmarkItemBinding;

    /**
     * 어댑터 생성자
     **/
    public BookmarkRecyclerAdapter(List<Bookmark> bookmarks,
                                   BookmarksRepository bookmarksRepository,
                                   MainFragBinding mainFragBinding){
        setBookmarks(bookmarks);
        mBookmarksRepository = bookmarksRepository;
        mMainFragBinding = mainFragBinding;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBookmarkItemBinding = MainBookmarkItemBinding.inflate(inflater,parent,false);
        View view = mBookmarkItemBinding.getRoot();
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
        return mBookmarks.size();
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

    // 각 아이템 text 및 이미지 셋팅
    // onBind 랑 뷰홀더에서 데이터바인딩 사용하면 꼬임
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView bookmarkTitle, bookmarkUrl;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkTitle = itemView.findViewById(R.id.tv_bookmark_title);
            bookmarkUrl = itemView.findViewById(R.id.tv_bookmark_url);
        }
        public void onBind(Bookmark bookmark) {
            bookmarkTitle.setText(bookmark.getTitle());
            bookmarkUrl.setText(bookmark.getUrl());
        }
    }
}
