package com.jroomstudio.smartbookmarkeditor.data.bookmark.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jroomstudio.smartbookmarkeditor.data.bookmark.Bookmark;

import java.util.List;

/**
 * Bookmark 테이블 데이터 액세스 인터페이스
 **/
@Dao
public interface BookmarksDAO {

    /**
     * bookmarks 테이블에서 모든 bookmark 아이템을 가져온다.
     * @return all bookmarks
     **/
    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAllBookmarks();

    /**
     * bookmarks 테이블에서 입력된 카테고리의 모든 아이템을 가져온다.
     * @return all bookmarks in category
     **/
    @Query("SELECT * FROM bookmarks WHERE category = :category")
    List<Bookmark> getAllBookmarks(String category);

    /**
     * url 로 찾은 bookmark 아이템을 가져온다.
     * @param url - bookmark url
     * @return url 과 일치하는 bookmark 아이템
     **/
    @Query("SELECT * FROM bookmarks WHERE url = :url")
    Bookmark getBookmarkByUrl(String url);

    /**
     * id로 찾은 bookmark 아이템을 가져온다.
     * @param id - bookmark 프라이머리키
     * @return id 와 일치하는 bookmark 아이템
     **/
    @Query("SELECT * FROM bookmarks WHERE id = :id")
    Bookmark getBookmarkById(String id);

    /**
     * - bookmark 생성 후 데이터베이스에 insert
     * - onConflict = OnConflictStrategy.REPLACE -> primary Key 가 동일하면 덮어쓴다는 의미
     * -> update 로도 사용할 수 있음
     * @param bookmark 데이터베이스에 insert 될 bookmark 아이템
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(Bookmark bookmark);

    /**
     * bookmark update
     * @param bookmark 업데이트 할 bookmark 아이템
     * @return 업데이트한 작업수. 항상 1이어야 한다.
     **/
    @Update
    int updateBookmark(Bookmark bookmark);

    /**
     * 입력된 id의 bookmark 포지션값을 변경한다.
     * @param id 변경할 bookmark 의 id
     * @param position 변경할 포지션
    **/
    @Query("UPDATE bookmarks SET position = :position WHERE id = :id")
    void updatePosition(String id, int position);


    /**
     * 입력된 id를 가지고있는 bookmark 를 삭제한다.
     * @param id 삭제할 bookmark 의 id
     **/
    @Query("DELETE FROM bookmarks WHERE id = :id")
    int deleteBookmarkById(String id);


    /**
     * 모든 bookmark 아이템 삭제
     **/
    @Query("DELETE FROM bookmarks")
    void deleteAllBookmarks();

    /**
     * 입력 카테고리의 bookmark 아이템을 모두 삭제
     * @param category 해당 카테고리의 bookmark item 을 찾아 모두 삭제한다.
     **/
    @Query("DELETE FROM bookmarks WHERE category = :category")
    int deleteAllInCategory(String category);

}
