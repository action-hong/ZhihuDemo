package com.example.kkopite.zhihudemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.kkopite.zhihudemo.MyApplication;
import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.utils.Utils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;

public class BaseActivity extends AppCompatActivity implements NewsAdapter.CardClickListener, Observer<List<NewsBean>>{

    protected int layoutID = R.layout.activity_base;
    protected SharedPreferences pref;
    protected SharedPreferences.Editor editor;
    protected NewsListDB db;

    protected  List<NewsBean> mList;
    protected NewsAdapter adapter;
    protected RecyclerView recyclerView;

    protected  LinearLayoutManager llm;

    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.init("Test");
        Logger.d("create");
        setContentView(layoutID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        init();
    }

    private void init() {
        pref = getSharedPreferences("user_zhihu",0);
        editor = pref.edit();
        editor.apply();
        db = MyApplication.getDB();

        mList = new ArrayList<>();
        adapter = new NewsAdapter(mList);
        adapter.setCardClickListener(this);
        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

        recyclerView = (RecyclerView) findViewById(R.id.news_list);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }


    /**
     * 点击item内容
     * @param position 点击位置
     */
    @Override
    public void onContentClick(int position) {
        NewsBean bean = mList.get(position);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(Utils.NEWS_BEAN, bean);
        startActivity(intent);
    }

    /**
     * 点击item爱心
     * @param position 点击位置
     */
    @Override
    public void onOverflowClick(int position) {
        NewsBean bean = adapter.getStoriesBeanList().get(position);
        if (db.isFavourite(bean)) {
            bean.setLoved(false);
            db.deleteFavourite(bean);
        } else {
            bean.setLoved(true);
            db.saveFavourite(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCompleted() {
        adapter.onRefreshList(mList);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(List<NewsBean> newsBeen) {
        this.mList = newsBeen;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db = null;
        adapter =null;
        llm = null;
        recyclerView = null;
    }

}
