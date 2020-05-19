package com.jroomstudio.smartbookmarkeditor.main;

import com.jroomstudio.smartbookmarkeditor.data.member.Member;

public interface MainNavNavigator {

    void onClickLogin();
    void onClickHome();
    void onClickNotice();
    void onClickPIPP();
    void onClickOSL();
    void loginCompleted(Member member,boolean refresh);
    void loginOut(boolean logout);
    void updateRemoteData(Member member,boolean isDarkTheme);

}
