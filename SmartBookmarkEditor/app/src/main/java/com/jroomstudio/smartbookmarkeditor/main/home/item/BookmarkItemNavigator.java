package com.jroomstudio.smartbookmarkeditor.main.home.item;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;

/**
 * 메인 액티비티 리사이클러뷰의 북마크 목록에서 북마크를 가져올 수 있다.
 **/
public interface BookmarkItemNavigator {
    void selectedBookmark(Bookmark bookmark);
}
