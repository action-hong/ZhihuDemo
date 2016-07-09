package com.example.kkopite.zhihudemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.task.NewsTask;
import com.example.kkopite.zhihudemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/7/9 11:13.
 */
public class DateActivity extends AppCompatActivity implements NewsAdapter.CardClickListener {

    private List<NewsBean> mList;
    private NewsListDB db;
    private NewsAdapter adapter;
    private String date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_favorite);
        date = getIntent().getStringExtra(Utils.PICK_DATE);
        bindView();
        initDate();
    }

    private void initDate() {
        new NewsTask(this, new NewsTask.OnSolveResponse() {
            @Override
            public void solveDate(String date) {

            }

            @Override
            public void solveList(List<NewsBean> list) {
                adapter.onRefreshList(list);
            }
        }).execute(Http.PASS_DAY_NEWS + date);
    }

    private void bindView() {
        mList = new ArrayList<>();
        adapter = new NewsAdapter(mList, this);
        adapter.setCardClickListener(this);
        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onContentClick(int position) {
        NewsBean bean = mList.get(position);
        Intent intent = new Intent(this,WebActivity.class);
        intent.putExtra(Utils.NEWS_BEAN,bean);
        startActivity(intent);
    }

    @Override
    public void onOverflowClick(int position) {

    }
}
