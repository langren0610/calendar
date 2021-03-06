package com.chaojie.mycalcdata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MyCalendarView.ClickDateListener {

    private MyCalendarView myCalendarView;

    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCalendarView = (MyCalendarView) findViewById(R.id.mycalendrview);
        myCalendarView.setOnClickDateListener(this);
        try {
            myCalendarView.setVacationDate(2015, 10, 1, 7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void clickDate(String formatterTime) {
        Log.i(TAG, "clickDate time[" + formatterTime +  "]");
    }
}
