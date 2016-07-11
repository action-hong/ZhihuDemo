package com.example.kkopite.zhihudemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.MyItemTouch;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.observable.NewsListFromDB;
import com.example.kkopite.zhihudemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavoriteActivity extends AppCompatActivity implements NewsAdapter.CardClickListener, Observer<List<NewsBean>> {

    private List<NewsBean> mList;
    private NewsListDB db;
    private NewsAdapter adapter;

    private static final String TAG = "FavoriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_favorite);

        mList = new ArrayList<>();

        bindView();

        loadListFromDB();//数据库加载
    }

    /**
     * 从数据库加载
     */
    private void loadListFromDB() {
        NewsListFromDB.getNewsListFrommDB(NewsListFromDB.FROOM_FAV, db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }


    private void bindView() {
        db = NewsListDB.getInstance(this);

        adapter = new NewsAdapter(mList, this);
        adapter.setCardClickListener(this);
        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        MyItemTouch callback = new MyItemTouch(new MyItemTouch.OnItemMoveListener() {
            @Override
            public void onItemDismiss(int position) {
                db.deleteFavourite(mList.get(position));
                mList.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onItemMove(int from, int to) {

            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onContentClick(int position) {
        NewsBean bean = mList.get(position);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(Utils.NEWS_BEAN, bean);
        startActivity(intent);
    }

    @Override
    public void onOverflowClick(int position) {

    }

    @Override
    public void onCompleted() {
        adapter.onUpdateList(mList);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(List<NewsBean> newsBeen) {
        mList = newsBeen;
    }
}
