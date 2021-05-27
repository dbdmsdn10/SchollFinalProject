package com.example.schoolfinalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Mypage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText id_Login_edit, et_passck, et_pass, et_name, NOK;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RadioButton male, female;
    Progress progress;
    Button btnedit, cancle;
    FirebaseUser user;
    String key;
    MypageInfo mypageInfo;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logup);
        id_Login_edit = findViewById(R.id.et_id);

        et_passck = findViewById(R.id.et_passck);
        et_pass = findViewById(R.id.et_pass);
        female = findViewById(R.id.et_age);
        male = findViewById(R.id.et_age2);
        et_name = findViewById(R.id.et_name);
        NOK = findViewById(R.id.NOK);
        context=getApplicationContext();

        database = FirebaseDatabase.getInstance();


        btnedit = findViewById(R.id.btn_register);
        btnedit.setText("수정");
        cancle = findViewById(R.id.btn_cancle);
        cancle.setVisibility(View.GONE);

        id_Login_edit.setEnabled(false);
        et_pass.setVisibility(View.GONE);
        et_passck.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("Mypage/" + user.getUid());
        myRef3.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                key=snapshot.getKey();
                mypageInfo = snapshot.getValue(MypageInfo.class);
                id_Login_edit.setText(user.getEmail());
                if (mypageInfo.getGender().equals("M")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
                et_name.setText(mypageInfo.getName());
                NOK.setText(mypageInfo.getNOK());
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

        NOK.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int permissonCheck= ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                if(permissonCheck == PackageManager.PERMISSION_GRANTED){
                }else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(Mypage.this, Manifest.permission.SEND_SMS)){
                        Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(Mypage.this, new String[]{ Manifest.permission.SEND_SMS}, 1);
                    }else{
                        ActivityCompat.requestPermissions(Mypage.this, new String[]{ Manifest.permission.SEND_SMS}, 1);
                    }
                }
                return false;
            }
        });
    }

    public void doit(View view) {
        switch (view.getId()) {

            case R.id.btn_register://회원가입
                progress = new Progress(Mypage.this);
                MypageInfo mypageInfo2 =new MypageInfo();
                mypageInfo2.setName(et_name.getText().toString());
                mypageInfo2.setAuto(mypageInfo.getAuto());
                if(male.isChecked()){
                    mypageInfo2.setGender("M");
                }else{
                    mypageInfo2.setGender("F");
                }
                mypageInfo2.setNOK(NOK.getText().toString());
                myRef=database.getReference("Mypage").child(user.getUid()).child(key);
                myRef.setValue(mypageInfo2);
                progress.stop();
                finish();
                break;

            default:
                break;

        }
    }
}
