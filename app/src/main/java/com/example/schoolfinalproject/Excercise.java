package com.example.schoolfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Excercise extends AppCompatActivity {

    private Button btn_move;
    private Button btn_move2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise);

        btn_move = findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Excercise.this,StopWatchActivity.class);
                startActivity(intent);
            }
        });
        btn_move2 = findViewById(R.id.btn_move2);
        btn_move2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Excercise.this, Pedometer.class);
                startActivity(intent);
            }
        });
    }
}