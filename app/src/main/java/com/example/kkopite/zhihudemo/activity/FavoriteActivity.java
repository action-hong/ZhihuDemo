package com.example.kkopite.zhihudemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.MyItemTouch;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private static final String TAG = "FavoriteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_favorite);
        final NewsListDB db = NewsListDB.getInstance(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        final List<NewsBean> mList = db.loadFavourite();
        final NewsAdapter adapter = new NewsAdapter(mList, this);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        MyItemTouch callback = new MyItemTouch(adapter);
        callback.setListener(new MyItemTouch.OnItemMoveListener() {
            @Override
            public void onItemDismiss(int position) {
                mList.remove(position);
                adapter.notifyItemRemoved(position);
                db.deleteFavourite(mList.get(position));
                Log.d(TAG, "onItemDismiss() called with: " + "position = [" + position + "]" + mList.size());
            }

            @Override
            public void onItemMove(int from, int to) {

            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);



        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);

    }
}
