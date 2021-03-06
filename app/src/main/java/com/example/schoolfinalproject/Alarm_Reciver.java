package com.example.schoolfinalproject;

import android.Manifest;
import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class Alarm_Reciver extends BroadcastReceiver {

    Context context;
    String origin[];
    private com.google.api.services.calendar.Calendar mService = null;
    private int mID = 0;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    Intent service_intent;
    GoogleAccountCredential mCredential;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String PREF_ACCOUNT_NAME = "accountName";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        boolean a = true;

        // intent????????? ???????????? string
        String get_yout_string = intent.getExtras().getString("state");
        String time = intent.getExtras().getString("time");

        String origin2 = intent.getExtras().getString("origin2");


        if (origin2 != null) {
            origin = origin2.split(" ");
        } else {
            System.out.println("??????");
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (origin2 == null) {
            doalarm(time, get_yout_string);
        } else {
            try {
                FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                DatabaseReference myRef3 = database3.getReference("Alarm/" + user.getUid());
                myRef3.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {// ??????????????????
                        AlarmInfo alarmInfo = (AlarmInfo) snapshot.getValue(AlarmInfo.class);
                        if (origin[0].equals("cycle")) {
                            if (alarmInfo.getStartTime() == null) {

                            } else {
                                int times[] = new int[6];
                                String startTime[] = alarmInfo.getStartTime().split(":");
                                String endTime[] = alarmInfo.getEndTime().split(":");
                                String cycleTime[] = alarmInfo.getCycleTime().split(":");
                                times[0] = Integer.parseInt(startTime[0]);
                                times[1] = Integer.parseInt(startTime[1]);
                                times[2] = Integer.parseInt(endTime[0]);
                                times[3] = Integer.parseInt(endTime[1]);
                                times[4] = Integer.parseInt(cycleTime[0]);
                                times[5] = Integer.parseInt(cycleTime[1]);

                                int taketime = 0;
                                int cycletimeT = times[4] * 60 + times[5];
                                if (times[0] == times[2] && times[1] == times[3]) {
                                    taketime = 24 * 60;
                                } else {
                                    if (times[0] > times[2]) {
                                        taketime += ((24 - times[0]) + times[2]) * 60;
                                    } else {
                                        taketime += (times[2] - times[0]) * 60;
                                    }
                                    if (times[1] > times[3]) {
                                        taketime += (60 - times[1]) + times[3] - 60;
                                    } else {
                                        taketime += times[3] - times[1];
                                    }
                                }//??? ?????? ???????????????

                                int timesize = taketime / cycletimeT;
                                // ??????????????? ??????
                                int hour = times[0];
                                int min = times[1];
                                for (int i = 0; i < timesize; i++) {
                                    min += times[5];
                                    if (min >= 60) {
                                        hour++;
                                        min -= 60;
                                    }
                                    hour += times[4];
                                    if (hour > 24) {
                                        hour -= 24;
                                    }
                                    if (origin[1].equals(hour + ":" + min)) {
                                        doalarm(time, get_yout_string);
                                    }
                                }
                            }
                        } else if (origin[0].equals("breakfirst")) {
                            if (alarmInfo.getBreakfirst() != null && origin[1].equals(alarmInfo.getBreakfirst())) {
                                doalarm(time, get_yout_string);
                            }
                        } else if (origin[0].equals("lunch")) {
                            if (alarmInfo.getLunch() != null && origin[1].equals(alarmInfo.getLunch())) {
                                doalarm(time, get_yout_string);
                            }
                        } else if (alarmInfo.getDinner() != null && origin[0].equals("dinner")) {
                            if (origin[1].equals(alarmInfo.getDinner())) {
                                doalarm(time, get_yout_string);
                            }
                        } else if (origin[0].equals("drug")) {
                            if (origin[1].equals(alarmInfo.getDrugresult())) {
                                doalarm(time, get_yout_string);
                            }
                        }
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
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    public void doalarm(String time, String get_yout_string) {

        System.out.println(time + "????????????\n" + get_yout_string);
        // RingtonePlayingService ????????? intent ??????
        service_intent = new Intent(context, RingtonePlayingService.class);

        // RingtonePlayinService??? extra string??? ?????????
        service_intent.putExtra("state", get_yout_string);
        service_intent.putExtra("time", time);
        try {
            mCredential = GoogleAccountCredential.usingOAuth2(
                    context,//---------------------------------------------------------------------getapplicationcontext
                    Arrays.asList(SCOPES)
            ).setBackOff(new ExponentialBackOff()); // I/O ?????? ????????? ???????????? ????????? ?????? ??????

            if (EasyPermissions.hasPermissions(context, Manifest.permission.GET_ACCOUNTS)) {

                // SharedPreferences?????? ????????? Google ?????? ????????? ????????????.

                String accountName = context.getSharedPreferences("MainCalendar", Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {

                    // ????????? ?????? ?????? ???????????? ????????????.
                    mCredential.setSelectedAccountName(accountName);

                    mCredential.setSelectedAccount(new Account(context.getSharedPreferences("MainCalendar", Context.MODE_PRIVATE)
                            .getString(PREF_ACCOUNT_NAME, null), "com.example.schoolfinalproject"));

                } else {
                    System.out.println("???????????????");
                }

                // GET_ACCOUNTS ????????? ????????? ?????? ?????????
            }
            mID = 3;
            getResultsFromApi();
        } catch (Exception e) {
            mID = 3;
            getResultsFromApi();
        }
    }

    private String getResultsFromApi() {
        new MakeRequestTask(this, mCredential).execute();

        return null;
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        Alarm_Reciver mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(Alarm_Reciver activity, GoogleAccountCredential credential) {
            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }


        @Override
        protected void onPreExecute() {
        }


        /*
         * ????????????????????? Google Calendar API ?????? ??????
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                if (mID == 3) {
                    int a = getEvent();
                    String size;
                    if (a == 0) {
                        size = "pass";
                    } else {
                        size = "nonpass";
                    }
                    service_intent.putExtra("Calendar", size);
                    // start the ringtone service

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(service_intent);
                    } else {
                        context.startService(service_intent);
                    }
                    return null;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        private int getEvent() throws IOException {

            java.util.Calendar calender;

            calender = java.util.Calendar.getInstance();

            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

            String calendarID = getCalendarID("?????? ?????? ??????");
            if (calendarID == null) {
                //????????? ?????? ?????? ??????
                return 0;
            }

            DateTime starttime = new DateTime(simpledateformat.format(calender.getTime()));

            calender.add(java.util.Calendar.SECOND, 1);
            DateTime endtime = new DateTime(simpledateformat.format(calender.getTime()));


            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(50)
                    .setTimeMin(starttime)
                    .setTimeMax(endtime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            return items.size();
        }


        @Override
        protected void onPostExecute(String output) {
            //mStatusText.setText(output);

            if (mID == 3)
                System.out.println(TextUtils.join("\n\n", eventStrings)); //mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    System.out.println("??????????????? ????????????");
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    System.out.println("????????? ????????????");
                } else {
                    System.out.println("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                    //mStatusText.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                System.out.println("?????? ?????????.");
                //mStatusText.setText("?????? ?????????.");
            }
        }
    }


    private String getCalendarID(String calendarTitle) {

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                System.out.println("??????" + e.toString());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                System.out.println("?????????1" + e.toString());
                return null;
            }
            List<CalendarListEntry> items = calendarList.getItems();


            for (CalendarListEntry calendarListEntry : items) {

                if (calendarListEntry.getSummary().toString().equals(calendarTitle)) {

                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }

}