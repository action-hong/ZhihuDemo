package com.example.kkopite.zhihudemo.model;

import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/6/29 21:01.
 */
@SuppressWarnings("ALL")
public class NewsDetail {


    /**
     * body : 啦啦啦
     * image_source : 《小武》
     * title : 小事 · 网吧里的底层生活
     * image : http://pic3.zhimg.com/2a67a8ceb5c4e9fd0216fffe8e426ae6.jpg
     * share_url : http://daily.zhihu.com/story/8417925
     * js : []
     * ga_prefix : 062422
     * section : {}
     * images : []
     * type : 0
     * id : 8417925
     * css : []
     */

    private String body;
    private String image_source;
    private String title;
    private String image;
    private String share_url;
    private String ga_prefix;
    private int type;
    private int id;
    private List<?> js;
    private List<?> images;
    private List<?> css;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<?> getJs() {
        return js;
    }

    public void setJs(List<?> js) {
        this.js = js;
    }

    public List<?> getImages() {
        return images;
    }

    public void setImages(List<?> images) {
        this.images = images;
    }

    public List<?> getCss() {
        return css;
    }

    public void setCss(List<?> css) {
        this.css = css;
    }
}
