package com.example.kkopite.zhihudemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.task.NewsDetailTask;
import com.example.kkopite.zhihudemo.utils.Utils;

public class WebActivity extends AppCompatActivity{

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = (WebView) findViewById(R.id.news_detail);
        setWebView();

        NewsBean news = (NewsBean) getIntent().getSerializableExtra(Utils.NEWS_BEAN);


        new NewsDetailTask(webView,this).execute(news.getId());
    }

    private void setWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
    }


}
