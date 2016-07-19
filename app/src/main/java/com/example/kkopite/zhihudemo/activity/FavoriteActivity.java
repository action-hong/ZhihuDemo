package com.example.kkopite.zhihudemo.activity;

import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.kkopite.zhihudemo.adpter.MyItemTouch;
import com.example.kkopite.zhihudemo.observable.NewsListFromDB;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavoriteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindView();

        if (getSupportActionBar() != null){
            //toolbar添加返回
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadListFromDB();//数据库加载
    }

    /**
     * 从数据库加载
     */
    private void loadListFromDB() {
        NewsListFromDB.getNewsListFrommDB(NewsListFromDB.FROM_FAV, db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }


    private void bindView() {

        MyItemTouch callback = new MyItemTouch(position -> {
            //删除事件
            db.deleteFavourite(mList.get(position));
            mList.remove(position);
            adapter.notifyItemRemoved(position);
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onCompleted() {
        adapter.onUpdateList(mList);
    }
    
}
