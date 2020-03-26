package com.jroomstudio.commentstube.data.source.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jroomstudio.commentstube.data.Tab;

import java.util.List;

/**
 * Data Access Object for the tabs table
* */

@Dao
public interface TabsDao {

   /**
    * tabs 테이블에서 모든 tab 아이템을 선택하여 가져온다.
    * @return all tabs
    * */
   @Query("SELECT * FROM tabs")
   List<Tab> getAllTabs();

   /**
    * tabs 에서 사용하는 아이템을 가져온다.
    * */
   @Query("SELECT * FROM tabs WHERE used = 1 ")
   List<Tab> getUseTabs();

    /**
     * tabs 에서 사용하는 아이템을 가져온다.
     * */
    @Query("SELECT * FROM tabs WHERE used = 0 ")
    List<Tab> getDisableTabs();

    /**
    * name 으로 선택한 tab 아이템을 가져온다.
    * @param tabName tab 의 name.
    * @return id에 해당하는 tab 아이템
    * */
   @Query("SELECT * FROM tabs WHERE name = :tabName")
    Tab getTabByName(String tabName);

    /**
     * Tab 사용 활성 상태를 업데이트한다.
     * @param  tabName tab name
     * @param  used tab 의 사용 활성 상태
     * */
    @Query("UPDATE tabs SET used = :used WHERE name = :tabName")
    void updateUsed(String tabName, boolean used);


   /**
    * 데이터베이스에 tab 아이템을 추가한다.
    * 만약 이미 해당하는 tab 이 존재하면 replace 한다.
    * @param tab tab 객채를 추가한다.
    * */
   @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTab(Tab tab);

   /**
    * tab 의 정보를 update 한다.
    * @param tab 입력된 tab 의 정보를 업데이트한다.
    * */
    @Update
    int updateTab(Tab tab);

    /**
     * Tab id에 해당하는 tab 아이템 삭제
     * @return 삭제한 tab 의 순번을 반환한다. 항상 1이어야 한다.
     * */
    @Query("DELETE FROM tabs WHERE entryId = :tabId")
    int deleteTabById(String tabId);

}
