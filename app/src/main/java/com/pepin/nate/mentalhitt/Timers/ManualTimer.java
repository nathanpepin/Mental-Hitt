package com.pepin.nate.mentalhitt.Timers;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.Data.Constants;
import com.pepin.nate.mentalhitt.Data.DataHelper;
import com.pepin.nate.mentalhitt.Data.FormatTime;
import com.pepin.nate.mentalhitt.Data.GenerateWorkout;
import com.pepin.nate.mentalhitt.MainActivity;
import com.pepin.nate.mentalhitt.R;
import com.pepin.nate.mentalhitt.Data.WorkoutData;

public class ManualTimer extends AppCompatActivity {

    private TextView tv_totalTime, state, stateTime;
    public EditText focusTime, restTime, reps, coolDown;
    public Button button_start, button_stop, button_reset;

    private WorkoutData workoutData;

    public String workoutSharedPreference;

    public Boolean isTheTimerStarted = false;

    public String timerType;

    TimerService mService;

    boolean mBound = false;

    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_timer);

        workoutSharedPreference = "DEFAULT_TIMER";
        timerType = "MANUAL_TIMER";

        init();
    }

    ////////////////////////////////////
    //Init: Initialize startup
    ////////////////////////////////////
    public void init() {

        initViews();

        //Data object init
        workoutData = new WorkoutData();
        getSharedPreferences();
        updateWorkOutText();
    }

    public void initViews() {
        //EditText init
        focusTime = findViewById(R.id.et_focusTime);
        restTime = findViewById(R.id.et_restTime);
        reps = findViewById(R.id.et_reps);
        coolDown = findViewById(R.id.et_coolDown);

        //TextView init
        tv_totalTime = findViewById(R.id.tv_workOutTime);
        state = findViewById(R.id.state);
        stateTime = findViewById(R.id.stateTime);

        //Button
        button_start = findViewById(R.id.b_startWorkout);
        button_stop = findViewById(R.id.b_pauseWorkout);
        button_reset = findViewById(R.id.b_resetWorkout);

        //Listeners
        ////////////////////////////////////
        //focusTime text listener
        ////////////////////////////////////
        focusTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nullCheckText = focusTime.getText().toString();
                if (!nullCheckText.equals("")) {
                    workoutData.focusTime = Integer.parseInt(focusTime.getText().toString());
                } else {
                    workoutData.focusTime = 0;
                }
                updateWorkOutTime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ////////////////////////////////////
        //restTime text listener
        ////////////////////////////////////
        restTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nullCheckText = restTime.getText().toString();
                if (!nullCheckText.equals("")) {
                    workoutData.restTime = Integer.parseInt(restTime.getText().toString());
                } else {
                    workoutData.restTime = 0;
                }
                updateWorkOutTime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ////////////////////////////////////
        //reps text listener
        ////////////////////////////////////
        reps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nullCheckText = reps.getText().toString();
                if (!nullCheckText.equals("")) {
                    workoutData.reps = Integer.parseInt(reps.getText().toString());
                } else {
                    workoutData.reps = 0;
                }
                updateWorkOutTime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ////////////////////////////////////
        //coolDown text listener
        ////////////////////////////////////
        coolDown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nullCheckText = coolDown.getText().toString();
                if (!nullCheckText.equals("")) {
                    workoutData.coolDown = Integer.parseInt(coolDown.getText().toString());
                } else {
                    workoutData.coolDown = 0;
                }
                updateWorkOutTime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void start(View view) {

        ////////////////////////////////////////////////////////////////////////
        //Make the views uneditable
        ////////////////////////////////////////////////////////////////////////
        focusTime.setEnabled(false);
        restTime.setEnabled(false);
        reps.setEnabled(false);
        coolDown.setEnabled(false);
        view.setEnabled(false);

        button_stop.setEnabled(true);
        button_reset.setEnabled(true);

        ////////////////////////////////////////////////////////////////////////
        //Reset functionality: resets countdown values if false
        ////////////////////////////////////////////////////////////////////////
        if (!workoutData.isCounting) {
            //Sync up the two timers
            updateWorkOutTime();
            workoutData.updateTotalTime();
            //Sync up the initial values that way there is a way to know how long the initial focus and rest time are
            workoutData.setIValues();
            //prevents condition from running again
            workoutData.isCounting = true;
            updateSharedPreferences();
        }

        /*
        From here the timer will be started in one of two ways:
            -If the service is already going then resume the timer
            -If not, start the service

            Do common
         */
        isTheTimerStarted = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("UPDATE_UI"));

        //Set wakeLock
        try {
            wakeLock.release();
            Log.d("Debug", "wakeLock: released");
        } catch (Exception e) {
            Log.d("Debug", "wakeLock: no wakeLock found so nothing to release");
        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        //Sets the wakelock ten seconds extra to account for the count in and the finish timer execute
        wakeLock.acquire((workoutData.totalTime * 1000) + (workoutData.coolDown * 1000) + 10000);


        ///////////////////////////////////////////////////////////////////////////////////////////////////
        //If the service is running
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        if (mBound) {
            mService.timer.resumeTimer();
            return;
        }

        ////////////////////////////////////////////////////////////////////////
        //If the service isn't running
        ////////////////////////////////////////////////////////////////////////
        Intent serviceIntent = new Intent(this, TimerService.class);

        serviceIntent.putExtra("focus", workoutData.focusTime);
        serviceIntent.putExtra("rest", workoutData.restTime);
        serviceIntent.putExtra("reps", workoutData.reps);
        serviceIntent.putExtra("coolDown", workoutData.coolDown);
        serviceIntent.putExtra("totalTime", workoutData.totalTime);
        serviceIntent.putExtra("timeType", timerType);

        startService(serviceIntent);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void pause(View view) {

        if (mBound) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

            //Shouldn't be needed, but error checking
            if (mService.timer.isRunning()) {
                mService.timer.pauseTimer();
            }
        }

        //Enable views
        button_start.setEnabled(true);
        view.setEnabled(false);

        isTheTimerStarted = false;
    }

    public void reset(View view) {

        stopTimerService();

        //Enable views
        button_start.setEnabled(true);
        button_stop.setEnabled(false);
        focusTime.setEnabled(true);
        restTime.setEnabled(true);
        reps.setEnabled(true);
        coolDown.setEnabled(true);

        //Reset data
        workoutData.isCounting = false;
        workoutData.focusTime = Integer.parseInt(focusTime.getText().toString());
        workoutData.restTime = Integer.parseInt(restTime.getText().toString());
        workoutData.reps = Integer.parseInt(reps.getText().toString());
        if (workoutData.coolDown != 0)
            workoutData.coolDown = Integer.parseInt(coolDown.getText().toString());
        workoutData.updateTotalTime();
        tv_totalTime.setText(FormatTime.convertSeconds(workoutData.totalTime));
        state.setText("");
        stateTime.setText("");
    }

    ////////////////////////////////////////////////////////////////////////
    //Updates the tv_totalTime view when manually setting the inputs
    ////////////////////////////////////////////////////////////////////////
    private void updateWorkOutTime() {
        //Don't update any of the values if they are 0 as they aren't meaningful
        if (workoutData.focusTime == 0) return;
        if (workoutData.restTime == 0) return;
        if (workoutData.reps == 0) return;

        //Sets the TextView
        tv_totalTime.setText(
                FormatTime.convertSeconds(workoutData.getTotalTime())
        );
    }

    ////////////////////////////////////////////////////////////////////////
    //Updates the tv_totalTime view when manually setting the inputs
    ////////////////////////////////////////////////////////////////////////
    public void getSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(workoutSharedPreference, 0);
        workoutData.focusTime = sharedPref.getInt(Constants.FOCUS, Constants.DEFAULT_FOCUS_TIME);
        workoutData.restTime = sharedPref.getInt(Constants.REST, Constants.DEFAULT_REST_TIME);
        workoutData.reps = sharedPref.getInt(Constants.REPS, Constants.DEFAULT_REPS);
        workoutData.coolDown = sharedPref.getInt(Constants.COOL_DOWN, Constants.DEFAULT_COOL_DOWN);
        workoutData.totalTime = sharedPref.getInt(Constants.TOTAL_TIME, Constants.DEFAULT_TOTAL_TIME);
    }

    public void updateSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(workoutSharedPreference, 0);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putInt(Constants.FOCUS, workoutData.focusTime);
        spEditor.putInt(Constants.REST, workoutData.restTime);
        spEditor.putInt(Constants.REPS, workoutData.reps);
        spEditor.putInt(Constants.COOL_DOWN, workoutData.coolDown);
        spEditor.putInt(Constants.TOTAL_TIME, workoutData.totalTime);
        spEditor.apply();
    }

    private void updateWorkOutText() {
        if (!isTheTimerStarted) {
            focusTime.setText(String.valueOf(workoutData.focusTime));
            restTime.setText(String.valueOf(workoutData.restTime));
            reps.setText(String.valueOf(workoutData.reps));
            coolDown.setText(String.valueOf(workoutData.coolDown));
            updateWorkOutTime();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Binding
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            state.setText(mService.currentInfoText());
            tv_totalTime.setText(
                    FormatTime.convertSeconds(mService.time)
            );

            //When the timer is finished
            if (mService.time == 0) {
                button_stop.setEnabled(false);
                onTimerFinish();
            }
        }
    };

    public void onTimerFinish() {
        Log.d("Debug", "ManualTimer->onTimerFinish");
        DataHelper dataHelper = new DataHelper(this);
        dataHelper.storeLog();
        mService.killNotificationBar();
        stopTimerService();
        isTheTimerStarted = false;
    }

    //Defines callbacks for service binding, passed to bindService()
    //
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("Debug", "ManualTimer->onServiceConnected()");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        //Only gets called when shits goes wrong
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("Debug", "ManualTimer->onServiceDisconnected");
            mBound = false;
        }
    };

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            Log.d("Debug", "isMyServiceRunning: Null Pointer exception");
            Toast.makeText(this, "Unexpected Error, restarted", Toast.LENGTH_SHORT).show();
            return false;
        } catch (Exception e) {
            Log.d("Debug", "isMyServiceRunning: Unknown exception");
            Toast.makeText(this, "Unexpected Error, restarted", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void stopTimerService() {
        Log.d("Debug", "ManualTimer->stopTimerService");

        if (mBound) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            mService.stopSelf();
            unbindService(mConnection);
            mBound = false;
            isTheTimerStarted = false;
        }

        try {
            wakeLock.release();
            Log.d("Debug", "wakeLock: wakeLock released");
        } catch (Exception e) {
            Log.d("Debug", "wakeLock: no wakeLock found so nothing to release");
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Life Cycle
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        Log.d("Debug", "ManualTimer->onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.d("Debug", "ManualTimer->onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("Debug", "ManualTimer->onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Debug", "ManualTimer->onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Debug", "ManualTimer->onDestroy");
        stopTimerService();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d("Debug", "ManualTimer->onBackPressed");
        if (isMyServiceRunning(TimerService.class)) {
            Toast.makeText(this, "Workout In Session", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}

