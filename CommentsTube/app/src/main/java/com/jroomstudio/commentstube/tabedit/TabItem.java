package com.jroomstudio.commentstube.tabedit;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;

public class TabItem {
    private String tabName;
    private int number;
    public TabItem(String name,int number){
        this.tabName = name;
        this.number = number;
    }
    public String getTabName() {
        return tabName;
    }

    @Nullable
    public String getNameForList() {
        if (!Strings.isNullOrEmpty(tabName)) {
            return tabName;
        } else {
            return null;
        }
    }

    public void setTabName(String name) {
        this.tabName = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
