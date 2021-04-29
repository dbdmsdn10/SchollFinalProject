package com.example.schoolfinalproject;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GraphScreen extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase database;
    private LineChart chart;

    List<Entry> values = new ArrayList<>();
    ArrayList<BloodInfo> arraylist = new ArrayList<BloodInfo>();
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        chart = findViewById(R.id.whatchart);


        refresh();

    }

    public void refresh() {

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("blood/" + user.getUid());
        myRef3.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                BloodInfo receiptInfo = (BloodInfo) snapshot.getValue(BloodInfo.class);
//                arraylist.add(receiptInfo);
//                Collections.sort(arraylist, new Comparator<BloodInfo>() {
//                    @Override
//                    public int compare(BloodInfo o1, BloodInfo o2) {
//                        return o2.getDate().compareTo(o1.getDate());
//                    }
//                });
//                for (int i = 0; i < arraylist.size(); i++) {
//                    values.add(new Entry(i, arraylist.get(i).bloodSugar));
//                }
                values.add(new Entry(i++,receiptInfo.bloodSugar));
                LineDataSet set1;
                set1 = new LineDataSet(values, "DataSet 1");

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the data sets

                // create a data object with the data sets
                LineData data = new LineData(dataSets);

                // black lines and points
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);

                // set data
                chart.setData(data);

                chart.invalidate();
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
}