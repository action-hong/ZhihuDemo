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
import com.example.kkopite.zhihudemo.utils.Utils;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements NewsAdapter.CardClickListener{

    private List<NewsBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_favorite);
        final NewsListDB db = NewsListDB.getInstance(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        mList = db.loadFavourite();
        final NewsAdapter adapter = new NewsAdapter(mList, this);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        adapter.setCardClickListener(this);
        MyItemTouch callback = new MyItemTouch(adapter);
        callback.setListener(new MyItemTouch.OnItemMoveListener() {
            @Override
            public void onItemDismiss(int position) {
                db.deleteFavourite(mList.get(position));
                mList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemMove(int from, int to) {

            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

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
