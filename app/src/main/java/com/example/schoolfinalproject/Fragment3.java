package com.example.schoolfinalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class Fragment3 extends Fragment implements SensorEventListener {
    ViewGroup viewGroup;

    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    TextView tvStepCount;
    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment3,container,false);

        if(ContextCompat.checkSelfPermission(viewGroup.getContext(),
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        tvStepCount = (TextView) viewGroup.findViewById(R.id.tvStepCount);
        sensorManager = (SensorManager) viewGroup.getContext().getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor == null) {
            Toast.makeText(viewGroup.getContext(), "센서 없음", Toast.LENGTH_SHORT).show();
        }
        return viewGroup;
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCountSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            tvStepCount.setText(String.valueOf(event.values[0]) + "걸음");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
