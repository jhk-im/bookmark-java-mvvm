package com.jroomstudio.smartbookmarkeditor.itemtouch;

import android.content.Context;

import androidx.annotation.Nullable;

import com.jroomstudio.smartbookmarkeditor.BookmarkViewModel;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local.BookmarksLocalRepository;
import com.jroomstudio.smartbookmarkeditor.main.home.MainHomeFragment;
import com.jroomstudio.smartbookmarkeditor.main.home.item.BookmarkItemNavigator;

import java.lang.ref.WeakReference;

/**
 * ({@link MainHomeFragment})의 북마크 리사이클러뷰에서 각각의 북마크의 리스너 역할
 **/
public class BookmarkItemTouchViewModel extends BookmarkViewModel {

    /**
     * 약한 참조 (Weak Reference)
     * - 가비지컬렉터가 발생하면 무조건 수거된다.
     * - 가비지컬렉터의 실행주기와 일치하며 이를 이용하여 짧은 주기에 자주 사용되는 객체를 캐시할 때 유용하다.
     **/
    @Nullable
    private WeakReference<BookmarkItemNavigator> mNavigator;

    public BookmarkItemTouchViewModel(Context context,
                                      BookmarksLocalRepository bookmarksLocalRepository) {
        super(context, bookmarksLocalRepository);
    }

    public void setNavigator(BookmarkItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }



}
