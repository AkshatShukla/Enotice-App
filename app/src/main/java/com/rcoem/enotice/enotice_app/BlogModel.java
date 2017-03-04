package com.rcoem.enotice.enotice_app;

/**
 * Created by dhananjay on 27-10-2016.
 */

public class BlogModel {

    private String title;
    private String Desc;
    private String approved;
    private String images;
    private String name;
    private String username;
    private String time;
    private String DEST;
    private String block;
    private int type;
    private String profileImg;
    private String label;
    private int level;



    public BlogModel() {
    }



    public BlogModel(String image, String desc, String username, String title, String approved, String name, String time, String DEST, String block, int type, String profileImg, String label, int level) {
        this.images = image;
        this.Desc = desc;
        this.approved = approved;
        this.title = title;
        this.username=username;
        this.name = name;
        this.time = time;
        this.DEST = DEST;
        this.block = block;
        this.type = type;
        this.profileImg = profileImg;
        this.label = label;
        this.level = level;
    }

    public String getDEST() {
        return DEST;
    }

    public void setDEST(String DEST) {
        this.DEST = DEST;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        this.Desc = desc;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getApproved() {return approved;}

    public void setApproved(String approved) {this.approved = approved;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getTime() {return time;}

    public void setTime(String time) {this.time = time;}

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }
}
