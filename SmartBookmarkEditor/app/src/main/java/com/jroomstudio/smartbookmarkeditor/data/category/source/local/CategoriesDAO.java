package com.jroomstudio.smartbookmarkeditor.data.category.source.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jroomstudio.smartbookmarkeditor.data.category.Category;

import java.util.List;


/**
 * categories 테이블 데이터 액세스 인터페이스
 **/
@Dao
public interface CategoriesDAO {

    /**
     * categories 테이블에서 모든 Category 아이템을 가져온다.
     * @return all category
     **/
    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    /**
     * id로 category 아이템을 찾아 가져온다.
     * @param id - category 프라이머리키
     * @return id 와 일치하는 category 아이템
     **/
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(String id);

    /**
     * category 생성 후 데이터베이스에 insert
     * onConflict = OnConflictStrategy.REPLACE-> primary Key 가 동일하면 덮어쓴다는 의미
     * -> update 로도 사용할 수 있음
     * @param category 데이터베이스에 insert 될 category 아이템
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    /**
     * category update
     * @param category 업데이트 할 category 아이템
     * @return 업데이트한 작업수. 항상 1이어야 한다.
     **/
    @Update
    int updateCategory(Category category);

    /**
     * 입력된 id의 category 포지션값을 변경한다.
     * @param id 변경할 category 의 id
     * @param position 변경할 포지션
     **/
    @Query("UPDATE categories SET position = :position WHERE id = :id")
    void updatePosition(String id, int position);

    /**
     * 입련된 id 를 입력된 selected 로 boolean 변경
     * @param id 변경할 category id
     * @param selected category 의 선택 여부
     **/
    @Query("UPDATE categories SET selected = :selected WHERE id = :id")
    void updateSelected(String id, boolean selected);
    /**
     * 입력된 id를 가지고있는 category 를 삭제한다.
     * @param id 삭제할 category 의 id
     **/
    @Query("DELETE FROM categories WHERE id = :id")
    int deleteCategoryById(String id);

    /**
     * 모든 category 아이템 삭제
     **/
    @Query("DELETE FROM categories")
    void deleteAllCategories();

}
