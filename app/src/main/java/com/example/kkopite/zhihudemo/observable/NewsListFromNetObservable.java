package com.example.kkopite.zhihudemo.observable;


import android.util.Log;

import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by forever 18 kkopite on 2016/7/11 16:58.
 */
public class NewsListFromNetObservable {

    private static final String TAG = "News";

    public static Observable<List<NewsBean>> ofData(String date){

//        Observable<NewsBean> news = Helper.getHtml(Http.PASS_DAY_NEWS+date)
//                .flatMap(NewsListFromNetObservable::getStory);
//
//        return news.toList();

        return ofData(date,Utils.getLastDay(date));

    }


    public static Observable<List<NewsBean>> ofData(String from, String to) {

        List<String> mList = Utils.getArrayDate(from,to);
        //之前也是类似这个思路为什么不对呀，擦
//        Observable<Story> stories = Observable.from(strs)
//                .map(str -> Constans.Urls.ZHIHU_DAILY_BEFORE + str)
//                .flatMap(Helper::getHtml)
//                .flatMap(NewsListFromZhihuObservable::getStoriesJsonArrayObservable)
//                .flatMap(NewsListFromZhihuObservable::getStoriesObservable);

        Observable<NewsBean> stories = Observable.from(mList)
                .map(str -> Http.PASS_DAY_NEWS+ str)
                .flatMap(Helper::getHtml)
                .flatMap(NewsListFromNetObservable::getStory);


        return stories.toList();
    }

    private static Observable<NewsBean> getStory(String html) {
        return Observable.create(new Observable.OnSubscribe<NewsBean>() {
            @Override
            public void call(Subscriber<? super NewsBean> subscriber) {
                try {
                    JSONObject object = new JSONObject(html);
                    String date = object.getString("date");
                    JSONArray array = object.getJSONArray("stories");
                    Log.i(TAG, "call: array" + date);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject newsJson = array.getJSONObject(i);
                        subscriber.onNext(getStoryFromJSON(newsJson, i, date));
                        subscriber.onCompleted();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static NewsBean getStoryFromJSON(JSONObject newsJson, int i, String date) {
        NewsBean story = new NewsBean();

        try {
            story.setId(newsJson.getInt("id"));
            story.setTitle(newsJson.getString("title"));
            story.setImages(getThumbnailUrlForStory(newsJson));//获取第一张图片
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (i == 0) {
            story.setDate(date);
        }

        return story;
    }



    public static Observable<JSONArray> getStoriesJsonArrayObservable(final String html){
        return Observable.create(new Observable.OnSubscribe<JSONArray>() {
            @Override
            public void call(Subscriber<? super JSONArray> subscriber) {
                try {
                    JSONObject object = new JSONObject(html);
                    subscriber.onNext(object.getJSONArray("stories"));
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<NewsBean> getNewsObservable(final JSONArray array) {
        return Observable.create(new Observable.OnSubscribe<NewsBean>() {
            @Override
            public void call(Subscriber<? super NewsBean> subscriber) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject newsJson = array.getJSONObject(i);
                        subscriber.onNext(getNewsFromJSON(newsJson));
                    }
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static NewsBean getNewsFromJSON(JSONObject jsonStory) throws JSONException {
        NewsBean story = new NewsBean();

        story.setId(jsonStory.getInt("id"));
        story.setTitle(jsonStory.getString("title"));
        story.setImages(getThumbnailUrlForStory(jsonStory));//获取第一张图片

        return story;
    }

    private static String getThumbnailUrlForStory(JSONObject jsonStory) throws JSONException {
        if (jsonStory.has("images")) {
            return (String) jsonStory.getJSONArray("images").get(0);
        } else {
            return null;
        }
    }


}
