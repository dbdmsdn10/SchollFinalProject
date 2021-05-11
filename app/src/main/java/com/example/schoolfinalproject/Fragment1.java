package com.example.schoolfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment1 extends Fragment {


    ViewGroup viewGroup;
    Button stopwatch, CheckBlood, showlist, graph, Calendar, alarm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        stopwatch=viewGroup.findViewById(R.id.btnstopwatch);
        CheckBlood=viewGroup.findViewById(R.id.btnCheckBlood);
        showlist=viewGroup.findViewById(R.id.btnshowlist);
        graph=viewGroup.findViewById(R.id.btngraph);
        Calendar=viewGroup.findViewById(R.id.btnCalendar);
        alarm=viewGroup.findViewById(R.id.btnalarm);

        stopwatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StopWatch.class);
            startActivity(intent);
        });

        CheckBlood.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CheckBlood.class);
            startActivity(intent);
        });
        showlist.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GetBlood.class);
            startActivity(intent);
        });
        graph.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GraphScreen.class);
            startActivity(intent);
        });
        Calendar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainCalendar.class);
            startActivity(intent);
        });
        alarm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CycleAlarm.class);
            startActivity(intent);
        });


        return viewGroup;
    }


}
