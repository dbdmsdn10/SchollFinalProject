package com.example.schoolfinalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
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

    SimpleDateFormat dayformat = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_blood);

        database = FirebaseDatabase.getInstance();
        editblood=findViewById(R.id.editblood);
        editkind=findViewById(R.id.editkind);
        btnDay=findViewById(R.id.btnDay);
        btnTime=findViewById(R.id.btnTime);

        user = FirebaseAuth.getInstance().getCurrentUser();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String txtday = dayformat.format(date);
        String txttime = timeformat.format(date);
        btnDay.setText(txtday);
        btnTime.setText(txttime);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void doit(View view) {
        switch (view.getId()) {
            case R.id.btnsetblood:
//                myRef=database.getReference("blood/").child(user.getUid());
                BloodInfo blood=new BloodInfo();

                String txtblood=editblood.getText().toString();
                if(!txtblood.equals("")) {
                    blood.setBloodSugar(Integer.parseInt(editblood.getText().toString()));
                    blood.setKind(editkind.getSelectedItem().toString());
                    blood.setDate(btnDay.getText().toString()+" "+btnTime.getText().toString());
                    myRef=database.getReference("blood").child(user.getUid());
                    myRef.push().setValue(blood);
                    finish();
                }
                else {
                    Toast.makeText(this,"혈당치를 입력해주세요",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btncancle:
                finish();
                break;
            case R.id.btnDay:
                String day[]=btnDay.getText().toString().split("/");
                DatePickerDialog datePicker= new DatePickerDialog(this,listener,Integer.parseInt(day[0]),Integer.parseInt(day[1]),Integer.parseInt(day[2]));
                datePicker.show();
                break;
            case R.id.btnTime:
                String time[]=btnTime.getText().toString().split(":");
                TimePickerDialog timepicker=new TimePickerDialog(this,timeListener ,Integer.parseInt(time[0]),Integer.parseInt(time[1]),false);
                timepicker.show();
                break;
            default:
                break;
        }
    }
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            btnDay.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btnTime.setText(hourOfDay+":"+minute);
        }
    };
}