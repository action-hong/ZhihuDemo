package com.example.kkopite.zhihudemo.activity;

import android.os.Bundle;

import com.example.kkopite.zhihudemo.observable.NewsListFromDB;
import com.example.kkopite.zhihudemo.observable.NewsListFromNetObservable;
import com.example.kkopite.zhihudemo.utils.Utils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by forever 18 kkopite on 2016/7/9 11:13.
 */
public class DateActivity extends BaseActivity {

    private String date;
    private String lastDate;//请求的是这个日期，但是要用这个日期的后一天请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //为毛这么慢？
        super.onCreate(savedInstanceState);
        date = getIntent().getStringExtra(Utils.PICK_DATE);
        lastDate = Utils.getLastDay(date);

        if (getSupportActionBar() != null){
            //toolbar添加返回
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(db.hasTheDateInDB(Utils.getLastDay(date))){
            loadFromDB();
            Logger.d("load db");
        }else {
            loadFromNet();
        }


    }

    /**
     * 网络取
     */
    private void loadFromNet() {
        NewsListFromNetObservable.ofData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(list -> db.insertContent(lastDate, new Gson().toJson(list)))
                .subscribe(this);
    }


    /**
     * 数据库中取
     */
    private void loadFromDB() {
        NewsListFromDB.getOneDayListFromAll(lastDate,db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }





}
