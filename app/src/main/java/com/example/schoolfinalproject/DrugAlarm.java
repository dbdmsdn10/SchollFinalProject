package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
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

import java.util.ArrayList;

public class DrugAlarm extends AppCompatActivity {
    Spinner name, time;
    TextView textView;
    FirebaseUser user;
    Progress progress;
    AlarmInfo alarmInfo = new AlarmInfo();
    String snapshotKey;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String personal;
    ArrayList<String> before30 = new ArrayList();
    ArrayList<String> before15 = new ArrayList();
    ArrayList<String> after = new ArrayList();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_alarm);
        context=getApplicationContext();

        String[] pre30 = getResources().getStringArray(R.array.식전30분);
        String[] pre15 = getResources().getStringArray(R.array.식전15분);
        String[] next = getResources().getStringArray(R.array.식후);
        progress = new Progress(DrugAlarm.this);
        for (int i = 0; i < pre30.length; i++) {
            before30.add(pre30[i]);
        }
        for (int i = 0; i < pre15.length; i++) {
            before15.add(pre15[i]);
        }
        for (int i = 0; i < next.length; i++) {
            after.add(next[i]);
        }

        name = findViewById(R.id.drugspinner);
        time = findViewById(R.id.whenspinner);
        textView = findViewById(R.id.drugtime);
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());

        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    time.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                } else {
                    time.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        myRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                alarmInfo = (AlarmInfo) snapshot.getValue(AlarmInfo.class);
                name.setSelection(alarmInfo.getDrugname());
                if (alarmInfo.getDrugname() == 1) {
                    time.setVisibility(View.VISIBLE);
                    System.out.println("drug값은"+alarmInfo.getDrug());
                    time.setSelection(alarmInfo.getDrug());
                    if (alarmInfo.getDrug() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        personal = alarmInfo.getDrugtime();
                        textView.setText(alarmInfo.getDrugtime());
                    }
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
        switch (view.getId()) {
            case R.id.btndrugset:
                int name1 = name.getSelectedItemPosition();
                boolean can = true;
                if (name1 == 0) {
                    alarmInfo.setDrugresult(-1);//알람없음
                } else if (name1 == 1) {
                    int name2 = time.getSelectedItemPosition();
                    alarmInfo.setDrugname(1);
                    if (name2 == 0) {
                        if (personal == null) {
                            Toast.makeText(getApplicationContext(), "시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                            can = false;
                        } else {
                            alarmInfo.setDrug(0);
                            alarmInfo.setDrugresult(0);//개인설정
                            alarmInfo.setDrugtime(personal);
                        }
                    } else if (name2 == 1) {
                        alarmInfo.setDrug(1);//30분전
                        alarmInfo.setDrugresult(1);
                    } else if (name2 == 2) {
                        alarmInfo.setDrug(2);
                        alarmInfo.setDrugresult(2);//15분전
                    } else if (name2 == 3) {
                        alarmInfo.setDrug(3);
                        alarmInfo.setDrugresult(3);//직후(1시간후)
                    }
                } else {
                    alarmInfo.setDrugname(name1);
                    String a = (String) name.getSelectedItem();
                    if (before30.contains(a)) {
                        alarmInfo.setDrugresult(1);
                    } else if (before15.contains(a)) {
                        alarmInfo.setDrugresult(2);
                    } else if (after.contains(a)) {
                        alarmInfo.setDrugresult(3);
                    }
                }
                if (can) {
                    if (ConfirmMeal()) {
                        if (alarmInfo.getDrugresult() == -1) {
                            push();
                        } else if (!(alarmInfo.getDrug() == 0 && alarmInfo.getDrugresult() == 0)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("식사 알람을 설정해주세요").setMessage("식전, 식후 알람을 하려면 식사시간이 필요합니다.");
                            builder.setPositiveButton("확인", null);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else {
                            push();
                        }
                    } else {
                        push();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "시간을 설정해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.drugtime:
                TimePickerDialog timepicker;
                if(alarmInfo.getDrugtime()==null) {
                    timepicker = new TimePickerDialog(this, 2, timeListener, 0, 0, false);
                }else{
                    String time[]=alarmInfo.getDrugtime().split(":");
                    timepicker = new TimePickerDialog(this, 2, timeListener, Integer.parseInt(time[0]), Integer.parseInt(time[1]), false);
                }
                timepicker.show();
                break;
        }
    }

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            personal = hourOfDay + ":" + minute;
            textView.setText(hourOfDay + ":" + minute);
        }
    };

    boolean ConfirmMeal() {
        if (alarmInfo.getBreakfirst() == null && alarmInfo.getLunch() == null && alarmInfo.getDinner() == null) {
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void push() {
        DatabaseReference myRef = database.getReference("Alarm/" + user.getUid());
        Alarm alarm=new Alarm();
        AlarmManager larm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (snapshotKey == null) {
            myRef.push().setValue(alarmInfo);
            Toast.makeText(getApplicationContext(), "약 알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
            alarm.alarm2(context,larm_manager);
            finish();
        } else {
            myRef = database.getReference("Alarm").child(user.getUid()).child(snapshotKey);
            myRef.setValue(alarmInfo);
            Toast.makeText(getApplicationContext(), "약 알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
            alarm.alarm2(this,larm_manager);
        }
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