package com.example.kkopite.zhihudemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.task.LoadHandler;
import com.example.kkopite.zhihudemo.task.NewsTask;
import com.example.kkopite.zhihudemo.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, NewsAdapter.CardClickListener, NewsTask.OnSolveResponse {

    private SwipeRefreshLayout refreshLayout;
    private NewsAdapter adapter;
    private LinearLayoutManager llm;
    private boolean useLatestLoad = false;//是否需要使用最新消息加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutID = R.layout.activity_main;

        super.onCreate(savedInstanceState);

        List<NewsBean> newsBeanList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);

        //设置recycleView布局
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        //实例适配器,设置item点击事件
        adapter = new NewsAdapter(newsBeanList, this);
        adapter.setCardClickListener(this);
        recyclerView.setAdapter(adapter);


        //添加滚动事件
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastItemPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshLayout.isRefreshing()) {
                    //只有当recycler非空的时候，才可以上啦刷新
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !useLatestLoad &&
                            lastItemPosition + 1 == adapter.getItemCount()) {
                        refreshLayout.setEnabled(false);
                        //电脑测时太快，看不到缓冲效果
                        addMoreNews();//请求添加新数据
                        adapter.setLoadStatus(NewsAdapter.LOADING_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = llm.findLastVisibleItemPosition();
            }
        });


        if (isUseLatestLoading()) {
            //请求今天的内容
            new NewsTask(this, this).execute(Http.TODAY_NEWS);
            useLatestLoad = false;
        } else {
            //不要在UI线程做耗时操作,虽然这个数据量也不大
            new LoadHandler(newsBeanList, db, adapter).sendEmptyMessage(LoadHandler.LOAD_FROM_TABLE);
        }
    }


    /**
     * 检查是否需要加载最新内容
     *
     * @return 是否需要加载最新内容
     */
    private boolean isUseLatestLoading() {
        useLatestLoad = pref.getBoolean(Utils.IS_FIRST_TIME, true);
        if (useLatestLoad) {
            //是第一次,改成不是第一次
            editor.putBoolean(Utils.IS_FIRST_TIME, false);
            editor.commit();
        }
        return useLatestLoad;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.clear_db:
                clearCache();
                break;
            case R.id.action_settings:
                break;
            case R.id.user_love:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 清空缓存
     */
    private void clearCache() {
        adapter.clearAll();
        db.deleteAll();
        useLatestLoad = true;//为空,不可以使用上拉加载
        adapter.setLoadStatus(NewsAdapter.NOT_SHOW);//为空时，不显示上啦刷新
        editor.putBoolean(Utils.IS_FIRST_TIME, true);
        editor.commit();
    }


    private void addMoreNews() {
        String date = pref.getString(Utils.LAST_DATE, "");
        if (date.equals(Utils.ZHIHU_FIRST_DAY)) {
            //最后一天
            Toast.makeText(this, getResources().getString(R.string.no_more_news), Toast.LENGTH_SHORT).show();
        } else {
            //加载date前一天的内容
            new NewsTask(this, new NewsTask.OnSolveResponse() {
                @Override
                public void solveDate(String date) {
                    editor.putString(Utils.LAST_DATE, date);
                    editor.commit();
                }

                @Override
                public void solveList(List<NewsBean> list) {
                    adapter.onRefreshList(list);
                    adapter.setLoadStatus(NewsAdapter.PULL_LOAD_MORE);
                    refreshLayout.setEnabled(true);
                }
            }).execute(Http.PASS_DAY_NEWS + date);
        }
    }

    @Override
    public void onRefresh() {
        if (isUseLatestLoading()) {
            //为空时，可以使用加载最近的消息
//            new NewsTask(this,this).execute(Http.PASS_DAY_NEWS+"20160701");
            new NewsTask(this, this).execute(Http.TODAY_NEWS);
            adapter.setLoadStatus(NewsAdapter.PULL_LOAD_MORE);
            useLatestLoad = false;
        } else {
            String today = Utils.getToday();
            String date = pref.getString(Utils.LATEST_DATE, "20160701");
            if (today.equals(date)) {
                Toast.makeText(this, "没有什么可刷新的了", Toast.LENGTH_SHORT).show();
            } else {
                final List<NewsBean> allList = new LinkedList<>();
                editor.putString(Utils.LATEST_DATE, today);
                editor.commit();
                today = Utils.getTomorrow(today);
                date = Utils.getTomorrow(date);
                while (!today.equals(date)) {
                    //加载到之前的一天
                    final String finalToday = today;
                    final String finalDate = date;
                    new NewsTask(this, new NewsTask.OnSolveResponse() {
                        @Override
                        public void solveDate(String date) {
                            //这里就不做日期处理了，下面自动搞成下一天了
                        }

                        @Override
                        public void solveList(List<NewsBean> list) {
                            allList.addAll(list);
                            if (Utils.getLastDay(finalToday).equals(finalDate)) {
                                //此时加载完毕，更新数据
                                adapter.addNewsInFront(allList);
                            }
                        }
                    }).execute(Http.PASS_DAY_NEWS + today);
                    today = Utils.getLastDay(today);//自动转下一天
                }
            }
        }
        refreshLayout.setRefreshing(false);
    }

    /**
     * 点击item内容
     *
     * @param position 点击的位置
     */
    @Override
    public void onContentClick(int position) {
        NewsBean bean = adapter.getStoriesBeanList().get(position);
        moreDetail(this, bean);//看更多内容
    }

    /**
     * 点击overflow
     *
     * @param position 点击的位置
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
//        adapter.notifyItemChanged(position);//会有闪一下,不好
        adapter.notifyDataSetChanged();
    }

    /**
     * 跳转到详细页面
     *
     * @param context 上下文
     * @param bean    要看具体网页的实例
     */
    public void moreDetail(Context context, NewsBean bean) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(Utils.NEWS_BEAN, bean);
        startActivity(intent);
    }


    @Override
    public void solveDate(String date) {
        //加载最新消息，需要同时传入当前最新的内容的日期，以及准备加载之前一天的日期
        editor.putString(Utils.LAST_DATE, date);
        editor.putString(Utils.LATEST_DATE, date);
        editor.commit();
    }

    @Override
    public void solveList(List<NewsBean> list) {
        adapter.onRefreshList(list);
    }


}
