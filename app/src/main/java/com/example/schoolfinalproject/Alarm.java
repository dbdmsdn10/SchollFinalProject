package com.example.schoolfinalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Alarm extends AppCompatActivity{

    AlarmManager alarm_manager[];
    TimePicker alarm_timepicker;
    Context context;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        this.context = this;

        alarm_manager=new AlarmManager[5];
        // 알람매니저 설정
        alarm_manager[0] = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 타임피커 설정
        alarm_timepicker = findViewById(R.id.time_picker);

        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        // 알람리시버 intent 생성
        final Intent my_intent = new Intent(this.context, Alarm_Reciver.class);

        // 알람 시작 버튼
        Button alarm_on = findViewById(R.id.btn_start);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                // calendar에 시간 셋팅
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());
                calendar.set(Calendar.SECOND,0);
                // 시간 가져옴
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                Toast.makeText(Alarm.this,"Alarm 예정 " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();

                // reveiver에 string 값 넘겨주기
                my_intent.putExtra("state","alarm on");


                    // 알람셋팅
                pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarm_manager[0].set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });

        // 알람 정지 버튼
        Button alarm_off = findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Alarm.this,"Alarm 종료",Toast.LENGTH_SHORT).show();
                // 알람매니저 취소

                AlarmManager alarm_manager2 = (AlarmManager) getSystemService(ALARM_SERVICE);


                my_intent.putExtra("state","alarm off");

                pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager2.cancel(pendingIntent);
                // 알람취소
                sendBroadcast(my_intent);
            }
        });
    }
}
