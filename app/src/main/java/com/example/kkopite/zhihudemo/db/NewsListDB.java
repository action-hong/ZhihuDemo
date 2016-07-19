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


    private NewsListDB(Context context) {
        DBHelper databaseHelper = new DBHelper(context);
        database = databaseHelper.getWritableDatabase();
    }

    public synchronized static NewsListDB getInstance(Context context) {
        if (newsListDB == null) {
            newsListDB = new NewsListDB(context.getApplicationContext());
        }
        return newsListDB;
    }

    //插入一天的数据
    public void insertContent(String date, String content) {
        if(hasTheDateInDB(date)){
            //数据库已经有这个了
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DATE, date);
        values.put(DBHelper.COLUMN_CONTENT, content);
        database.insert(DBHelper.TABLE_NAME, null, values);
    }

    //是否有这个日期的数据了
    public boolean hasTheDateInDB(String date) {
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, DBHelper.COLUMN_DATE + " = ?", new String[]{date}, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            return true;
        }
        return false;
    }

    //取出具体某天的数据
    public List<NewsBean> getOneDayList(String date){
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, DBHelper.COLUMN_DATE + " = ?", new String[]{date}, null, null, null);
        List<NewsBean> list = new ArrayList<>();
        if(cursor.moveToNext()){
            list = new Gson().fromJson(cursor.getString(2), Utils.Types.newsListType);
        }
        cursor.close();
        return list;
    }

    /**
     * 得到所有数据库的item集合
     * @return 集合
     */
    public List<NewsBean> getAllNews() {
        List<NewsBean> list = new ArrayList<>();
        Gson gson = new Gson();
        //按时间顺序排列
        Cursor cursor = database.query(DBHelper.TABLE_NAME, allColumns, null, null, null, null, DBHelper.COLUMN_DATE + " desc");
        if (cursor.moveToFirst()) {
            do {
                List<NewsBean> mList = gson.fromJson(cursor.getString(2), Utils.Types.newsListType);
                //每条数据都是一个集合,多个集合合并成一个集合
                list.addAll(mList);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteAll() {
        database.delete(DBHelper.TABLE_NAME, null, null);
    }



    /**
     * 保存到收藏的数据库
     * @param news 收藏的item
     */
    public void saveFavourite(NewsBean news) {
        if (news != null) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_NEWS_ID, news.getId());
            values.put(DBHelper.COLUMN_NEWS_TITLE, news.getTitle());
            values.put(DBHelper.COLUMN_NEWS_IMAGE, news.getImages());
            database.insert(DBHelper.TABLE_FAV, null, values);
        }
    }

    /**
     * 加载收藏的
     * @return 收藏的item的集合
     */
    public List<NewsBean> loadFavourite() {
        List<NewsBean> favouriteList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_FAV, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                NewsBean news = new NewsBean();
                news.setId(cursor.getInt(1));
                news.setTitle(cursor.getString(2));
                news.setImages(cursor.getString(3));
                favouriteList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favouriteList;
    }


    /**
     * 删除收藏中的某一bean
     * @param news 删除的item
     */
    public void deleteFavourite(NewsBean news) {
        if (news != null) {
            database.delete(DBHelper.TABLE_FAV, DBHelper.COLUMN_NEWS_ID + " = ?", new String[]{news.getId() + ""});
        }
    }

    /**
     * 是否已经在收藏里面了
     * @param news 新闻
     * @return 是否在收藏中了
     */
    public boolean isFavourite(NewsBean news) {
        Cursor cursor = database.query(DBHelper.TABLE_FAV, null, DBHelper.COLUMN_NEWS_ID + " = ?", new String[]{news.getId() + ""}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

}
