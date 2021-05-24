package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText id_Login_edit, pass_Login_edit;
    FirebaseDatabase database;

    CheckBox checkBox;
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            progress=new Progress(Login.this);
            FirebaseDatabase database3 = FirebaseDatabase.getInstance();
            DatabaseReference myRef3 = database3.getReference("Mypage/" + user.getUid());
            myRef3.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    MypageInfo mypageInfo = (MypageInfo) snapshot.getValue(MypageInfo.class);
                    if(mypageInfo.getAuto().equals("Y")){
                        Intent intent = new Intent(Login.this, MainActivity2.class);
                        startActivity(intent);
                        progress.stop();
                        finish();
                    }else{
                        progress.stop();
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id_Login_edit = findViewById(R.id.id);
        pass_Login_edit = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        checkBox=findViewById(R.id.autoLoginCheck);
    }

    public void doit(View view) {
        switch (view.getId()) {
            case R.id.loginButton://로그인
                String stremail = id_Login_edit.getText().toString();
                String strpasswd = pass_Login_edit.getText().toString();
                if (stremail.indexOf('@') < 0) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                } else if (strpasswd.length() < 8) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 8자리 이상이여야 합니다", Toast.LENGTH_SHORT).show();
                } else {
                    progress=new Progress(Login.this);
                    lohinUser(stremail, strpasswd);
                }
                break;
            case R.id.register://회원가입
                Intent intent = new Intent(Login.this, Logup.class);
                startActivity(intent);

                break;
            case R.id.test:
                MainCalendar mainCalendar=new MainCalendar();
                mainCalendar.onCreate(null);
                System.out.println(mainCalendar.size()+"획인용");
                break;

            default:
                break;

        }
    }

    public void lohinUser(String strEmail, String strPasswd) {
        mAuth.signInWithEmailAndPassword(strEmail, strPasswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef3 = database3.getReference("Mypage/" + user.getUid());
                    myRef3.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            DatabaseReference myRef=database.getReference("Mypage").child(user.getUid()).child(snapshot.getKey()).child("auto");
                            if(checkBox.isChecked()){
                                myRef.setValue("Y");
                            }else{
                                myRef.setValue("N");
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
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, MainActivity2.class);
                    startActivity(intent);
                    progress.stop();
                    finish();
                } else {
                    progress.stop();
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}