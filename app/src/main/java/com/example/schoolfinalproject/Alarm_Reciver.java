package com.example.schoolfinalproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Alarm_Reciver extends BroadcastReceiver {

    Context context;
    String origin[];

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        boolean a = true;
        // intent로부터 전달받은 string
        String get_yout_string = intent.getExtras().getString("state");
        String time = intent.getExtras().getString("time");


        String origin2 = intent.getExtras().getString("origin2");

        if (origin2 != null) {
            origin = origin2.split(" ");
        } else {
            System.out.println("널임");
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
        try {
            myRef3.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {// 주기알람시작
                    AlarmInfo alarmInfo = (AlarmInfo) snapshot.getValue(AlarmInfo.class);
                    if (origin2 == null) {
                        doalarm(time, get_yout_string);
                    } else if (origin[0].equals("cycle")) {
                        int times[] = new int[6];
                        String startTime[] = alarmInfo.getStartTime().split(":");
                        String endTime[] = alarmInfo.getEndTime().split(":");
                        String cycleTime[] = alarmInfo.getCycleTime().split(":");
                        times[0] = Integer.parseInt(startTime[0]);
                        times[1] = Integer.parseInt(startTime[1]);
                        times[2] = Integer.parseInt(endTime[0]);
                        times[3] = Integer.parseInt(endTime[1]);
                        times[4] = Integer.parseInt(cycleTime[0]);
                        times[5] = Integer.parseInt(cycleTime[1]);

                        int taketime = 0;
                        int cycletimeT = times[4] * 60 + times[5];
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
                        }//총 크기 재기위한것

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
                            if (origin[1].equals(hour + ":" + min)) {
                                System.out.println(alarmInfo.getStartTime()+"\n"+alarmInfo.getEndTime()+"\n"+alarmInfo.getCycleTime());
                                System.out.println("오리진="+origin[1]);

                                System.out.println("시간="+hour + ":" + min);
                                doalarm(time, get_yout_string);
                            }
                        }
                    } else if (origin[0].equals("breakfirst")) {
                        if (origin[1].equals(alarmInfo.getBreakfirst())) {
                            doalarm(time, get_yout_string);
                        }
                    } else if (origin[0].equals("lunch")) {
                        if (origin[1].equals(alarmInfo.getLunch())) {
                            doalarm(time, get_yout_string);
                        }
                    } else if (origin[0].equals("dinner")) {
                        if (origin[1].equals(alarmInfo.getDinner())) {
                            doalarm(time, get_yout_string);
                        }
                    } else if (origin[0].equals("drug")) {
                        if (origin[1].equals(alarmInfo.getDrugresult())) {
                            doalarm(time, get_yout_string);
                        }
                    }
                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
        }

    }


    public void doalarm(String time, String get_yout_string) {

        System.out.println(time + "알람시작\n" + get_yout_string);
        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        // RingtonePlayinService로 extra string값 보내기
        service_intent.putExtra("state", get_yout_string);
        service_intent.putExtra("time", time);
        // start the ringtone service

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.context.startForegroundService(service_intent);
        } else {
            this.context.startService(service_intent);
        }

    }
}