package com.example.kkopite.zhihudemo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.db.NewsListDB;

public class BaseActivity extends AppCompatActivity {

    public int layoutID = R.layout.activity_base;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public NewsListDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        pref = getSharedPreferences("user_zhihu",0);
        editor = pref.edit();
        editor.apply();

        db = NewsListDB.getInstance(this);
    }


}
