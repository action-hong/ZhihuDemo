package com.example.kkopite.zhihudemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.utils.Utils;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/7/1 21:06.
 */
public class NewsListDB {

    private SQLiteDatabase database;
    private static NewsListDB newsListDB;
    private String[] allColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_DATE,
            DBHelper.COLUMN_CONTENT
    };

    private NewsListDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public synchronized static  NewsListDB getInstance(Context context){
        if(newsListDB == null){
            newsListDB = new NewsListDB(context);
        }
        return newsListDB;
    }

    public void insertContent(String date,String content){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DATE, date);
        values.put(DBHelper.COLUMN_CONTENT, content);
        database.insert(DBHelper.TABLE_NAME, null, values);
    }

    public List<NewsBean> getAllNews(){
        List<NewsBean> list = new ArrayList<>();
        Gson gson = new Gson();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, allColumns, null, null, null, null, DBHelper.COLUMN_DATE+" desc");
        if(cursor.moveToFirst()){
            do{
                List<NewsBean> mList = gson.fromJson(cursor.getString(2), Utils.Types.newsListType);
                bindList(list,mList);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteAll(){
        database.delete(DBHelper.TABLE_NAME,null,null);
    }


    public void bindList( List<NewsBean> list, List<NewsBean> mList){
        for (NewsBean bean:mList) {
            list.add(bean);
        }
    }



}
