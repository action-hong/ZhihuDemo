package com.example.kkopite.zhihudemo.model;

import java.io.Serializable;

/**
 * Created by forever 18 kkopite on 2016/6/26 18:04.
 */
public class NewsBean implements Serializable{


    /**
     * images : ["http://pic4.zhimg.com/71caa71c6ab89e84cf801ee32e80ac37.jpg"]
     * type : 0
     * id : 8417925
     * ga_prefix : 062422
     * title : 小事 · 网吧里的底层生活
     */

    private int id;
    private String title;
    private String images;
    private String date;

    public NewsBean(String title, String images, int id) {
        this.title = title;
        this.images = images;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
