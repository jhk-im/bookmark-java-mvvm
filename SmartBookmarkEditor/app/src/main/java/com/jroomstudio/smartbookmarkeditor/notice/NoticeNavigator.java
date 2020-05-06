package com.jroomstudio.smartbookmarkeditor.notice;

import com.jroomstudio.smartbookmarkeditor.data.notice.Notice;

public interface NoticeNavigator {

    void allDeleteItems();
    void deleteItem(String id);
    void moveToDetail(Notice notice);

}
