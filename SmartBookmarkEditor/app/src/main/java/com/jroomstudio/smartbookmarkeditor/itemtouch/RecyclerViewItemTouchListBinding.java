package com.jroomstudio.smartbookmarkeditor.itemtouch;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.BookmarkItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.CategoriesItemTouchRecyclerAdapter;

import java.util.List;

public class RecyclerViewItemTouchListBinding {

    /**
     * 메인 프래그먼트의 북마크 리사이클러뷰의 현재 카테고리 북마크 리스트를 관찰한다.
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:bookmarkTouchItems")
    public static void setBookmarkItems(RecyclerView recyclerView, List<Bookmark> bookmarks){

        try{

            // 터치 편집 프래그먼트 리사이클러뷰
            BookmarkItemTouchRecyclerAdapter bookmarkTouchAdapter =
                    (BookmarkItemTouchRecyclerAdapter) recyclerView.getAdapter();

            if(bookmarkTouchAdapter != null){
                if(!bookmarkTouchAdapter.isMove()){
                    bookmarkTouchAdapter.replaceBookmarks(bookmarks);
                }
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }

    }

    /**
     * 메인 프래그먼트의 카테고리 리사이클러뷰의 카테고리 리스트를 관찰한다.
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:categoryTouchItems")
    public static void setCategoryItems(RecyclerView recyclerView, List<Category> categories){

        try{

            // 터치 편집 프래그먼트 리사이클러뷰
            CategoriesItemTouchRecyclerAdapter categoryTouchAdapter =
                    (CategoriesItemTouchRecyclerAdapter) recyclerView.getAdapter();

            if(categoryTouchAdapter != null){
                if(!categoryTouchAdapter.isMove()){
                    categoryTouchAdapter.replaceCategories(categories);
                }
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }

    }
}
