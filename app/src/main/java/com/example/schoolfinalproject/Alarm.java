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
    AlarmInfo alarmInfo = new AlarmInfo();
    Calendar calendar = Calendar.getInstance();
    Intent my_intent;
    AlarmInfo mealAlarmInfo = new AlarmInfo();
    int i = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void alarm2(Context context, AlarmManager alarm_manager) {
        my_intent = new Intent(context, Alarm_Reciver.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
        myRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {// 주기알람시작
                alarmInfo = (AlarmInfo) snapshot.getValue(AlarmInfo.class);
                if (alarmInfo.getStartTime() != null) {
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
                    System.out.println("크기" + timesize);
                    // 알람매니저 설정
                    int hour = times[0];
                    int min = times[1];
                    for (i = 0; i < timesize; i++) {
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
                        String a = "정기 알람 " + hour + "시 " + min + "분";
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("time", a);
                        String type="cycle "+hour+":"+min;
                        my_intent.putExtra("origin2",type);
                        System.out.println(type);

                        // 알람셋팅
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        if (b) {
                            calendar.add(Calendar.DATE, -1);
                        }
                    }
                }
                int drugResult = alarmInfo.getDrugresult();//약알람
                boolean drugCan = false;
                if (drugResult == 1 || drugResult == 2 || drugResult == 3) {
                    drugCan = true;
                } else if (drugResult == 0) {
                    String Time[] = alarmInfo.getDrugtime().split(":");
                    int hour = Integer.parseInt(Time[0]);
                    int min = Integer.parseInt(Time[1]);
                    drugAlarm(hour, min, drugResult, alarm_manager, context);
                }

                if (alarmInfo.getBreakfirst() != null) {//식사알람 시작
                    int times[] = {0, 0};
                    if (alarmInfo.getBreakfirst() != null) {
                        String Time[] = alarmInfo.getBreakfirst().split(":");
                        times[0] = Integer.parseInt(Time[0]);
                        times[1] = Integer.parseInt(Time[1]);
                        if (drugCan) {
                            drugAlarm(times[0], times[1], drugResult, alarm_manager, context);
                        }
                        times[0] = (2 + times[0]) % 24;

                        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
                        calendar.set(Calendar.MINUTE, times[1]);
                        calendar.set(Calendar.SECOND, 0);
                        boolean b = false;
                        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            calendar.add(Calendar.DATE, 1);
                            b = true;
                        }
                        String a = "아침 이후 알람 " + times[0] + "시 " + times[1] + "분";
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("time", a);
                        String type="breakfirst "+alarmInfo.getBreakfirst();
                        my_intent.putExtra("origin2",type);
                        System.out.println(a);

                        // 알람셋팅
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        if (b) {
                            calendar.add(Calendar.DATE, -1);
                        }

                    }

                    if (alarmInfo.getLunch() != null) {
                        String Time[] = alarmInfo.getLunch().split(":");
                        times[0] = Integer.parseInt(Time[0]);
                        times[1] = Integer.parseInt(Time[1]);
                        if (drugCan) {
                            drugAlarm(times[0], times[1], drugResult, alarm_manager, context);
                        }
                        times[0] = (2 + times[0]) % 24;

                        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
                        calendar.set(Calendar.MINUTE, times[1]);
                        calendar.set(Calendar.SECOND, 0);
                        boolean b = false;
                        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            calendar.add(Calendar.DATE, 1);
                            b = true;
                        }
                        String a = "점심 이후 알람 " + times[0] + "시 " + times[1] + "분";
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("time", a);
                        String type="lunch "+alarmInfo.getLunch();
                        my_intent.putExtra("origin2",type);
                        System.out.println(a);

                        // 알람셋팅
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        if (b) {
                            calendar.add(Calendar.DATE, -1);
                        }

                    }

                    if (alarmInfo.getDinner() != null) {
                        String Time[] = alarmInfo.getDinner().split(":");
                        times[0] = Integer.parseInt(Time[0]);
                        times[1] = Integer.parseInt(Time[1]);
                        if (drugCan) {
                            drugAlarm(times[0], times[1], drugResult, alarm_manager, context);
                        }
                        times[0] = (2 + times[0]) % 24;

                        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
                        calendar.set(Calendar.MINUTE, times[1]);
                        calendar.set(Calendar.SECOND, 0);
                        boolean b = false;
                        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            calendar.add(Calendar.DATE, 1);
                            b = true;
                        }
                        String a = "저녁 이후 알람 " + times[0] + "시 " + times[1] + "분";
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("time", a);
                        String type="dinner "+alarmInfo.getDinner();
                        my_intent.putExtra("origin2", type);
                        System.out.println(a);

                        // 알람셋팅
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        if (b) {
                            calendar.add(Calendar.DATE, -1);
                        }

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

    public void drugAlarm(int hour, int min, int what, AlarmManager alarm_manager, Context context) {
        if (what == 0) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            boolean b = false;
            if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                calendar.add(Calendar.DATE, 1);
                b = true;
            }
            String a = "개인설정 약 알람 " + hour + "시 " + min + "분";
            my_intent.putExtra("state", "alarm on");
            my_intent.putExtra("time", a);
            my_intent.putExtra("origin2","drug "+alarmInfo.getDrugresult()+"");
            System.out.println(a);

            // 알람셋팅
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            if (b) {
                calendar.add(Calendar.DATE, -1);
            }
        } else if (what == 1) {
            min -= 30;
            if (min < 0) {
                hour -= 1;
                if (hour < 0) {
                    hour += 24;
                }
                min += 60;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            boolean b = false;
            if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                calendar.add(Calendar.DATE, 1);
                b = true;
            }
            String a = "식전30분 약 알람 " + hour + "시 " + min + "분";
            my_intent.putExtra("state", "alarm on");
            my_intent.putExtra("time", a);
            my_intent.putExtra("origin2","drug "+alarmInfo.getDrugresult()+"");
            System.out.println(a);

            // 알람셋팅
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            if (b) {
                calendar.add(Calendar.DATE, -1);
            }
        } else if (what == 2) {
            min -= 15;
            if (min < 0) {
                hour -= 1;
                if (hour < 0) {
                    hour += 24;
                }
                min += 60;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            boolean b = false;
            if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                calendar.add(Calendar.DATE, 1);
                b = true;
            }
            String a = "식전15분 약 알람 " + hour + "시 " + min + "분";
            my_intent.putExtra("state", "alarm on");
            my_intent.putExtra("time", a);
            my_intent.putExtra("origin2","drug "+alarmInfo.getDrugresult()+"");
            System.out.println(a);

            // 알람셋팅
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            if (b) {
                calendar.add(Calendar.DATE, -1);
            }
        } else if (what == 3) {
            hour = (hour + 1) % 24;
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            boolean b = false;
            if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                calendar.add(Calendar.DATE, 1);
                b = true;
            }
            String a = "식후 약 알람 " + hour + "시 " + min + "분";
            my_intent.putExtra("state", "alarm on");
            my_intent.putExtra("time", a);
            my_intent.putExtra("origin2","drug "+alarmInfo.getDrugresult()+"");
            System.out.println(a);

            // 알람셋팅
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i++, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            if (b) {
                calendar.add(Calendar.DATE, -1);
            }
        }

    }

}
