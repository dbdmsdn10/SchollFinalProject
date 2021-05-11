package com.example.schoolfinalproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Alarm {
    CycleAlarmInfo cycleAlarmInfo=new CycleAlarmInfo();
    Calendar calendar = Calendar.getInstance();
    Intent my_intent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void alarm2(Context context,AlarmManager alarm_manager){
        my_intent = new Intent(context, Alarm_Reciver.class);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid()+"/CycleAlarm");


        myRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                cycleAlarmInfo=(CycleAlarmInfo) snapshot.getValue(CycleAlarmInfo.class);
                System.out.println("값 가져옴"+cycleAlarmInfo);
                int times[]=new int[6];
                String startTime[]=cycleAlarmInfo.getStartTime().split(":");
                String endTime[]=cycleAlarmInfo.getEndTime().split(":");
                String cycleTime[]=cycleAlarmInfo.getCycleTime().split(":");
                times[0]=Integer.parseInt(startTime[0]);
                times[1]=Integer.parseInt(startTime[1]);
                times[2]=Integer.parseInt(endTime[0]);
                times[3]=Integer.parseInt(endTime[1]);
                times[4]=Integer.parseInt(cycleTime[0]);
                times[5]=Integer.parseInt(cycleTime[1]);

                int taketime=0;
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
                System.out.println("크기"+timesize);
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


                    boolean b = false;
                    if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                        calendar.add(Calendar.DATE, 1);
                        b = true;
                    }
                    String a = "Alarm 예정 " + hour + "시 " + min + "분";
                    my_intent.putExtra("state", "alarm on");
                    my_intent.putExtra("time", a);
                    System.out.println(a);

                    // 알람셋팅
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


                    if (b) {
                        calendar.add(Calendar.DATE, -1);
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

    }
}
