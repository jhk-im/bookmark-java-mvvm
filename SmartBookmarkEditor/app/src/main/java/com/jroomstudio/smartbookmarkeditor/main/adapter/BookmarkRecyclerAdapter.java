package com.jroomstudio.smartbookmarkeditor.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jroomstudio.smartbookmarkeditor.R;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;
import com.jroomstudio.smartbookmarkeditor.databinding.MainBookmarkItemBinding;
import com.jroomstudio.smartbookmarkeditor.main.BookmarkItemNavigator;
import com.jroomstudio.smartbookmarkeditor.main.BookmarkItemViewModel;
import com.jroomstudio.smartbookmarkeditor.main.MainActivity;
import com.jroomstudio.smartbookmarkeditor.main.MainViewModel;

import java.util.List;

import javax.annotation.Nullable;

public class BookmarkRecyclerAdapter
        extends RecyclerView.Adapter<BookmarkRecyclerAdapter.ItemViewHolder> {

    // 북마크 리스트 멤버변수
    private List<Bookmark> mBookmarks;

    // 북마크 원격과 로컬 데이터소스 액세스
    private BookmarksRepository mBookmarksRepository;

    // 메인프래그먼트 뷰모델
    private MainViewModel mMainViewModel;

    // 북마크 아이템 데이터 바인딩
    private MainBookmarkItemBinding mBookmarkItemBinding;

    // 메인 액티비티 네비게이터
    @Nullable private BookmarkItemNavigator mItemNavigator;

    /**
     * 어댑터 생성자
     **/
    public BookmarkRecyclerAdapter(List<Bookmark> bookmarks,
                                   BookmarksRepository bookmarksRepository,
                                   MainViewModel mainViewModel, MainActivity itemNavigator){
        setBookmarks(bookmarks);
        mBookmarksRepository = bookmarksRepository;
        mMainViewModel = mainViewModel;
        mItemNavigator = itemNavigator;
    }

    // 각 아이템의 view 추가
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBookmarkItemBinding = MainBookmarkItemBinding.inflate(inflater,parent,false);
        View view = mBookmarkItemBinding.getRoot();
        return new ItemViewHolder(view,parent);
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


    public class ItemViewHolder extends RecyclerView.ViewHolder {

         ImageView bookmarkFavicon;
         // 북마크 아이템뷰의 뷰모델
         BookmarkItemViewModel mBookmarkItemViewModel;
        public ItemViewHolder(@NonNull View itemView, ViewGroup viewGroup) {
            super(itemView);
            // 파비콘 이미지뷰
            bookmarkFavicon = itemView.findViewById(R.id.iv_url_image);

            // 뷰모델 생성
            mBookmarkItemViewModel = new BookmarkItemViewModel(
                    viewGroup.getContext().getApplicationContext(),
                    mBookmarksRepository
            );
            // 뷰모델 네비게이터 셋팅
            mBookmarkItemViewModel.setNavigator(mItemNavigator);
            // 뷰모델과 뷰를 연결
            mBookmarkItemBinding.setViewmodel(mBookmarkItemViewModel);

        }

        public void onBind(Bookmark bookmark) {

            // 뷰모델에 관찰할 북마크 아이템 셋팅
            mBookmarkItemViewModel.setBookmark(bookmark);

            // 파비콘 셋팅
            // 로드 실패하면 기본 로고 이미지 셋팅
            // 로드 성공하면 파비콘을 셋팅
            // error()  실패했을 때 이미지 지정할 수 있음
            Glide.with(itemView)
                    .load(bookmark.getFaviconUrl())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(bookmarkFavicon);

            // 롱클릭시 북마크 아이템 편집 팝업띄우기
            itemView.setOnLongClickListener(v -> {
                mMainViewModel.editLongClickBookmark(bookmark);
                return false;
            });

        }
    }
}
