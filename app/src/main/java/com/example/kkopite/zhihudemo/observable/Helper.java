package com.example.kkopite.zhihudemo.observable;


import com.example.kkopite.zhihudemo.http.Http;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by forever 18 kkopite on 2016/7/11 17:02.
 */
public class Helper {

    public static Observable<String> getHtml(final String url){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(Http.get(url));
                subscriber.onCompleted();
            }
        });
    }
}
