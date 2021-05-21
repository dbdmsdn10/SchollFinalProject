package com.example.schoolfinalproject;

import android.accounts.Account;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


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

import android.Manifest;
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

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//사용법 블로그 참고 추가1 끝
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditCalendar extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    EditText calendar_title;
    EditText calendar_content;

    TextView startDateSetText;
    TextView endDateSetText;
    TextView startTimeSetText;
    TextView endTimeSetText;

    Button commit;

    String title;
    String content;
    String startDate;
    String endDate;

    //년월일 시분초
    int Syear;
    int Smonth;
    int Sday;
    int Shour;
    int Sminute;
    int Ssecond;

    String TotelstartDateTime;
    String TotelendDateTime;
    String id;

    //사용법 블로그 참고 추가2 시작
    /**
     * Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체
     */

    private com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    private int mID = 0;


    GoogleAccountCredential mCredential;

    ProgressDialog mProgress;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    Intent intent;

    //사용법 블로그 참고 추가2 끝


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //일정 제목과 내용을 받기 위해 사용
        calendar_title = (EditText) findViewById(R.id.calendarTitle);
        calendar_content = (EditText) findViewById(R.id.calendarContent);

        //날짜 설정을 받기 위해 사용
        startDateSetText = (TextView) findViewById(R.id.startDateSettingText);
        endDateSetText = (TextView) findViewById(R.id.endDateSettingText);

        //시간 설정을 받기 위해 사용
        startTimeSetText = (TextView) findViewById(R.id.startTimeSettingText);
        endTimeSetText = (TextView) findViewById(R.id.endTimeSettingText);

        //버튼 설정
        commit = (Button) findViewById(R.id.commit);

        intent = getIntent();
        id = intent.getStringExtra("id");
        calendar_title.setText(intent.getStringExtra("제목"));
        calendar_content.setText(intent.getStringExtra("내용"));
        String first[] = intent.getStringExtra("시작").split(" ");
        String second[] = intent.getStringExtra("종료").split(" ");

        System.out.println("시작= " + intent.getStringExtra("시작"));
        startDateSetText.setText(first[0]);
        startTimeSetText.setText(first[1]);
        endDateSetText.setText(second[0]);
        endTimeSetText.setText(second[1]);


        //현재 시간 값 받아오기
        java.util.Calendar calander;

        calander = java.util.Calendar.getInstance();
        SimpleDateFormat simpledateformat, simpletimeformat;

        simpledateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String dateformat = simpledateformat.format(calander.getTime());

        simpletimeformat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        String timeformat = simpletimeformat.format(calander.getTime());

        String Sdate[] = dateformat.split("-");
        for (int i = 0; i < Sdate.length; i++) {
            if (i == 0) {
                Syear = Integer.parseInt(Sdate[0]);
            } else if (i == 1) {
                Smonth = Integer.parseInt(Sdate[1]);
            } else if (i == 2) {
                Sday = Integer.parseInt(Sdate[2]);
            }
        }

        System.out.println("시간 보기" + timeformat);

        String Stime[] = timeformat.split(":");
        for (int j = 0; j < Stime.length; j++) {
            if (j == 0) {
                Shour = Integer.parseInt(Stime[0]);
            } else if (j == 1) {
                Sminute = Integer.parseInt(Stime[1]);
            } else if (j == 2) {
                Ssecond = Integer.parseInt(Stime[2]);
            }
        }

        //startDateSetText.setText(Syear+"-"+ Smonth+"-"+Sday);
        //endDateSetText.setText(Syear+"-"+ Smonth+"-"+Sday);

        startDateSetText.setOnClickListener(click);
        endDateSetText.setOnClickListener(click);

        startTimeSetText.setOnClickListener(click);
        endTimeSetText.setOnClickListener(click);

        commit.setOnClickListener(click);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");


        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

    }

    public View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.calendarTitle:

                    break;
                case R.id.calendarContent:

                    break;
                case R.id.startDateSettingText://dateSetting:

                    DatePickerDialog startDateDialog = new DatePickerDialog(EditCalendar.this, callbackStart_date, Syear, Smonth - 1, Sday);
                    startDateDialog.show();

                    break;
                case R.id.endDateSettingText://dateSetting:
                    //OnClickHandler(dateSetText);
                    //AddCalendar.this.InitializeView();
                    //AddCalendar.this.InitializeListener();

                    DatePickerDialog endDateDialog = new DatePickerDialog(EditCalendar.this, callbackEnd_date, Syear, Smonth - 1, Sday);
                    endDateDialog.show();

                    break;
                case R.id.startTimeSettingText:
                    TimePickerDialog startTimeDialog = new TimePickerDialog(EditCalendar.this, 2, callbackStart_time, Shour, Sminute, false);
                    startTimeDialog.show();

                    break;
                case R.id.endTimeSettingText:
                    TimePickerDialog endTimeDialog = new TimePickerDialog(EditCalendar.this, 2, callbackEnd_time, Shour, Sminute, false);
                    endTimeDialog.show();

                    break;
                case R.id.commit:
                    commit.setEnabled(false);
                    title = calendar_title.getText().toString();
                    content = calendar_content.getText().toString();

                    if (startDateSetText.getText().toString().equals("시작 날짜")
                            || endDateSetText.getText().toString().equals("종료 날짜")) {
                        startDateSetText.setText(String.format("%d-%02d-%02d", Syear, Smonth, Sday));
                        endDateSetText.setText(String.format("%d-%02d-%02d", Syear, Smonth, Sday));
                        startDate = startDateSetText.getText().toString();
                        endDate = endDateSetText.getText().toString();
                    } else if (swapDate()) {
                        endDate = startDateSetText.getText().toString();
                        startDate = endDateSetText.getText().toString();
                    } else {
                        startDate = startDateSetText.getText().toString();
                        endDate = endDateSetText.getText().toString();
                    }

                    String startEditTime;
                    //String startEdit[] = startEditTime.split(":");

                    String endEditTime;
                    //String endEdit[] = endEditTime.split(";");

                    if (startTimeSetText.getText().toString().equals("시작 시간 설정")
                            || endTimeSetText.getText().toString().equals("종료 시간 설정")) {
                        startTimeSetText.setText(String.format("%02d:%02d", Shour, Sminute));
                        endTimeSetText.setText(String.format("%02d:%02d", Shour, Sminute));
                        startEditTime = startTimeSetText.getText().toString();
                        endEditTime = endTimeSetText.getText().toString();
                    } else if (swapTime()) {
                        endEditTime = startTimeSetText.getText().toString();
                        startEditTime = endTimeSetText.getText().toString();
                    } else {
                        startEditTime = startTimeSetText.getText().toString();
                        endEditTime = endTimeSetText.getText().toString();
                    }

                    TotelstartDateTime = startDate.concat('T' + startEditTime + ":00+09:00");
                    TotelendDateTime = endDate.concat('T' + endEditTime + ":00+09:00");

                    //mStatusText.setText("");
                    mID = 2;        //이벤트 생성
                    getResultsFromApi();
                    commit.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "추가되었습니다", Toast.LENGTH_SHORT).show();

                    /*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2000);
*/
                    break;
            }
        }
    };


    private DatePickerDialog.OnDateSetListener callbackStart_date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDateSetText.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

    private DatePickerDialog.OnDateSetListener callbackEnd_date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endDateSetText.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
        }
    };

    private TimePickerDialog.OnTimeSetListener callbackStart_time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            startTimeSetText.setText(String.format("%02d:%02d", hour, minute));
            /*
            if(hour < 13)
            {
                startTimeSetText.setText("오전 "+hour+ ":" + minute);
            }
            else
            {
                startTimeSetText.setText("오후 "+hour%12+ ":" + minute);
            }
             */
        }
    };

    private TimePickerDialog.OnTimeSetListener callbackEnd_time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            endTimeSetText.setText(String.format("%02d:%02d", hour, minute));
            /*
            if(hour < 13)
            {
                endTimeSetText.setText("오전 "+hour+ ":" + minute);
            }
            else
            {
                endTimeSetText.setText("오후 "+hour%12+ ":" + minute);
            }
             */
        }
    };

    boolean swapDate() {
        String divide1[] = startDateSetText.getText().toString().split("-");
        String divide2[] = endDateSetText.getText().toString().split("-");
        int[] startDate = new int[3];
        int[] endDate = new int[3];

        for (int i = 0; i < divide1.length; i++) {
            if (i == 0) {
                startDate[0] = Integer.parseInt(divide1[0]);
            } else if (i == 1) {
                startDate[1] = Integer.parseInt(divide1[1]);
            } else if (i == 2) {
                startDate[2] = Integer.parseInt(divide1[2]);
            }
        }

        for (int i = 0; i < divide2.length; i++) {
            if (i == 0) {
                endDate[0] = Integer.parseInt(divide2[0]);
            } else if (i == 1) {
                endDate[1] = Integer.parseInt(divide2[1]);
            } else if (i == 2) {
                endDate[2] = Integer.parseInt(divide2[2]);
            }
        }

        if (startDate[0] > endDate[0])//시작 년도 보다 종료 년도가 더 작은 경우
        {
            return true;
        } else if (startDate[1] > endDate[1])//시작 월 보다 종료 월이 더 작은 경우
        {
            return true;
        } else if (startDate[2] > endDate[2]) //시작 일 보다 종료 일이 더 작은 경우
        {
            return true;
        }

        return false;
    }


    boolean swapTime() {
        String divide1[] = startTimeSetText.getText().toString().split(":");
        String divide2[] = endTimeSetText.getText().toString().split(":");
        int[] startTime = new int[2];
        int[] endTime = new int[2];

        for (int i = 0; i < divide1.length; i++) {
            if (i == 0) {
                startTime[0] = Integer.parseInt(divide1[0]);
            } else if (i == 1) {
                startTime[1] = Integer.parseInt(divide1[1]);
            }

        }

        for (int i = 0; i < divide2.length; i++) {
            if (i == 0) {
                endTime[0] = Integer.parseInt(divide2[0]);
            } else if (i == 1) {
                endTime[1] = Integer.parseInt(divide2[1]);
            }
        }

        if (startTime[0] > endTime[0])//시작 시간 보다 종료 시간이 더 작은 경우
        {
            return true;
        } else if (startTime[1] > endTime[1])//시작 분 보다 종료 분이 더 작은 경우
        {
            return true;
        }

        return false;
    }
