package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
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

public class MealAlarm extends AppCompatActivity {

    AlarmManager alarm_manager;
    Context context;

    TextView breakfirst, lunch, dinner;
    FirebaseUser user;
    String snapshotKey;
    Calendar calendar;
    AlarmInfo alarmInfo =new AlarmInfo();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_alarm);
        breakfirst=findViewById(R.id.edbreakfirst);
        lunch=findViewById(R.id.edlunch);
        dinner=findViewById(R.id.eddinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        context=getApplicationContext();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
        progress=new Progress(MealAlarm.this);

        myRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                alarmInfo =(AlarmInfo) snapshot.getValue(AlarmInfo.class);
                if(alarmInfo.getBreakfirst()==null) {
                    breakfirst.setText("HH:MM");
                }else{
                    breakfirst.setText(alarmInfo.getBreakfirst());
                }
                if(alarmInfo.getLunch()==null) {
                    lunch.setText("HH:MM");
                }else{
                    lunch.setText(alarmInfo.getLunch());
                }

                if(alarmInfo.getDinner()==null) {
                    dinner.setText("HH:MM");
                }else{
                    dinner.setText(alarmInfo.getDinner());
                }
                snapshotKey=snapshot.getKey();
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
        switch (view.getId()){
            case R.id.btn_mealstart:
                if(snapshotKey!=null) {
                    DatabaseReference myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("breakfirst");
                    myRef.setValue(alarmInfo.getBreakfirst());
                    myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("lunch");
                    myRef.setValue(alarmInfo.getLunch());
                    myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey).child("dinner");
                    myRef.setValue(alarmInfo.getDinner());
                }else{
                    FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
                    myRef3.push().setValue(alarmInfo);
                }
                Alarm alarm=new Alarm();
                alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarm.alarm2(context,alarm_manager);
                break;

            case R.id.edbreakfirst:
                calendar = Calendar.getInstance();
                TimePickerDialog timepicker;
                if(alarmInfo.getBreakfirst()==null){
                    timepicker = new TimePickerDialog(this, 2, breakfirstlistener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), false);
                }else{
                    String time[]=alarmInfo.getBreakfirst().split(":");
                    timepicker = new TimePickerDialog(this, 2, breakfirstlistener, Integer.parseInt(time[0]), Integer.parseInt(time[1]), false);
                }
                timepicker.show();
                break;
            case R.id.edlunch:
                calendar = Calendar.getInstance();
                if(alarmInfo.getLunch()==null){
                    timepicker = new TimePickerDialog(this, 2, lunchlistener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), false);
                }else{
                    String time[]=alarmInfo.getDinner().split(":");
                    timepicker = new TimePickerDialog(this, 2, lunchlistener, Integer.parseInt(time[0]), Integer.parseInt(time[1]), false);
                }
                timepicker.show();
                break;
            case R.id.eddinner:
                calendar = Calendar.getInstance();
                if(alarmInfo.getDinner()==null){
                    timepicker = new TimePickerDialog(this, 2, dinnerlistener, calendar.getTime().getHours(), calendar.getTime().getMinutes(), false);
                }else{
                    String time[]=alarmInfo.getDinner().split(":");
                    timepicker = new TimePickerDialog(this, 2, dinnerlistener, Integer.parseInt(time[0]), Integer.parseInt(time[1]), false);
                }
                timepicker.show();
                break;
            case R.id.btn_reset:
                if(alarmInfo==null){
                }else if(alarmInfo.getDrugresult()>0){
                    Toast.makeText(MealAlarm.this, "약 알람이 존재하여 초기화 할수없습니다", Toast.LENGTH_SHORT).show();
                }else{
                    alarmInfo.setBreakfirst(null);
                    alarmInfo.setLunch(null);
                    alarmInfo.setDinner(null);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey);
                    myRef.setValue(alarmInfo);

                    breakfirst.setText("HH:MM");
                    lunch.setText("HH:MM");
                    dinner.setText("HH:MM");
                    alarm=new Alarm();
                    alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarm.alarm2(context,alarm_manager);
                }
                break;
            default:
                break;
        }
    }

    private TimePickerDialog.OnTimeSetListener breakfirstlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            breakfirst.setText(hourOfDay + ":" + minute);
            alarmInfo.setBreakfirst(hourOfDay + ":" + minute);//아침
        }
    };
    private TimePickerDialog.OnTimeSetListener lunchlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            lunch.setText(hourOfDay + ":" + minute);
            alarmInfo.setLunch(hourOfDay + ":" + minute);//점심
        }
    };
    private TimePickerDialog.OnTimeSetListener dinnerlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            dinner.setText(hourOfDay + ":" + minute);
            alarmInfo.setDinner(hourOfDay + ":" + minute);//저녁
        }
    };
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