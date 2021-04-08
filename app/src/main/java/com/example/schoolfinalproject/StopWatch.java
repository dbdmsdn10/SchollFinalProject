package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopWatch extends AppCompatActivity {
    TextView txtstopwatch, doname, txtkcal;
    long time = 0, preTime = 0, pauseTime = 0, time2 = 0, time3 = 0;
    TimeThread timeThread = new TimeThread();
    SimpleDateFormat format1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);

        Intent intent = getIntent();
        format1 = new SimpleDateFormat("yyyy/MM/dd");

        txtstopwatch = findViewById(R.id.txtstopwatch);
        doname = findViewById(R.id.doname);
        txtkcal = findViewById(R.id.txtkcal);
    }

    public void mclick(View view) {
        switch (view.getId()) {
            case R.id.stopwatchstart:
                if (timeThread == null || !timeThread.isAlive()) {
                    if (time != 0) {
                        preTime = System.currentTimeMillis() - time;
                    } else {
                        preTime = System.currentTimeMillis();

                    }

                    timeThread = new TimeThread();
                    pauseTime = System.currentTimeMillis();
                    timeThread.start();
                } else {
                }

                break;
            case R.id.stopwatchpause:
                if (timeThread.isAlive()) {
                    timeThread.interrupt();
                }
                break;
            case R.id.stopwatchstop:
                if (timeThread.isAlive()) {
                    timeThread.interrupt();
                }
                time=0;
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                break;
        }
    }


    class TimeThread extends Thread {
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    sleep(10);
//                    if (DoOr) {// 스톱워치 역으로
//                        time = time3 - (System.currentTimeMillis() - preTime);
//
//                    } else {
                    time = System.currentTimeMillis() - preTime;
                    //  }
                    time2 = System.currentTimeMillis() - pauseTime;
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("스톱워치 오류" + e.toString());
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            txtstopwatch.setText(toTime(time));
        }
    };


    public String toTime(long time) {
        int h = (int) (time / 1000.0 / 60.0 / 60.0);
        int m = (int) (time / 1000.0 / 60.0 % 60);
        int s = (int) (time % (1000.0 * 60) / 1000.0);
        int ms = (int) (time % 1000 / 10.0);

        return String.format("%02d h: %02d m: %02d s: %02d ms", h, m, s, ms);
    }

}