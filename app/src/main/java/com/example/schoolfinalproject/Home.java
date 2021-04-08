package com.example.schoolfinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void doit(View view){
        switch (view.getId()){
            case R.id.btnstopwatch:
                Intent intent=new Intent(Home.this, StopWatch.class);
                startActivity(intent);
                break;
        }
    }
}