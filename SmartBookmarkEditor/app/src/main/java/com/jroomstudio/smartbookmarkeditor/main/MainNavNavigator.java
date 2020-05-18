package com.jroomstudio.smartbookmarkeditor.main;

public interface MainNavNavigator {

    void onClickLogin();
    void onClickHome();
    void onClickNotice();
    void onClickPIPP();
    void onClickOSL();
    void loginCompleted(boolean refresh);
    void loginFailed(boolean logout);
}
