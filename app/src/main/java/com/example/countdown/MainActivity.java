package com.example.countdown;

import android.media.MediaPlayer;
import android.os.Bundle;
import java.lang.String;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewCounter;
    private TextView mTextViewCountDown;

    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonSetTimer;

    private NumberPicker np;

    private CountDownTimer mCountDownTimer;

    private MediaPlayer mp;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis;
    private long mSelectedStartTime;

    private static int counter = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //prevent device from going into sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        np = findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(999);
        np.setWrapSelectorWheel(true);

        mTextViewCounter = findViewById(R.id.counter_text);
        mTextViewCountDown = findViewById(R.id.countdown_text);
        mButtonStartPause = findViewById(R.id.countdown_buttonStartPause);
        mButtonReset = findViewById(R.id.countdown_buttonReset);
        mButtonSetTimer = findViewById(R.id.countdown_buttonSetTimer);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                np.setVisibility(View.INVISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                if(mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                    mButtonSetTimer.setVisibility(View.INVISIBLE);
                }
            }
        });

        mButtonSetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                np.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.INVISIBLE);
                mButtonSetTimer.setVisibility(View.INVISIBLE);
                resetTimer();
            }
        });

        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                // get time in milliseconds
                long timeHelper = newVal * 1000;
                mSelectedStartTime = timeHelper;
                mTextViewCountDown.setText(String.valueOf(newVal));
                mTimeLeftInMillis = timeHelper;
            }
        };
        np.setOnValueChangedListener(onValueChangeListener);


        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                playSound();
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);

                startPauseTimer();

            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void startPauseTimer() {
        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonStartPause.setVisibility(View.VISIBLE);
       mCountDownTimer = new CountDownTimer(15000, 1000) {
           @Override
           public void onTick(long millisUntilFinished) {
               mTimeLeftInMillis = millisUntilFinished;
               updateCountDownText();
           }

           @Override
           public void onFinish(){
               playSound();
               mTimeLeftInMillis = mSelectedStartTime;
               updateCountDownText();
               mButtonReset.setVisibility(View.INVISIBLE);
               mButtonStartPause.setVisibility(View.VISIBLE);
               mTextViewCounter.setText(String.valueOf(++counter));
               startTimer();
           }
        }.start();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
        mButtonSetTimer.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = mSelectedStartTime;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        counter = 1;
        mTextViewCounter.setText(String.valueOf(counter));
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void playSound() {
        if(mp != null) mp.reset();
        try {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
            mp.start();
        } catch(Exception e) {
            if(mp != null) mp.release();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
