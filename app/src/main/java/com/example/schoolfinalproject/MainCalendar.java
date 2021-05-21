package com.example.schoolfinalproject;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//사용법 블로그 참고 추가1 시작
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import com.google.api.services.calendar.model.*;


import androidx.annotation.NonNull;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//사용법 블로그 참고 추가1 끝


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class MainCalendar extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    Button addSchedule;

    ListView eventList;//일정 출력을 위한 리스트뷰
    ArrayList<EventInfo> arrayEvent = new ArrayList<>();//일정 정보를 저장하기위한 arrayList
    EventAdapter eventAdapter;// 리스트 뷰에 텍스트뷰를 추가하여 일정 리스트 출력
    CalendarView calendarView;


    private com.google.api.services.calendar.Calendar mService = null;

    private int mID = 0;


    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    String editStarttime, editEndtime;
    Context context;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addSchedule = (Button) findViewById(R.id.btnAddSchedule);

        context=getApplicationContext();

        Toast.makeText(getApplicationContext(), "캘린더 화면", Toast.LENGTH_SHORT).show();

        addSchedule.setOnClickListener(click);
        calendarView = findViewById(R.id.calendarContent);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String mon, day;
                if ((month+1) < 10) {
                    mon = "0" + (month+1);
                } else {
                    mon = (month+1) + "";
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = dayOfMonth + "";
                }

                editStarttime = year + "-" + mon + "-" + dayOfMonth + "T00:00:00+09:00";
                editEndtime = year + "-" + mon + "-" + dayOfMonth + "T23:59:59+09:00";
                System.out.println("시작 " + editStarttime + "\n끝 " + editEndtime);
                mID = 4;
                getResultsFromApi();
            }
        });


        //리스트 뷰
        eventList = (ListView) findViewById(R.id.eventList);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");


        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

        //사용법 블로그 참고 추가3 끝
        eventAdapter = new EventAdapter(arrayEvent, getApplicationContext());
        eventList.setAdapter(eventAdapter);

        mID = 3;        //이벤트 가져오기
        getResultsFromApi();//



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //어뎁터 추가 및 리스트뷰 사용

        eventAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener click = new View.OnClickListener() {
        Intent intent;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnAddSchedule:
                    intent = new Intent(MainCalendar.this, AddCalendar.class);
                    startActivityForResult(intent,0);
                    break;
            }
        }
    };

    //현재 리스트뷰 출력이 되지 않음.....
    //리스부 사용을 위한 어뎁터
    class EventAdapter extends BaseAdapter {
        ArrayList<EventInfo> arrayEvent;
        LayoutInflater _inflater;

        public EventAdapter(ArrayList<EventInfo> arrayEvent, Context context) {
            this.arrayEvent = arrayEvent;
            _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayEvent.size();
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
            EventInfo info = arrayEvent.get(position);
            TextView textView = convertView.findViewById(R.id.txttext);
            textView.setText("일정 제목: " + info.getEventTitle() + "\n" +
                    "일정 내용: " + info.getEventContent() + "\n" +
                    "시작날짜:  " + timeParsing(info.getEventStart())+" ~ "+timeParsing(info.getEventEnd()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),EditCalendar.class);
                    intent.putExtra("id",info.getId());
                    intent.putExtra("제목",info.getEventTitle());
                    intent.putExtra("내용",info.getEventContent());
                    intent.putExtra("시작",timeParsing(info.getEventStart()));
                    intent.putExtra("종료",timeParsing(info.getEventEnd()));
                    startActivityForResult(intent,0);
                }
            });
            return convertView;
        }
    }


    //사용법 블로그 참고 추가4 시작

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     * <p>
     * 사전 조건
     * - Google Play Services 설치
     * - 유효한 구글 계정 선택
     * - 안드로이드 디바이스에서 인터넷 사용 가능
     * <p>
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */
    private String getResultsFromApi() {


        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우

            System.out.println("google Play Services를 사용할 수 없는 경우");

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

            System.out.println("getSelectedAccountName " + mCredential.getSelectedAccountName());

            System.out.println("유효한 Google 계정이 선택되어 있지 않은 경우");

            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우
            System.out.println("인터넷을 사용할 수 없는 경우");
            //mStatusText.setText("No network connection available.");
        } else {
            System.out.println("Google Calendar API 호출 시작");
            // Google Calendar API 호출
            new MainCalendar.MakeRequestTask(this, mCredential).execute();
        }
        return null;
    }


    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해
     * 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                MainCalendar.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }


    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);

                mCredential.setSelectedAccount(new Account(getPreferences(Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null), "com.example.schoolfinalproject"));

                System.out.println(accountName);

                //System.out.println(mCredential.getSelectedAccountName());

                getResultsFromApi();
            } else {

                System.out.println("사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.");
                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }


            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {

            System.out.println("GET_ACCOUNTS 권한을 가지고 있지 않다면");
            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    (Activity) this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }



    /*
     * 구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아올때 호출된다.
     */

    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        mID = 3;        //이벤트 가져오기
        getResultsFromApi();//
        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    System.out.println(" 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                            + "구글 플레이 서비스를 설치 후 다시 실행하세요.");
                    //mStatusText.setText( " 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                    //+ "구글 플레이 서비스를 설치 후 다시 실행하세요." );
                } else {

                    getResultsFromApi();
                }
                break;


            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;


            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    /*
     * Android 6.0 (API 23) 이상에서 런타임 권한 요청시 결과를 리턴받음
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  //requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 승인한 경우 호출된다.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 거부한 경우 호출된다.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * 캘린더 이름에 대응하는 캘린더 ID를 리턴
     */
    private String getCalendarID(String calendarTitle) {

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("유은우1" + e.toString());
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


    /*
     * 비동기적으로 Google Calendar API 호출
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private MainCalendar mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(MainCalendar activity, GoogleAccountCredential credential) {
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
            // mStatusText.setText("");
            mProgress.show();
            //mStatusText.setText("데이터 가져오는 중...");
            //mResultText.setText("");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */
        @Override
        protected String doInBackground(Void... params) {
            System.out.println("백그라운드 실행");
            try {
                if (mID == 1) {
                    return createCalendar();
                } else if (mID == 3) {
                    arrayEvent.clear();
                    Date time = new Date();
                    String time1 = format1.format(time) + "T00:00:00+09:00";
                    String time2 = format1.format(time) + "T23:59:59+09:00";
                    System.out.println("원본 값들"+time1);
                    String a = getEvent(time1, time2);
                    return a;
                } else if (mID == 4) {
                    arrayEvent.clear();
                    String a = getEvent(editStarttime, editEndtime);
                    return a;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        private String getEvent(String time1, String time2) throws IOException {

            String calendarID = getCalendarID("혈당 관리 일정");
            if (calendarID == null) {
                //캘린더 생성 작업 진행
                createCalendar();
                return "캘린더를 먼저 생성하세요.";
            }
            DateTime starttime = new DateTime(time1);
            DateTime endtime = new DateTime(time2);

            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(50)
                    .setTimeMin(starttime)
                    .setTimeMax(endtime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                if (start == null) {

                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                }
                EventInfo eventInfo = new EventInfo();
                eventInfo.setId(event.getId());
                eventInfo.setEventTitle(event.getSummary());
                eventInfo.setEventContent(event.getDescription());
                eventInfo.setEventStart(event.getStart().toString());
                eventInfo.setEventEnd(event.getEnd().toString());

                arrayEvent.add(eventInfo);

                eventStrings.add(String.format("%s \n (%s)", event.getSummary(), start));

            }

            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /*
         * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
         */
        private String createCalendar() throws IOException {

            String ids = getCalendarID("혈당 관리 일정");

            if (ids != null) {

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary("혈당 관리 일정");

            // 캘린더의 시간대 설정
            calendar.setTimeZone("Asia/Seoul");

            // 구글 캘린더에 새로 만든 캘린더를 추가
            Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // 추가한 캘린더의 ID를 가져옴.
            String calendarId = createdCalendar.getId();


            // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // 캘린더의 배경색을 파란색으로 표시  RGB
            calendarListEntry.setBackgroundColor("#0000ff");

            // 변경한 내용을 구글 캘린더에 반영
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // 새로 추가한 캘린더의 ID를 리턴
            return "캘린더가 생성되었습니다.";
        }


        @Override
        protected void onPostExecute(String output) {

            mProgress.hide();
            //mStatusText.setText(output);
            System.out.println(output);

            if (mID == 3)
                System.out.println(TextUtils.join("\n\n", eventStrings)); //mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainCalendar.REQUEST_AUTHORIZATION);
                } else {
                    System.out.println("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                    //mStatusText.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                System.out.println("요청 취소됨.");
                //mStatusText.setText("요청 취소됨.");
            }
        }
    }

    //사용법 블로그 참고 추가4 끝

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    public String timeParsing(String start) {
        System.out.println(start);
        String startdate = start.substring(13, 23);
        String starttimes = start.substring(24, 29);

        return startdate + " " + starttimes;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public int size(){
        mID=3;
        getResultsFromApi();//
        return arrayEvent.size();
    }

}
