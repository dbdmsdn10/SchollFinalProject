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
    Button stopwatch, CheckBlood, showlist, graph, Calendar, cycle,meal,drug;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        stopwatch=viewGroup.findViewById(R.id.btnstopwatch);
        CheckBlood=viewGroup.findViewById(R.id.btnCheckBlood);
        showlist=viewGroup.findViewById(R.id.showlist);
        graph=viewGroup.findViewById(R.id.btngraph);
        Calendar=viewGroup.findViewById(R.id.btnCalendar);
        cycle=viewGroup.findViewById(R.id.btnalarm);
//        meal=viewGroup.findViewById(R.id.btnmeal);
//        drug=viewGroup.findViewById(R.id.btndrug);


        stopwatch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Excercise.class);
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
        cycle.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainAlarm.class);
            startActivity(intent);
        });
//        meal.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), MealAlarm.class);
//            startActivity(intent);
//        });
//        drug.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), DrugAlarm.class);
//            startActivity(intent);
//        });


        return viewGroup;
    }



}
