package com.example.schoolfinalproject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String getState = intent.getExtras().getString("state");
        String time = intent.getExtras().getString("time");
        assert getState != null;
        switch (getState) {
            case "alarm on":
                if (Build.VERSION.SDK_INT >= 26) {
                    String CHANNEL_ID = "default";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                    Intent intent1=new Intent(this,CheckBlood.class);
                    if(getState.equals("위급알람")){
                        intent1.putExtra("위급알람","위급알람");
                    }
                    PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent1,0);

                  Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle(time)
                            .setContentText("혈당체크화면에 들어가면 알람이 꺼집니다")
                            .setSmallIcon(R.mipmap.notificationicon)
                            .setContentIntent(pendingIntent)
//                    .setContent(Alarm)
                            .build();

//                    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"default");
////                    builder.setSmallIcon()
//                    builder.setContentTitle(time);
//                    builder.setContentText(time);

                    startForeground(1, notification);

                }
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                onDestroy();
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {

            mediaPlayer = MediaPlayer.create(this,R.raw.music);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {

            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){

            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d("onDestory() 실행", "서비스 파괴");

    }
}
