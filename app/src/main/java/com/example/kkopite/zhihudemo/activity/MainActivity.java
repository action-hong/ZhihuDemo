package com.example.kkopite.zhihudemo.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.adpter.NewsAdapter;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.observable.NewsListFromDB;
import com.example.kkopite.zhihudemo.observable.NewsListFromNetObservable;
import com.example.kkopite.zhihudemo.utils.Utils;
import com.google.gson.Gson;

import java.util.Calendar;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout refreshLayout;
    private boolean useLatestLoad = false;//是否需要使用最新消息加载

    public static final int EMPTY_LOAD = 1;//数据为空时加载
    public static final int LOAD_MORE = 2;//滑到底部，上拉加载更多
    public static final int LOAD_NEW = 3;//顶部下拉刷新，加载最新内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);


        //添加滚动事件，可上啦加载更多
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
            load(EMPTY_LOAD);
        } else {
            //不要在UI线程做耗时操作,虽然这个数据量也不大
            loadListFromDB(db);
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
                //清缓存
                clearCache();
                break;
            case R.id.action_settings:
                break;
            case R.id.user_love:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.search_news:
                showDatePicker();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (datePicker, year, month, day) -> {
            month++;
            String date = year + ""
                    + (month < 10 ? "0" + month : month) + ""
                    + (day < 10 ? "0" + day : day);
            String today = Utils.getToday();
            if (Integer.parseInt(date) > Integer.parseInt(today)) {
                Toast.makeText(MainActivity.this, "那天都还没到了,哪来的新闻", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DateActivity.class);
                intent.putExtra(Utils.PICK_DATE, Utils.getTomorrow(date));
                startActivity(intent);
            }
        }
                , c.get(Calendar.YEAR)
                , c.get(Calendar.MONTH)
                , c.get(Calendar.DAY_OF_MONTH)).show();
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


    /**
     * 上拉刷新，加载更多
     */
    private void addMoreNews() {
        load(LOAD_MORE);
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        if (isUseLatestLoading()) {
            //为空时，可以使用加载最近的消息
            load(EMPTY_LOAD);
        } else {
            //加载最新内容
            load(LOAD_NEW);
        }
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onCompleted() {
        //baseActivity只把获得的数据更新
        super.onCompleted();

        adapter.setLoadStatus(NewsAdapter.PULL_LOAD_MORE);//上拉加载更多可以
        useLatestLoad = false;//此时有数据了,可以使用上拉记载更多
        refreshLayout.setEnabled(true);//下拉刷新可用
    }

    /**
     * 加载数据
     * @param flag 那种加载模式
     */
    private void load(int flag) {
        String today = Utils.getToday();//今天的日期
        String tomorrow = Utils.getTomorrow(today);//明天的日期

        String newLoad = pref.getString(Utils.LATEST_DATE, "20160701");//目前数据库中最新的日期
        String lastLoad = pref.getString(Utils.LAST_DATE, "");//目前数据库中最晚的日期
        switch (flag) {
            case EMPTY_LOAD:
                //empty
                editor.putString(Utils.LAST_DATE, today);
                editor.putString(Utils.LATEST_DATE, tomorrow);//更新至这个日期
                loadListWithRxJava(tomorrow, today);
                break;
            case LOAD_MORE:
                //load more
                String lastLastLoad = Utils.getLastDay(lastLoad);
                editor.putString(Utils.LAST_DATE, Utils.getLastDay(lastLoad));
                loadListWithRxJava(lastLoad, lastLastLoad);
                break;
            case LOAD_NEW:
                //new
                editor.putString(Utils.LATEST_DATE, tomorrow);
                loadListWithRxJava(tomorrow, newLoad);
                break;
        }
        editor.commit();
    }

    /**
     * 加载从from到to的新闻
     *
     * @param from 起始加载日期
     * @param to   终止加载日期
     */
    private void loadListWithRxJava(String from, String to) {
        NewsListFromNetObservable.ofData(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(list -> db.insertContent(Utils.getLastDay(from), new Gson().toJson(list)))
                .subscribe(this);
    }

    /**
     * 从数据库加载
     * @param db 数据库
     */
    private void loadListFromDB(NewsListDB db) {
        NewsListFromDB.getNewsListFrommDB(NewsListFromDB.FROM_ALL,db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }


}
