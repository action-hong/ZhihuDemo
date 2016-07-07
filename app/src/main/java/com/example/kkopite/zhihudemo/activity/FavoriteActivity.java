package com.example.kkopite.zhihudemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        NewsListDB db = NewsListDB.getInstance(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        List<NewsBean> mList = db.loadFavourite();
        NewsAdapter adapter = new NewsAdapter(mList, this);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

    }
}