/*
    public void InitializeListener()
    {
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                dateSetText.setText(year + "년" + monthOfYear + "월" + dayOfMonth + "일");
            }
        };
    }

    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, Syear, Smonth-1, Sday);

        dialog.show();
    }*/


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

        System.out.println("getResultsFromApi 실행");

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
            new MakeRequestTask(this, mCredential).execute();
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
                EditCalendar.this,
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

        System.out.println("GET_ACCOUNTS 권한을 가지고 있다면");
        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            System.out.println("SharedPreferences에서 저장된 Google 계정 이름을 가져온다.");
            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                System.out.println("선택된 구글 계정 이름으로 설정한다.");

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
                System.out.println("오류" + e.toString());
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
        private EditCalendar mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(EditCalendar activity, GoogleAccountCredential credential) {

            System.out.println("MakeRequestTask 실행");
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
            System.out.println(("데이터 가져오는 중..."));
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            mProgress.show();
        }

        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */
        @Override
        protected String doInBackground(Void... params) {
            try {

                if (mID == 1) {

                    return createCalendar();

                } else if (mID == 2) {

                    return addEvent();
                }

            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
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
                            AddCalendar.REQUEST_AUTHORIZATION);
                } else {
                    System.out.println("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                    //mStatusText.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                System.out.println("요청 취소됨.");
                //mStatusText.setText("요청 취소됨.");
            }
        }


        private String addEvent() {


            String calendarID = getCalendarID("혈당 관리 일정");

            if (calendarID == null) {

                System.out.println("캘린더를 먼저 생성하세요.");
                return "캘린더를 먼저 생성하세요.";

            }

            //다이얼로그를 생성하여 일정 내용을 추가할 수 있는 코드를 작성(예정)
            //String calendarTitle;
            //String calendarContent;
            /*
            Intent intent = new Intent(AddCalendar.this, AddCalendar.class);
            startActivity(intent);
            */
            Event event = new Event()
                    .setSummary(title)//일정 제목
                    .setLocation("서울시")
                    .setDescription(content);//일정 메모

            java.util.Calendar calander;

            calander = java.util.Calendar.getInstance();
            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
            String datetime = simpledateformat.format(calander.getTime());


            //일정 시간 범위 지정 다이얼로그 추가 예정

            System.out.println("datetime: " + datetime);
            System.out.println("TotelstartDateTime: " + TotelstartDateTime);
            //DateTime startDateTime = new DateTime(datetime);
            DateTime startDateTime = new DateTime(TotelstartDateTime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            //Log.d( "@@@", datetime );

            System.out.println("TotelendDateTime: " + TotelendDateTime);
            DateTime endDateTime = new DateTime(TotelendDateTime);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setEnd(end);


            //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
            //event.setRecurrence(Arrays.asList(recurrence));


            try {
                event = mService.events().update(calendarID, id, event).execute();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception : " + e.toString());
            }
            System.out.printf("Event created: %s\n", event.getHtmlLink());
            Log.e("Event", "created : " + event.getHtmlLink());
            String eventStrings = "created : " + event.getHtmlLink();
            return eventStrings;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
