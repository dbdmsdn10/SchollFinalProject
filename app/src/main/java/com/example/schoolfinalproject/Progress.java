package com.example.schoolfinalproject;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress {
    Context context;
    ProgressDialog progressDialog;
    public Progress(Context context){
        this.context=context;
        progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("로딩중");
        progressDialog.show();
    }
    public void stop(){
        progressDialog.dismiss();
    }
}
