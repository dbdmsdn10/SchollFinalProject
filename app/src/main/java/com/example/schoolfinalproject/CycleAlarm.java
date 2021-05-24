package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CycleAlarm extends AppCompatActivity {

    AlarmManager alarm_manager;
    Context context;

    TextView waketime, nightTime, cycleTime;
    Intent my_intent;
    FirebaseUser user;
    AlarmInfo alarmInfo2;
    String snapshotKey;

    int times[] = {0, 0, 0, 0, 0, 0};//0,1은 기상 2,3은 자는시간, 4,5는 사이클

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Progress progress = new Progress(CycleAlarm.this);
        this.context = getApplicationContext();

        waketime = findViewById(R.id.edwaketime);
        nightTime = findViewById(R.id.ednighttime);
        cycleTime = findViewById(R.id.edcycletime);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        my_intent = new Intent(this.context, Alarm_Reciver.class);
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());

        myRef3.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                alarmInfo2 = (AlarmInfo) snapshot.getValue(AlarmInfo.class);
                if(alarmInfo2.getStartTime()!=null) {
                    waketime.setText(alarmInfo2.getStartTime());
                    nightTime.setText(alarmInfo2.getEndTime());
                    cycleTime.setText(alarmInfo2.getCycleTime());

                    String startTime[] = alarmInfo2.getStartTime().split(":");
                    String endTime[] = alarmInfo2.getEndTime().split(":");
                    String cycleTime[] = alarmInfo2.getCycleTime().split(":");
                    times[0] = Integer.parseInt(startTime[0]);
                    times[1] = Integer.parseInt(startTime[1]);
                    times[2] = Integer.parseInt(endTime[0]);
                    times[3] = Integer.parseInt(endTime[1]);
                    times[4] = Integer.parseInt(cycleTime[0]);
                    times[5] = Integer.parseInt(cycleTime[1]);
                }
                snapshotKey = snapshot.getKey();
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
        progress.stop();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void doit(View view) {
        Calendar calendar = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.btn_start:
                if (times[0] + times[1] == 0) {
                    Toast.makeText(CycleAlarm.this, "기상시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                } else if (times[2] + times[3] == 0) {
                    Toast.makeText(CycleAlarm.this, "잘시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                } else if (times[4] == 0 && times[5] == 0) {
                    Toast.makeText(CycleAlarm.this, "주기시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    AlarmInfo alarmInfo = new AlarmInfo();
                    alarmInfo.setStartTime(times[0] + ":" + times[1]);
                    alarmInfo.setEndTime(times[2] + ":" + times[3]);
                    alarmInfo.setCycleTime(times[4] + ":" + times[5]);

                    if (snapshotKey != null) {
                        DatabaseReference myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("startTime");
                        myRef.setValue(alarmInfo.getStartTime());
                        myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("endTime");
                        myRef.setValue(alarmInfo.getEndTime());
                        myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("cycleTime");
                        myRef.setValue(alarmInfo.getCycleTime());
                    } else if (alarmInfo2 == null) {
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
                        alarm();
                        myRef3.push().setValue(alarmInfo);
                        finish();
                    }
                    alarm();
                    Toast.makeText(CycleAlarm.this, "알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
                }
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

            case R.id.btn_reset:
                if(alarmInfo2==null){
                }else{
                    alarmInfo2.setStartTime(null);
                    alarmInfo2.setEndTime(null);
                    alarmInfo2.setCycleTime(null);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("startTime");
                    myRef.setValue(alarmInfo2.getStartTime());

                    waketime.setText("HH:MM");
                    nightTime.setText("HH:MM");
                    cycleTime.setText("HH:MM");
                    alarm();
                }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void alarm() {
        Alarm alarm = new Alarm();
        alarm.alarm2(context, alarm_manager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
