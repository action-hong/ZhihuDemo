package com.example.kkopite.zhihudemo;

import android.app.Application;

import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;


/**
 * Created by forever 18 kkopite on 2016/6/26 17:34.
 */
public class MyApplication extends Application {


    private static Picasso single;
    private static NewsListDB db;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        single = Picasso.with(getApplicationContext());
        db = NewsListDB.getInstance(getApplicationContext());
    }

    public static Picasso getPicasso(){
        return single;
    }

    public static NewsListDB getDB(){
        return db;
    }




}
