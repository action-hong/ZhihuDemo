package com.example.kkopite.zhihudemo.task;

import android.os.Handler;
import android.os.Message;

import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;

import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/7/9 9:36.
 */
public class LoadHandler extends Handler {

    public static final int LOAD_FROM_TABLE = 0;
    public static final int LOAD_FROM_FAVORITE = 1;

    private List<NewsBean> beanList;
    private NewsAdapter adapter;
    private NewsListDB db;

    public LoadHandler(List<NewsBean> beanList, NewsListDB db, NewsAdapter adapter) {
        this.beanList = beanList;
        this.db = db;
        this.adapter = adapter;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case LOAD_FROM_TABLE:
                //取出所有数据
                beanList = db.getAllNews();
                break;
            case LOAD_FROM_FAVORITE:
                //取出收藏的数据
                beanList = db.loadFavourite();
                break;
        }
        adapter.onRefreshList(beanList);
    }
}
