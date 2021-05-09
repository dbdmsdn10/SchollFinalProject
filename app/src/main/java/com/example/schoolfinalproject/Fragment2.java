package com.example.schoolfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Fragment2 extends Fragment {
    ViewGroup viewGroup;
    TextView highReason, lowReason;
    int count[][] = new int[2][3];

    String whenLow[] = {"식사를 하지않았습니다", "술을 섭취하셨습니다", "1시간 이상의 운동하셨습니다"};
    String whenHigh[] = {"탄수화물 섭취가 많았습니다", "충분한 수면을 취하셨습니다", "스트레스가 많습니다"};


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);
        highReason = viewGroup.findViewById(R.id.txthighreason);
        lowReason = viewGroup.findViewById(R.id.txtlowreason);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Progress progress = new Progress(getActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("find/" + user.getUid());
        myRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                WhenInfo mypageInfo = (WhenInfo) snapshot.getValue(WhenInfo.class);
                if (mypageInfo.getOne() == 1) {
                    count[mypageInfo.getHighorLow()][0]++;
                }
                if (mypageInfo.getTwo() == 1) {
                    count[mypageInfo.getHighorLow()][1]++;
                }
                if (mypageInfo.getThree() == 1) {
                    count[mypageInfo.getHighorLow()][2]++;
                }
                end();
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
        progress.stop();
        return viewGroup;
    }

    public void end(){
        int low=0,high=0;
        String setlow="저혈당인적이 없습니다",sethigh="고혈당인적이 없습니다";

        for(int i=0;i<3;i++){
            if(low<count[0][i]){
                low=count[0][i];
                setlow=whenLow[i];
            }else if(count[0][i]!=0&&low==count[0][i]){
                setlow+=("\n"+whenLow[i]);
            }
        }

        for(int i=0;i<3;i++){
            if(high<count[1][i]){
                high=count[1][i];
                sethigh=whenHigh[i];
            }else if(count[1][i]!=0&&high==count[1][i]){
                sethigh+=whenHigh[i];
            }
        }
        highReason.setText(sethigh);
        lowReason.setText(setlow);
    }
}
