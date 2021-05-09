package com.example.schoolfinalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class GraphScreen extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase database;
    private BarChart chart;


    ArrayList<BloodInfo> arraylist = new ArrayList<BloodInfo>();
    XAxis xAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        chart = findViewById(R.id.whatchart);

        xAxis = chart.getXAxis();
        refresh();

    }

    public void refresh() {

        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("blood/" + user.getUid());
        myRef3.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                BloodInfo bloodInfo = (BloodInfo) snapshot.getValue(BloodInfo.class);
                arraylist.add(bloodInfo);
                avglist();
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

    public void avglist() {
        int k=5;
        chart.clear();
        List<BarEntry> values = new ArrayList<>();
        HashMap<String,Integer> map=new HashMap();
        for (int i = 0; i < arraylist.size(); i++) {
            BloodInfo bloodInfo=arraylist.get(i);
            String date[] = bloodInfo.getDate().split(" ");
            if(map.containsKey(date[0])){
                System.out.println(date[0]+"은=="+map.get(date[0])+"   "+bloodInfo.getBloodSugar()+"   "+(map.get(date[0])+bloodInfo.getBloodSugar())/2);
                map.put(date[0],(map.get(date[0])+bloodInfo.getBloodSugar())/2);
            }else{
                map.put(date[0],bloodInfo.getBloodSugar());
            }
        }

        TreeMap<String,Integer> treeMap=new TreeMap<>(Collections.reverseOrder());
        treeMap.putAll(map);
        Iterator<String> treeMapReverseIter = treeMap.keySet().iterator();
        String list[]={"","","","","",""};
        while(treeMapReverseIter.hasNext()) {
            if(k<0){
                break;
            }

            String key = treeMapReverseIter.next();
            int value = treeMap.get( key );
            System.out.println(k+"값= "+key+"  "+value);
            values.add(new BarEntry(k, value));
            list[k--]=key;

        }
        while(k>=0){
            values.add(new BarEntry(k--, 0));
        }

//        String[] list = listString.toArray(new String[listString.size()]);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(list));//문자열 삽입
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        BarDataSet set1;
        set1 = new BarDataSet(values, "");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets


        // create a data object with the data sets
        BarData data = new BarData(dataSets);

        // black lines and points


        // set data
        chart.setData(data);

        chart.invalidate();
    }
}