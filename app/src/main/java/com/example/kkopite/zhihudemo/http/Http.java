package com.example.kkopite.zhihudemo.http;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by forever 18 kkopite on 2016/6/26 17:35.
 */
public class Http {


    public static final String TODAY_NEWS = "http://news-at.zhihu.com/api/4/news/latest";
    public static final String DETAIL_NEWS = "http://news-at.zhihu.com/api/4/news/";
    public static final String PASS_DAY_NEWS = "http://news.at.zhihu.com/api/4/news/before/";


    public static String get(String address) {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(address);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                bf.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert con != null;
            con.disconnect();
        }
        return null;
    }

    public static String getDetailNews(int id) throws IOException {
        return get(DETAIL_NEWS + id);
    }


}



