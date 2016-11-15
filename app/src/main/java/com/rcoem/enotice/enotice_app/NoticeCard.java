package com.rcoem.enotice.enotice_app;


public class NoticeCard {
    private String name;
    private int thumbnail;

    public NoticeCard() {
    }

    public NoticeCard(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
