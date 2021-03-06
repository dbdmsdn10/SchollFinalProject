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
        String calendar = intent.getExtras().getString("Calendar");

        assert getState != null;
        switch (getState) {
            case "alarm on":
                if (Build.VERSION.SDK_INT >= 26) {
                    String CHANNEL_ID = "default";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                    Intent intent1 = new Intent(this, CheckBlood.class);



                    intent1.putExtra("time", time);
                    intent1.putExtra("confirm", "cancel");
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle(time)
                            .setContentText("????????????????????? ???????????? ????????? ????????????")
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

        // ????????? ?????? X , ????????? ?????? ??????
        if (!this.isRunning && startId == 1 && !(calendar.equals("nonpass"))) {

            mediaPlayer = MediaPlayer.create(this, R.raw.music);
            mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;
        }

        // ????????? ?????? O , ????????? ?????? ?????? ??????
        else if (this.isRunning && startId == 0) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
        }

        // ????????? ?????? X , ????????? ?????? ?????? ??????
        else if (!this.isRunning && startId == 0) {

            this.isRunning = false;
            this.startId = 0;

        }

        // ????????? ?????? O , ????????? ?????? ?????? ??????
        else if (this.isRunning && startId == 1) {

            this.isRunning = true;
            this.startId = 1;
        } else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        Log.d("onDestory() ??????", "????????? ??????");

    }
}
