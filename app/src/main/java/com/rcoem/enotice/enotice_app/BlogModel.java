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



    public BlogModel() {
    }

    public BlogModel(String image, String desc,String username, String title,String approved,String name, String time) {
        this.images = image;
        this.Desc = desc;
        this.approved = approved;
        this.title = title;
        this.username=username;
        this.name = name;
        this.time = time;

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

}
