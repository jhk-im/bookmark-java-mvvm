package com.jroomstudio.smartbookmarkeditor.data.member.source.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;

/**
 * 로컬 데이터베이스 액세스 인터페이스
 **/
@Dao
public interface MemberDao {

    // 멤버객체 가져오기
    @Query("SELECT * FROM member WHERE member_email = :email")
    Member getMemberByEmail(String email);

    // Member 객체 저장 혹은 업데이트
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMember(Member member);

    // 다크테마 업데이트
    @Query("UPDATE member SET dark_theme = :darkTheme")
    void updateDarkTheme(boolean darkTheme);

    // 푸시알림 업데이트
    @Query("UPDATE member SET push_notice = :pushNotice")
    void updatePushNotice(boolean pushNotice);

    // 로그인 상태 업데이트
    @Query("UPDATE member SET login_status = :loginStatus")
    void updateLoginStatus(boolean loginStatus);

    // 멤버 삭제
    @Query("DELETE FROM member")
    void deleteMember();

}
