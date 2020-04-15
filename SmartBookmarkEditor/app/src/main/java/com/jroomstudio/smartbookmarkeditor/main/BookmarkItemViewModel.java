package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import com.jroomstudio.smartbookmarkeditor.BookmarkViewModel;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;

public class BookmarkItemViewModel extends BookmarkViewModel {

    public BookmarkItemViewModel(Context context, BookmarksRepository bookmarksRepository) {
        super(context, bookmarksRepository);
    }


}
