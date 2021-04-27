package com.example.schoolfinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
    public void doit(View view){
        switch (view.getId()){
            case R.id.btnstopwatch:
                Intent intent=new Intent(Home.this, StopWatch.class);
                startActivity(intent);
                break;
            case R.id.btnCheckBlood:
                intent=new Intent(Home.this, CheckBlood.class);
                startActivity(intent);
                break;
            case R.id.showlist:
                intent=new Intent(Home.this, GetBlood.class);
                startActivity(intent);
                break;
        }
    }
}