package com.example.kkopite.zhihudemo.utils;

import com.example.kkopite.zhihudemo.model.NewsBean;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by forever 18 kkopite on 2016/6/30 22:51.
 */
public class Utils {

    public static final String NEWS_BEAN = "news_bean";
    public static final String LAST_DATE = "last_date";//上一次更新的内容的日期
    public static final String LATEST_DATE = "latest_date";//所有内容中最新的内容的日期
    public static final String IS_FIRST_TIME = "is_first_time";//首次使用
    public static final String ZHIHU_FIRST_DAY = "20130520";//最多只能刷到这一天
    public static final String PICK_DATE = "pick_date";//选择的日期
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    /**
     * 获取今天的日期，比如今天是16年7月3日，就返回20160703
     *
     * @return 返回今天的日期
     */
    public static String getToday() {
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    /**
     * 得到下一天
     *
     * @param date 输入日期
     * @return 返回输入日期的明天
     */
    public static String getTomorrow(String date) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        return sdf.format(c.getTime());

    }

    /**
     * @param date 输入日期
     * @return 返回输入的昨天
     */
    public static String getLastDay(String date) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) - 1);
        return sdf.format(c.getTime());
    }

    public static String getDate(String date) {
        return sdf2.format(date);
    }

    public static final class Types {
        public static final Type newsListType = new TypeToken<List<NewsBean>>() {

        }.getType();
    }

    public static String getNormalDate(String date) {
        String result = date;
        try {
            result = sdf2.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
