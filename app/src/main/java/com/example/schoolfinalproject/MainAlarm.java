package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MainAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void doit(View view) {
        switch (view.getId()){
            case R.id.cycle:
                Intent intent = new Intent(this, CycleAlarm.class);
                startActivity(intent);
                break;
            case R.id.meal:
                intent = new Intent(this, MealAlarm.class);
                startActivity(intent);
                break;
            case R.id.drug:
                intent = new Intent(this, DrugAlarm.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}