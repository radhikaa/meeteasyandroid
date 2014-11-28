package com.example.meeteasy;

public class NavDrawItem {    
    private String mTitle;
    private String mIcon;

    public NavDrawItem(){}

    public NavDrawItem(String title, String icon){
        this.mTitle = title;
        this.mIcon = icon;
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getIcon(){
        return this.mIcon;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setIcon(String icon){
        this.mIcon = icon;
    }     
}
