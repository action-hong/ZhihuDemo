package com.example.kkopite.zhihudemo.observable;


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


    public static Observable<List<NewsBean>> ofData(String from, String to) {

        List<String> mList = Utils.getArrayDate(from,to);
        Observable<NewsBean> stories = Observable.from(mList)
                .map(str -> Http.PASS_DAY_NEWS+ str)
                .flatMap(Helper::getHtml)
                .flatMap(NewsListFromNetObservable::getStory);
        return stories.toList();
    }

    public static Observable<List<NewsBean>> ofData(String data) {

        return ofData(data,Utils.getLastDay(data));
    }



    private static Observable<NewsBean> getStory(String html) {
        return Observable.create(new Observable.OnSubscribe<NewsBean>() {
            @Override
            public void call(Subscriber<? super NewsBean> subscriber) {
                try {
                    JSONObject object = new JSONObject(html);
                    String date = object.getString("date");
                    JSONArray array = object.getJSONArray("stories");

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

    private static String getThumbnailUrlForStory(JSONObject jsonStory) throws JSONException {
        if (jsonStory.has("images")) {
            return (String) jsonStory.getJSONArray("images").get(0);
        } else {
            return null;
        }
    }


}
