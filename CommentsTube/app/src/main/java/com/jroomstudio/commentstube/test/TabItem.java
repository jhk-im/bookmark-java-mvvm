package com.jroomstudio.commentstube.test;

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
