package com.jroomstudio.smartbookmarkeditor.itemtouch.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.databinding.ItemTouchCategoryItemBinding;
import com.jroomstudio.smartbookmarkeditor.itemtouch.ItemTouchEditViewModel;
import com.jroomstudio.smartbookmarkeditor.util.ItemTouchHelperListener;

import java.util.List;

public class BookmarkItemTouchRecyclerAdapter
        extends RecyclerView.Adapter<BookmarkItemTouchRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperListener {

    // 북마크 리스트 멤버변수
    private List<Bookmark> mBookmarks;

    // 북마크 원격과 로컬 데이터소스 액세스
    private BookmarksRepository mBookmarksRepository;

    // 메인프래그먼트 뷰모델
    private ItemTouchEditViewModel mViewModel;

    private ItemTouchCategoryItemBinding mDatabindig;

    /**
     * 어댑터 생성자
     **/
    public BookmarkItemTouchRecyclerAdapter(List<Bookmark> bookmarks,
                                            BookmarksRepository bookmarksRepository,
                                            ItemTouchEditViewModel viewModel){
        setBookmarks(bookmarks);
        mBookmarksRepository = bookmarksRepository;
        mViewModel = viewModel;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("test","category on create holder");
       /*
        Log.e("test","book on create holder");
        Context context = parent.getContext();
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_touch_bookmark_item, parent, false);
        */
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mDatabindig = ItemTouchCategoryItemBinding.inflate(inflater,parent,false);
        View view = mDatabindig.getRoot();

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

    // 아이템 무브
    @Override
    public boolean onItemMove(int from_position, int to_position) {
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


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, url;
        @SuppressLint("CutPasteId")
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // 제목 텍스트
            title = itemView.findViewById(R.id.tv_bookmark_url);
            // url 텍스트
            url = itemView.findViewById(R.id.tv_bookmark_url);
        }

        public void onBind(Bookmark bookmark) {
            Log.e("test","bookmark on bind");
            title.setText(bookmark.getTitle());
            url.setText(bookmark.getUrl());
        }
    }
}
