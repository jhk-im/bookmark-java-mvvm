package com.jroomstudio.smartbookmarkeditor.main;

import android.content.Context;

import com.jroomstudio.smartbookmarkeditor.BookmarkViewModel;
import com.jroomstudio.smartbookmarkeditor.data.bookmark.source.BookmarksRepository;

public class BookmarkItemViewModel extends BookmarkViewModel {

    public BookmarkItemViewModel(Context context, BookmarksRepository bookmarksRepository) {
        super(context, bookmarksRepository);
    }

    /**
     * 아이템 클릭하면 데이터 바인딩 라이브러리 호출
     **/
    public void BookmarkClicked() {
       // ... 구현
    }

}
