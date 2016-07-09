package com.example.kkopite.zhihudemo.task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.http.Http;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/6/29 16:03.
 */
public class NewsTask extends AsyncTask<String, Void, List<NewsBean>> {


    private NewsListDB db;
    private Gson gson;
    private OnSolveResponse onSolveResponse;

    public NewsTask(Context context, OnSolveResponse onSolveResponse) {
        this.db = NewsListDB.getInstance(context);
        this.onSolveResponse = onSolveResponse;
        gson = new Gson();
    }

    @Override
    protected List<NewsBean> doInBackground(String... strings) {
        try {
            String result = Http.get(strings[0]);
            return parseJsonToList(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(List<NewsBean> newsBeen) {
        onSolveResponse.solveList(newsBeen);
    }

    private List<NewsBean> parseJsonToList(String result) throws JSONException {
        JSONObject newsContent = new JSONObject(result);
        String date = newsContent.optString("date");
        JSONArray array = newsContent.getJSONArray("stories");
        List<NewsBean> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            int id = object.optInt("id");
            String title = object.optString("title");
            String img = "";
            if (object.has("images")) {
                img = (String) object.getJSONArray("images").get(0);
            }
            NewsBean bean = new NewsBean(title, img, id);
            if (i == 0) {
                bean.setDate(date);//每天的第一个设置日期，便于viewHolder选择
            }
            list.add(bean);
        }

        //首次加载,过了好几天下拉刷新,拉到底部上拉刷新的日期处理是不一样的
        onSolveResponse.solveDate(date);

        db.insertContent(date, gson.toJson(list));//将这一天的信息压成json放入数据库

        return list;

    }

    public interface OnSolveResponse {

        void solveDate(String date);

        void solveList(List<NewsBean> list);


    }

}
