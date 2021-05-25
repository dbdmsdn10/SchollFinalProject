package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckBlood extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText editblood;
    Spinner editkind;
    TextView btnDay, btnTime;
    Context context, context2;
    String key;

    AlarmManager alarm_manager;

    SimpleDateFormat dayformat = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    boolean whenLowbool[] = {false, false, false}, whenHighbool[] = {false, false, false};
    //    int whenLowbool[]={0,0,0},whenHighbool[]={0,0,0};
    String whenLow[] = {"식사를 하지않았습니까?", "술을 섭취하셨습니까?", "1시간 이상의 운동하셨습니까?"};
    String whenHigh[] = {"탄수화물 섭취가 많았습니까?", "충분한 수면을 취하셨습니까", "스트레스가 많습니까?"};
    String emer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_blood);

        database = FirebaseDatabase.getInstance();
        editblood = findViewById(R.id.editblood);
        editkind = findViewById(R.id.editkind);
        btnDay = findViewById(R.id.btnDay);
        btnTime = findViewById(R.id.btnTime);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        String txtday = dayformat.format(date);
        String txttime = timeformat.format(date);
        btnDay.setText(txtday);
        btnTime.setText(txttime);
        context = this;
        context2 = getApplicationContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent my_intent = new Intent(this.context, Alarm_Reciver.class);
        AlarmManager alarm_manager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = getIntent();
        emer = intent.getStringExtra("위급상황");
        String confirm = intent.getStringExtra("confirm");

        if (confirm != null && confirm.equals("cancle")) {
            my_intent.putExtra("state", "alarm off");
            int i = 0;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(CheckBlood.this, i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarm_manager2.cancel(pendingIntent);
            // 알람취소
            sendBroadcast(my_intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doit(View view) {
        switch (view.getId()) {
            case R.id.btnsetblood:
//                myRef=database.getReference("blood/").child(user.getUid());
                BloodInfo blood = new BloodInfo();

                String txtblood = editblood.getText().toString();
                if (!txtblood.equals("")) {

                    blood.setBloodSugar(Integer.parseInt(editblood.getText().toString()));
                    blood.setKind(editkind.getSelectedItem().toString());
                    blood.setDate(btnDay.getText().toString() + " " + btnTime.getText().toString());
                    if (blood.getBloodSugar() <= 75) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("위험 저혈당입니다").setMessage("포도당이나 사탕등 간식을 섭취후 15분가량 휴식을 취하세요");
                        //15분후 알람 추가
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myRef = database.getReference("blood").child(user.getUid());
                                DatabaseReference databaseReference = myRef.push();
                                databaseReference.setValue(blood);
                                key = databaseReference.getKey();
                                wheninfoM(0);
                            }
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        doAlarm();
                    } else if (blood.getBloodSugar() >= 300) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("위험 고혈당입니다").setMessage("약을 섭취하거나 없다면 의사와 상담해보세요");// 약값 받는거 사용해서 있으면 먹으라고함 수정
                        //15분후 알람 추가
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myRef = database.getReference("blood").child(user.getUid());
                                DatabaseReference databaseReference = myRef.push();
                                databaseReference.setValue(blood);
                                key = databaseReference.getKey();
                                wheninfoM(1);
                            }
                        });
                        dialog.setCancelable(false);
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        doAlarm();

                    } else if (blood.getBloodSugar() >= 200) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("고혈당입니다").setMessage("약을 섭취하거나 운동을 하는건 어떨까요");// 약값 받는거 사용해서 있으면 먹으라고함 수정

                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myRef = database.getReference("blood").child(user.getUid());
                                DatabaseReference databaseReference = myRef.push();
                                databaseReference.setValue(blood);
                                key = databaseReference.getKey();
                                wheninfoM(1);
                            }
                        });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    } else {//75~200
                        myRef = database.getReference("blood").child(user.getUid());
                        DatabaseReference databaseReference = myRef.push();
                        databaseReference.setValue(blood);
                        key = databaseReference.getKey();
                        key = myRef.getKey();
                        if (blood.getKind().equals("취침전") && blood.getBloodSugar() >= 150) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setTitle("조금 고혈당입니다").setMessage("저녁간식을 줄이거나 의사와 상담해보세요");// 약값 받는거 사용해서 있으면 먹으라고함 수정
                            dialog.setPositiveButton("확인", (dialog12, which) -> finish());
                            dialog.show();
                        } else if (blood.getKind().equals("아침 식전(8시간 이상 공복)") && blood.getBloodSugar() >= 150) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                            dialog.setTitle("조금 고혈당입니다").setMessage("저녁간식을 줄이거나 의사와 상담해보세요");// 약값 받는거 사용해서 있으면 먹으라고함 수정
                            dialog.setPositiveButton("확인", (dialog1, which) -> finish());
                            dialog.show();
                        } else {
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(this, "혈당치를 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnDay:
                String day[] = btnDay.getText().toString().split("/");
                DatePickerDialog datePicker = new DatePickerDialog(this, listener, Integer.parseInt(day[0]), Integer.parseInt(day[1]) - 1, Integer.parseInt(day[2]));
                datePicker.show();
                break;
            case R.id.btnTime:
                String time[] = btnTime.getText().toString().split(":");
                TimePickerDialog timepicker = new TimePickerDialog(this, 2, timeListener, Integer.parseInt(time[0]), Integer.parseInt(time[1]), false);
                timepicker.show();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void doAlarm() {
        Alarm alarm = new Alarm();
        alarm.alarm3(context2, alarm_manager);
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String mon = (monthOfYear + 1) + "";
            String day = dayOfMonth + "";
            if ((monthOfYear + 1) < 10) {
                mon = 0 + mon;
            }
            if (dayOfMonth < 10) {
                day = 0 + day;
            }
            btnDay.setText(year + "/" + mon + "/" + day);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTime.setText(hourOfDay + ":" + minute);
        }
    };

    public void wheninfoM(int i) {// 이상수치일시 원인분석용, i가 0이면 저혈당 1이면 고혈당
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(context);
        if (i == 0) {
            dialog2.setTitle("원인분석").setMultiChoiceItems(whenLow, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    whenLowbool[which] = isChecked;
                }
            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WhenInfo whenInfo = new WhenInfo();
                    whenInfo.setHighorLow(i);//0이면 낮은거 1이면 높을때
                    if (whenLowbool[0]) {
                        whenInfo.setOne(1);
                    }
                    if (whenLowbool[1]) {
                        whenInfo.setTwo(1);
                    }
                    if (whenLowbool[2]) {
                        whenInfo.setThree(1);
                    }
                    myRef = database.getReference("find").child(user.getUid()).child(key);
                    myRef.setValue(whenInfo);
                    finish();
                }
            }).show();
        } else if (i == 1) {
            dialog2.setTitle("원인분석").setMultiChoiceItems(whenHigh, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    whenLowbool[which] = isChecked;
                }
            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WhenInfo whenInfo = new WhenInfo();
                    whenInfo.setHighorLow(i);//0이면 낮은거 1이면 높을때
                    if (whenLowbool[0]) {
                        whenInfo.setOne(1);
                    }
                    if (whenLowbool[1]) {
                        whenInfo.setTwo(1);
                    }
                    if (whenLowbool[2]) {
                        whenInfo.setThree(1);
                    }
                    myRef = database.getReference("find").child(user.getUid()).child(key);
                    myRef.setValue(whenInfo);
                    finish();
                }
            }).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

