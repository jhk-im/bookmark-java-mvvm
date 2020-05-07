package com.jroomstudio.smartbookmarkeditor.util;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;
import com.jroomstudio.smartbookmarkeditor.data.category.Category;
import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.BookmarkItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.itemtouch.adapter.CategoriesItemTouchRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.main.home.BookmarkRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.main.home.CategoriesRecyclerAdapter;
import com.jroomstudio.smartbookmarkeditor.notice.NoticeRecyclerAdapter;

import java.util.List;

public class RecyclerViewItemBinding {

    /**
     * notice 프래그먼트의 알림 객체 리사이클러뷰의 리스트를 관찰한다.
     **/
    @BindingAdapter("noticeItems")
    public static void setNoticeItems(RecyclerView recyclerView, List<Notice> notifications){
        try{
            NoticeRecyclerAdapter noticeAdapter =
                    (NoticeRecyclerAdapter) recyclerView.getAdapter();
            if(noticeAdapter != null){
                noticeAdapter.replaceNotifications(notifications);
            }
        }catch (ClassCastException e){
            e.getStackTrace();
        }
    }

    /**
     * 메인 프래그먼트의 북마크 리사이클러뷰의 현재 카테고리 북마크 리스트를 관찰한다.
     **/
    @BindingAdapter("bookmarkItems")
    public static void setBookmarkItems(RecyclerView recyclerView, List<Bookmark> bookmarks){
        try{
            // 메인 프래그먼트 리사이클러뷰
            BookmarkRecyclerAdapter bookmarkAdapter =
                    (BookmarkRecyclerAdapter) recyclerView.getAdapter();
            if(bookmarkAdapter != null){
                bookmarkAdapter.replaceBookmarks(bookmarks);
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }
    }

    /**
     * 메인 프래그먼트의 카테고리 리사이클러뷰의 카테고리 리스트를 관찰한다.
     **/
    @BindingAdapter("categoryItems")
    public static void setCategoryItems(RecyclerView recyclerView, List<Category> categories){

        try{
            // 메인 프래그먼트 리사이클러뷰
            CategoriesRecyclerAdapter categoryAdapter =
                    (CategoriesRecyclerAdapter) recyclerView.getAdapter();
            if(categoryAdapter != null){
                categoryAdapter.replaceCategories(categories);
            }

        }catch (ClassCastException e){
            e.getStackTrace();
        }

    }

    /**
     * 메인 프래그먼트의 북마크 리사이클러뷰의 현재 카테고리 북마크 리스트를 관찰한다.
     **/
    @BindingAdapter("bookmarkTouchItems")
    public static void setBookmarkTouchItems(RecyclerView recyclerView, List<Bookmark> bookmarks){

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
    @BindingAdapter("categoryTouchItems")
    public static void setCategoryTouchItems(RecyclerView recyclerView, List<Category> categories){

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
