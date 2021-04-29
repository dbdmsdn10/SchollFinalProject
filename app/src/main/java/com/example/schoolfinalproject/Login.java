package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText id_Login_edit, pass_Login_edit;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id_Login_edit = findViewById(R.id.id);
        pass_Login_edit = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
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
                    lohinUser(stremail, strpasswd);
                }
                break;
            case R.id.register://회원가입
                Intent intent = new Intent(Login.this, Logup.class);
                startActivity(intent);

                break;
//            case R.id.test:
//                Intent intent = new Intent(Login.this, Logup.class);
//                startActivity(intent);
//                InputStream inputStream = getResources().openRawResource(R.raw.dataoriginal);
//
//                Parsing a = new Parsing();
//                a.Parsing(inputStream);
//                break;

            default:
                break;

        }
    }

    public void lohinUser(String strEmail, String strPasswd) {
        mAuth.signInWithEmailAndPassword(strEmail, strPasswd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}