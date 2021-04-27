package com.example.schoolfinalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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


public class GetBlood extends AppCompatActivity {
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListView listview;
    ArrayList<BloodInfo> arraylist=new ArrayList<>();
    myAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_blood);

        listview=findViewById(R.id.bloodList);

        database = FirebaseDatabase.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        adapter=new myAdapter(arraylist,getApplicationContext());
        listview.setAdapter(adapter);
        refresh();
    }

    class myAdapter extends BaseAdapter{
        ArrayList<BloodInfo> arraylist;
        LayoutInflater _inflater;
        public myAdapter(ArrayList<BloodInfo> arrayList,Context context){
            this.arraylist=arrayList;
            _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arraylist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = _inflater.inflate(R.layout.list_item, parent, false);
            BloodInfo info=arraylist.get(position);
            TextView textView=convertView.findViewById(R.id.txttext);
            textView.setText("날짜: "+info.getDate()+"\n종류: "+info.getKind()+"\n혈당:  "+info.getBloodSugar());
            return convertView;
        }
    }
    public void refresh(){
        arraylist.clear();
        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference myRef3 = database3.getReference("blood/" + user.getUid() );
        myRef3.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BloodInfo receiptInfo=(BloodInfo)snapshot.getValue(BloodInfo.class);
                arraylist.add(receiptInfo);
                Collections.sort(arraylist, new Comparator<BloodInfo>() {
                    @Override
                    public int compare(BloodInfo o1, BloodInfo o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();
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