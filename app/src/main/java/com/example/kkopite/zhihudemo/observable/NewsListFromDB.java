package com.example.kkopite.zhihudemo.observable;

import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by forever 18 kkopite on 2016/7/11 22:47.
 */
public class NewsListFromDB {

    public static final int FROM_ALL = 0;//从总的数据库提取
    public static final int FROM_FAV = 1;//从收藏的数据库提取


    /**
     * 从数据库库读取
     *
     * @param flag
     * @param db
     * @return
     */
    public static Observable<List<NewsBean>> getNewsListFrommDB(int flag, NewsListDB db) {

        return Observable.create(new Observable.OnSubscribe<List<NewsBean>>() {
            @Override
            public void call(Subscriber<? super List<NewsBean>> subscriber) {
                List<NewsBean> list = null;
                switch (flag) {
                    case FROM_ALL:
                        list = db.getAllNews();
                        break;
                    case FROM_FAV:
                        list = db.loadFavourite();
                        break;
                }
                if (list != null) {
                    subscriber.onNext(list);
                }
                subscriber.onCompleted();
            }
        });
    }

    public static Observable<List<NewsBean>> getOneDayListFromAll(String date,NewsListDB db){
        return Observable.create(new Observable.OnSubscribe<List<NewsBean>>() {
            @Override
            public void call(Subscriber<? super List<NewsBean>> subscriber) {
                subscriber.onNext(db.getOneDayList(date));
                subscriber.onCompleted();
            }
        });
    }
}
