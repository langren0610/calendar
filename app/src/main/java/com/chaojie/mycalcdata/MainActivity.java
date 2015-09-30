package com.chaojie.mycalcdata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements MyCalendarView.ClickDateListener {

    private MyCalendarView myCalendarView;

    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCalendarView = (MyCalendarView) findViewById(R.id.mycalendrview);
        myCalendarView.setOnClickDateListener(this);
    }


    @Override
    public void clickDate(long mills) {
        Timestamp timestamp = new Timestamp(mills);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i(TAG, "clickDate time[" + simpleDateFormat.format(timestamp) +  "]");
    }
}
