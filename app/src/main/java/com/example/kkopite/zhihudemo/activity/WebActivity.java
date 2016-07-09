package com.example.kkopite.zhihudemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.task.NewsDetailTask;
import com.example.kkopite.zhihudemo.utils.Utils;

public class WebActivity extends AppCompatActivity {

    private WebView webView;
    private NewsBean news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_web);
        webView = (WebView) findViewById(R.id.news_detail);
        setWebView();


        news = (NewsBean) getIntent().getSerializableExtra(Utils.NEWS_BEAN);


        new NewsDetailTask(webView, this).execute(news.getId());


    }

    private void setWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fav, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.fav_share:
                shareNews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareNews() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);//启动的activity会在app重置的时候销毁
        share.putExtra(Intent.EXTRA_TEXT,
                news.getTitle() + " " + Http.DETAIL_NEWS + news.getId() + "分享来自知乎");
        startActivity(Intent.createChooser(share, "分享..."));
    }
}
