package com.example.kkopite.zhihudemo.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsDetail;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by forever 18 kkopite on 2016/6/29 21:06.
 */
public class NewsDetailTask extends AsyncTask<Integer,Void,NewsDetail> {
    private static final String DEFAULT = "file:///android_asset/news_detail_header_image.jpg";

    private Context context;
    private WebView webView;

    public NewsDetailTask(WebView webView,Context context){
        this.webView = webView;
        this.context = context;
    }

    @Override
    protected NewsDetail doInBackground(Integer... integers) {
        try {
            return getNewsDetailWithGson(Http.getDetailNews(integers[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(NewsDetail newsDetail) {
        String headerImage;
        if(newsDetail.getImage() == null || Objects.equals(newsDetail.getImage(), "")){
            headerImage = DEFAULT;
        }else{
            headerImage = newsDetail.getImage();
        }
        String sb = "<div class=\"img-wrap\">" +
                "<h1 class=\"headline-title\">" +
                newsDetail.getTitle() + "</h1>" +
                "<span class=\"img-source\">" +
                newsDetail.getImage_source() + "</span>" +
                "<img src=\"" + headerImage +
                "\" alt=\"\">" +
                "<div class=\"img-mask\"></div>";
        String mNewsContent = "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_content_style.css\"/>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_header_style.css\"/>"
                + newsDetail.getBody().replace("<div class=\"img-place-holder\">", sb);
        webView.loadDataWithBaseURL("file:///android_asset/", mNewsContent, "text/html", "UTF-8", null);
        Log.d("可恶呀","救救我吧");
    }

    public NewsDetail getNewsDetailWithGson(String response){
        Gson gson = new Gson();
        return gson.fromJson(response,NewsDetail.class);
    }

}
