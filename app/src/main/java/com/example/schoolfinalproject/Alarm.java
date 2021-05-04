package com.example.schoolfinalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {

    AlarmManager alarm_manager;
    Context context;

    TextView waketime, nightTime, cycleTime;
     Intent my_intent;


    int times[] = {0, 0, 0, 0, 0, 0};//0,1은 기상 2,3은 자는시간, 4,5는 사이클

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        this.context = this;

        waketime = findViewById(R.id.edwaketime);
        nightTime = findViewById(R.id.ednighttime);
        cycleTime = findViewById(R.id.edcycletime);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        my_intent = new Intent(this.context, Alarm_Reciver.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void doit(View view) {
        Calendar calendar = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.btn_start:
                int taketime = 0, cycletimeT = 0;
                if (times[4] == 0 && times[5] == 0) {
                    Toast.makeText(Alarm.this, "주기시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    cycletimeT = times[4] * 60 + times[5];
                    if (times[0] == times[2] && times[1] == times[3]) {
                        taketime = 24 * 60;
                    } else {
                        if (times[0] > times[2]) {
                            taketime += ((24 - times[0]) + times[2]) * 60;
                        } else {
                            taketime += (times[2] - times[0]) * 60;
                        }
                        if (times[1] > times[3]) {
                            taketime += (60 - times[1]) + times[3] - 60;
                        } else {
                            taketime += times[3] - times[1];
                        }
                    }

                    int timesize = taketime / cycletimeT;
                    // 알람매니저 설정
                    int hour = times[0];
                    int min = times[1];
                    for (int i = 0; i < timesize; i++) {
                        min += times[5];
                        if (min >= 60) {
                            hour++;
                            min -= 60;
                        }
                        hour += times[4];
                        if (hour > 24) {
                            hour -= 24;
                        }

                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 0);


                        boolean b=false;
                        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            calendar.add(Calendar.DATE, 1);
                            b=true;
                        }
                        String a="Alarm 예정 " + hour + "시 " + min + "분";
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("time", a);

                        // 알람셋팅
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);



                        if(b){
                            calendar.add(Calendar.DATE, -1);
                        }
                    }
                }
                Toast.makeText(Alarm.this, "알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_finish:
                Toast.makeText(Alarm.this, "Alarm 종료", Toast.LENGTH_SHORT).show();
                // 알람매니저 취소

                AlarmManager alarm_manager2 = (AlarmManager) getSystemService(ALARM_SERVICE);


                my_intent.putExtra("state", "alarm off");
                int i=0;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);;


                alarm_manager2.cancel(pendingIntent);
                // 알람취소
                sendBroadcast(my_intent);

                break;

            case R.id.edwaketime:
                calendar = Calendar.getInstance();
                TimePickerDialog timepicker = new TimePickerDialog(this, 2, waketimelistener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), false);
                timepicker.show();
                break;
            case R.id.ednighttime:
                calendar = Calendar.getInstance();
                timepicker = new TimePickerDialog(this, 2, nighttimelistener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), false);
                timepicker.show();
                break;
            case R.id.edcycletime:

                timepicker = new TimePickerDialog(this, 2, cycletimelistener, 0, 0, false);
                timepicker.show();
                break;
            default:

                break;
        }
    }

    private TimePickerDialog.OnTimeSetListener waketimelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            waketime.setText(hourOfDay + ":" + minute);
            times[0] = hourOfDay;
            times[1] = minute;
        }
    };
    private TimePickerDialog.OnTimeSetListener nighttimelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            nightTime.setText(hourOfDay + ":" + minute);
            times[2] = hourOfDay;
            times[3] = minute;
        }
    };
    private TimePickerDialog.OnTimeSetListener cycletimelistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            cycleTime.setText(hourOfDay + ":" + minute);
            times[4] = hourOfDay;
            times[5] = minute;
        }
    };
}
