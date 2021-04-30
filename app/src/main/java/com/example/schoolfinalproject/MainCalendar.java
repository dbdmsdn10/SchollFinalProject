package com.example.schoolfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainCalendar extends AppCompatActivity
{
    Button addSchedule;
    Button editSchedule;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_calendar);

        addSchedule =  (Button)findViewById(R.id.btnAddSchedule);
        editSchedule = (Button)findViewById(R.id.btnEditSchedule);

        Toast.makeText(getApplicationContext(),"캘린더 화면", Toast.LENGTH_SHORT).show();

        addSchedule.setOnClickListener(click);
        editSchedule.setOnClickListener(click);
    }

    public View.OnClickListener click = new View.OnClickListener()
    {
        Intent intent;
        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.btnAddSchedule:
                    intent = new Intent(MainCalendar.this, AddCalendar.class);
                    startActivity(intent);
                    break;
                case R.id.btnEditSchedule:
                    intent = new Intent(MainCalendar.this, EditCalendar.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
