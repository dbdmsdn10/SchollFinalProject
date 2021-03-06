package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;

public class Logup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText id_Login_edit, pass_Login_edit, et_passck, et_pass, et_name, NOK;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RadioButton male, female;
    boolean passbool = false;
    Progress progress;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logup);
        id_Login_edit = findViewById(R.id.et_id);
        pass_Login_edit = findViewById(R.id.et_pass);
        et_passck = findViewById(R.id.et_passck);
        et_pass = findViewById(R.id.et_pass);
        female = findViewById(R.id.et_age);
        male = findViewById(R.id.et_age2);
        et_name = findViewById(R.id.et_name);
        NOK = findViewById(R.id.NOK);
        context = getApplicationContext();

        database = FirebaseDatabase.getInstance();

        et_passck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pass.getText().toString().equals(et_passck.getText().toString())) {
                    et_passck.setBackgroundColor(00000000);
                    passbool = true;
                } else {
                    et_passck.setBackgroundColor(Color.RED);
                    passbool = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pass.getText().toString().equals(et_passck.getText().toString())) {
                    et_passck.setBackgroundColor(00000000);
                    passbool = true;
                } else {
                    et_passck.setBackgroundColor(Color.RED);
                    passbool = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        NOK.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int permissonCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                if (permissonCheck == PackageManager.PERMISSION_GRANTED) {

                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Logup.this, Manifest.permission.SEND_SMS)) {
                        Toast.makeText(getApplicationContext(), "SMS????????? ???????????????", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(Logup.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        ActivityCompat.requestPermissions(Logup.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void doit(View view) {
        switch (view.getId()) {

//            case R.id.validateButton:
//                String stremail = id_Login_edit.getText().toString();
//                boolean a=mAuth.email
//                System.out.println("?????????"+a);
//
//                break;

            case R.id.btn_register://????????????
                String stremail = id_Login_edit.getText().toString();
                String strpasswd = pass_Login_edit.getText().toString();
                if (stremail.indexOf('@') < 0) {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                } else if (strpasswd.length() < 8) {
                    Toast.makeText(getApplicationContext(), "??????????????? 8?????? ??????????????? ?????????", Toast.LENGTH_SHORT).show();
                } else if (!passbool) {
                    Toast.makeText(getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                } else if (et_name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "????????? ???????????????", Toast.LENGTH_SHORT).show();
                } else if ((!male.isChecked()) && (!female.isChecked())) {
                    Toast.makeText(getApplicationContext(), "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    progress = new Progress(Logup.this);
                    registUser(stremail, strpasswd);
                }
                break;
            case R.id.btn_cancle:
                finish();
                break;

            default:
                break;

        }
    }

    public void registUser(String strEmail, String strPasswd) {
        try {
            mAuth.createUserWithEmailAndPassword(strEmail, strPasswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        MypageInfo mypageInfo = new MypageInfo();
                        mypageInfo.setName(et_name.getText().toString());
                        mypageInfo.setAuto("N");
                        if (male.isChecked()) {
                            mypageInfo.setGender("M");
                        } else {
                            mypageInfo.setGender("F");
                        }
                        mypageInfo.setNOK(NOK.getText().toString());
                        myRef = database.getReference("Mypage").child(user.getUid());
                        myRef.push().setValue(mypageInfo);
                        Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                        database = FirebaseDatabase.getInstance();
                        progress.stop();
                        finish();
                    } else {
                        progress.stop();
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException existEmail) {
                            Toast.makeText(getApplicationContext(), "?????? ???????????? email?????????", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

}