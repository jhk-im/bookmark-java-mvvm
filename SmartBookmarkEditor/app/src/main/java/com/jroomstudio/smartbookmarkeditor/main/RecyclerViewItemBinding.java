package com.jroomstudio.smartbookmarkeditor.main;

import android.util.Log;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.BookmarkItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.CategoriesItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.main.adapter.BookmarkRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.main.adapter.CategoriesRecyclerAdapter;

import java.util.List;

public class RecyclerViewItemBinding {

    /**
     * 메인 프래그먼트의 북마크 리사이클러뷰의 현재 카테고리 북마크 리스트를 관찰한다.
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:bookmarkItems")
    public static void setBookmarkItems(RecyclerView recyclerView, List<Bookmark> bookmarks){
        try{

            // 메인 프래그먼트 리사이클러뷰
            BookmarkRecyclerAdapter bookmarkAdapter =
                    (BookmarkRecyclerAdapter) recyclerView.getAdapter();
            if(bookmarkAdapter != null){
                bookmarkAdapter.replaceBookmarks(bookmarks);
            }

            // 터치 편집 프래그먼트 리사이클러뷰
            BookmarkItemTouchRecyclerAdapter bookmarkTouchAdapter =
                    (BookmarkItemTouchRecyclerAdapter) recyclerView.getAdapter();
            if(bookmarkTouchAdapter != null){
                Log.e("fuck","fuck");
                bookmarkTouchAdapter.replaceBookmarks(bookmarks);
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }
    }

    /**
     * 메인 프래그먼트의 카테고리 리사이클러뷰의 카테고리 리스트를 관찰한다.
     **/
    @SuppressWarnings("unchecked")
    @BindingAdapter("app:categoryItems")
    public static void setCategoryItems(RecyclerView recyclerView, List<Category> categories){

        try{

            // 메인 프래그먼트 리사이클러뷰
            CategoriesRecyclerAdapter categoryAdapter =
                    (CategoriesRecyclerAdapter) recyclerView.getAdapter();
            if(categoryAdapter != null){
                categoryAdapter.replaceCategories(categories);
            }

            // 터치 편집 프래그먼트 리사이클러뷰
            CategoriesItemTouchRecyclerAdapter categoryTouchAdapter =
                    (CategoriesItemTouchRecyclerAdapter) recyclerView.getAdapter();
            if(categoryTouchAdapter != null){
                Log.e("fuck","fuck");
                categoryTouchAdapter.replaceCategories(categories);
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }

    }
}
