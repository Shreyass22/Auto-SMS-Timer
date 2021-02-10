package com.joaquimley.smsparsing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
//implements View.OnClickListener

    TextView textview;
    private static final long START_TIME_IN_MILLIS = 30000;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private static final String TAG = "MainActivity";
    private static final String PREF_USER_MOBILE_PHONE = "9702061635";
    private static final int SMS_PERMISSION_CODE = 0;

    private EditText mNumberEditText;
    private String mUserMobilePhone ;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //my code
        textview = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);


        startTimer();
//        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mTimerRunning) {
//                    pauseTimer();
//                } else {
//                    startTimer();
//                }
//            }
//        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();

        // initialize timer
//        long duration = TimeUnit.MINUTES.toMillis(1);
//
//        // initialize counter
//        new CountDownTimer(duration, 1000) {
//            @Override
//            public void onTick(long l) {
//                // When tick
//                // convert miliseconds to minute and seconds
//                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
//                        , TimeUnit.MILLISECONDS.toMinutes(1)
//                        ,TimeUnit.MILLISECONDS.toSeconds(1) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));
//
//                // Set converted string on textview
//                textview.setText(sDuration);
//            }
//
//            @Override
//            public void onFinish() {
//                // When Finish
//                // Hide text
//                textview.setText("SMS SEND");
//                if (!hasValidPreConditions()) return;
//                checkAndUpdateUserPrefNumber();
//
//                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION
//                            + " This SMS is Automatically send, Hello toast");
//                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
//            }
//        }.start();

        // my code end


        if (!hasReadSmsPermission()) {
            showRequestPermissionsInfoAlertDialog();
        }

        initViews();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserMobilePhone = mSharedPreferences.getString(PREF_USER_MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(mUserMobilePhone)) {
            mNumberEditText.setText(mUserMobilePhone);
        }
    }


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                mButtonReset.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                textview.setText("SMS SEND");
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();

                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION
                            + " This SMS is Automatically send, Hello toast");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
            }
        }.start();
        mTimerRunning = true;
        mButtonStartPause.setVisibility(View.INVISIBLE);
        mButtonReset.setVisibility(View.INVISIBLE);
    }
//    private void pauseTimer() {
//        mCountDownTimer.cancel();
//        mTimerRunning = false;
//        mButtonStartPause.setText("Start");
//        mButtonReset.setVisibility(View.VISIBLE);
//    }
    private void resetTimer() {
        mCountDownTimer.cancel();
        //mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.VISIBLE);
        mButtonStartPause.setVisibility(View.INVISIBLE);
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textview.setText(timeLeftFormatted);
    }

    private void initViews() {
        mNumberEditText = (EditText) findViewById(R.id.et_number);
//        findViewById(R.id.btn_normal_sms).setOnClickListener(this);
//        findViewById(R.id.btn_conditional_sms).setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_conditional_sms:
//                if (!hasValidPreConditions()) return;
//                checkAndUpdateUserPrefNumber();
//
//                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + " This SMS is conditional, Hello toast");
//                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.btn_normal_sms:
//                if (!hasValidPreConditions()) return;
//                checkAndUpdateUserPrefNumber();
//
//                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), "The broadcast should not show a toast for this");
//                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

    /**
     * Checks if stored SharedPreferences value needs updating and updates \o/
     */
    private void checkAndUpdateUserPrefNumber() {
        if (TextUtils.isEmpty(mUserMobilePhone) && !mUserMobilePhone.equals(mNumberEditText.getText().toString())) {
            mSharedPreferences
                    .edit()
                    .putString(PREF_USER_MOBILE_PHONE, mNumberEditText.getText().toString())
                    .apply();
        }
    }


    /**
     * Validates if the app has readSmsPermissions and the mobile phone is valid
     *
     * @return boolean validation value
     */
    private boolean hasValidPreConditions() {
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
            return false;
        }

        if (!SmsHelper.isValidPhoneNumber(mNumberEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.error_invalid_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission();
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }
}