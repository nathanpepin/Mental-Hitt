package com.pepin.nate.mentalhitt.Timers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
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

import com.ankushgrover.hourglass.Hourglass;
import com.pepin.nate.mentalhitt.Data.Constants;
import com.pepin.nate.mentalhitt.Data.DataHelper;
import com.pepin.nate.mentalhitt.Data.FormatTime;
import com.pepin.nate.mentalhitt.MainActivity;
import com.pepin.nate.mentalhitt.Profile_Main;
import com.pepin.nate.mentalhitt.R;

import java.util.ArrayList;

public class Focus_max extends AppCompatActivity {

    private EditText et_maxTime, et_numberOfClicksHeard;
    private Button b_start, b_reset, b_confirm;

    private Boolean isTimerRunning;

    //Hold time data
    private ArrayList<Integer> clickData;

    private int maxTimerLimit = 0;

    public DataHelper dataHelper;

    final String timerType = "Focus Max";

    public void setMaxTimerLimit(int maxTimerLimit) {
        if (maxTimerLimit > 0) {
            this.maxTimerLimit = maxTimerLimit;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_max);

        init();
    }

    private void init() {
        isTimerRunning = false;
        dataHelper = new DataHelper(this);

        init_Views();
        serviceHandler();
    }

    private void init_Views() {
        //Find views
        TextView tv_cFocusMax;
        tv_cFocusMax = findViewById(R.id.tv_cFocusMax);
        String t_cFocusMax = "Focus Max: " + FormatTime.convertSeconds(dataHelper.focusMax);
        tv_cFocusMax.setText(t_cFocusMax);

        et_maxTime = findViewById(R.id.et_maxTime);
        et_numberOfClicksHeard = findViewById(R.id.et_clicks);

        b_start = findViewById(R.id.b_start);
        b_reset = findViewById(R.id.b_reset);
        b_confirm = findViewById(R.id.b_confirm);

        //Default State
        b_reset.setEnabled(false);
        b_confirm.setEnabled(false);
        et_numberOfClicksHeard.setEnabled(false);
        et_maxTime.setHint(FormatTime.convertSeconds(dataHelper.totalTime) + " by default");
        et_maxTime.setEnabled(false);

        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        b_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        b_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nullCheckText = et_numberOfClicksHeard.getText().toString();
                if (!nullCheckText.equals("")) {
                    confirm(Integer.parseInt(nullCheckText));
                }
            }
        });

        et_maxTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String nullCheckText = et_maxTime.getText().toString();
                if (!nullCheckText.equals("")) {
                    setMaxTimerLimit(Integer.parseInt(nullCheckText));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void serviceHandler() {
        if (isMyServiceRunning(FocusMaxService.class)) {
            //UI
            String t_start = "Stop";
            b_start.setText(t_start);
            b_reset.setEnabled(false);
            et_numberOfClicksHeard.setEnabled(false);
            b_confirm.setEnabled(false);

            isTimerRunning = true;

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("UPDATE_UI"));
            Intent serviceIntent = new Intent(this, FocusMaxService.class);
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

            Toast.makeText(this, "Continuing Your Focus Max Test", Toast.LENGTH_SHORT).show();
        } else {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("UPDATE_UI"));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    PowerManager.WakeLock wakeLock;

    private void start() {
        String t_stop = "Stop";

        //If the timer is running, then the button will use stop method
        if (isTimerRunning) {
            //Stop the timer
            stop();
        } else {

            try {
                wakeLock.release();
            } catch (Exception e) {
                Log.d("Debug", "wakeLock: no wakeLock found so nothing to release");
            }
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
            //Sets the wakelock ten seconds extra to account for the count in and the finish timer execute
            wakeLock.acquire((dataHelper.totalTime * 1000) + 10000);

            Intent serviceIntent = new Intent(this, FocusMaxService.class);
            serviceIntent.putExtra("totalTime", dataHelper.totalTime);
            serviceIntent.putExtra("timeType", timerType);

            startService(serviceIntent);

            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

            isTimerRunning = true;

            //Sets button text to "Stop" since we are going from start to stop
            b_start.setText(t_stop);
            //Disable reset, user will have to use stop to get to it
            b_reset.setEnabled(false);
            et_maxTime.setEnabled(false);
            //Shouldn't be able to do confirmation
            et_numberOfClicksHeard.setEnabled(false);
            b_confirm.setEnabled(false);

            //The timer is now running
            isTimerRunning = true;
        }
    }

    /*/////////////////////////////////////////////////////
    stop(): Stops and resets the timer
    /////////////////////////////////////////////////////*/
    private void stop() {

        //Stop the service
        Intent serviceIntent = new Intent(this, FocusMaxService.class);
        stopService(serviceIntent);

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        //Update the click data
        clickData = mService.clickData;

        isTimerRunning = false;

        //Now that the timer is stopped and we know there is something to reset, reset the functionality
        b_reset.setEnabled(true);

        //Should now be able to enter the clicks heard
        et_numberOfClicksHeard.setEnabled(true);
        et_numberOfClicksHeard.requestFocus();
        b_confirm.setEnabled(true);


        //Sets button text to "Start" since we are going from stop to start
        String t_start = "Start";
        b_start.setText(t_start);
    }

    /*/////////////////////////////////////////////////////
    reset(): Resets the timer functionality
    /////////////////////////////////////////////////////*/
    public void reset() {

        //Since the timer is being reset, having reset would be redundant
        b_reset.setEnabled(false);
        //Need to be able to start the timer again
        b_start.setEnabled(true);
        //Make max time editable again
        et_maxTime.setEnabled(true);
        //Shouldn't be able to set confirm
        et_numberOfClicksHeard.setEnabled(false);
        b_confirm.setEnabled(false);
        //Reset array
        clickData.clear();
    }


    /*/////////////////////////////////////////////////////
    confirm(clicksHeard): Validates the number of clicks heard and gets the next workout
    /////////////////////////////////////////////////////*/
    public void confirm(int clicksHeard) {

        //If the number of clicks heard is possible
        if (clicksHeard > 0 && clicksHeard < clickData.size() + 1) {

            //If it is possible then get the time associated with the click
            //Minus 1 because the index is 0 based
            int clicks = clickData.get(clicksHeard - 1);

            //If a PR is set then go to the next level, otherwise restart the progression
            //Note: currently forced, but will not be in final
            if (clicks > dataHelper.focusMax) {
                //Ensures that the new focusMax will be set
                dataHelper.XP = 100;
                dataHelper.nextFocusMax = clicks;
                dataHelper.getNextWorkout();
            } else {
                Toast.makeText(this, "PR not achieved", Toast.LENGTH_SHORT).show();
                dataHelper.restartNextWorkout();
            }

            //Go back to the profile activity
            Intent intent = new Intent(this, Profile_Main.class);
            startActivity(intent);

            //If the number of clicks heard isn't possible
        } else {
            //If it isn't then find out what went wrong
            if (clicksHeard < 1) {
                Toast.makeText(this, "Cannot enter 0 or negative clicks", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "There were only " + clickData.size() + " clicks", Toast.LENGTH_SHORT).show();
            }
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Binding
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    FocusMaxService mService;
    boolean mBound = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //When the timer is finished
            if (dataHelper.totalTime == mService.timeCount) {
                stop();
                b_start.setEnabled(false);
                onTimerFinish();
            }
        }
    };

    public void onTimerFinish() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);*/

        //When the timer is finished stop the service
        //This should only call when the user is on the activity
        mService.killNotificationBar();
        Intent serviceIntent = new Intent(this, FocusMaxService.class);
        stopService(serviceIntent);

        isTimerRunning = false;
    }


    //Defines callbacks for service binding, passed to bindService()
    //
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            FocusMaxService.LocalBinder binder = (FocusMaxService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Life Cycle
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();

        Intent serviceIntent = new Intent(this, FocusMaxService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onBackPressed() {
        if (isMyServiceRunning(FocusMaxService.class)) {
            Toast.makeText(this, "Workout In Session", Toast.LENGTH_SHORT).show();
        } else {
            stopTimerService();

            PowerManager.WakeLock wakeLock;

            Intent intent = new Intent(this, Profile_Main.class);
            startActivity(intent);
        }
    }

    public void stopTimerService() {
        // Assume broadcast receiver is inplace because it is created in onCreate
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        //Unbinds service connection
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        //Stop service
        if (isMyServiceRunning(FocusMaxService.class)) {

            try {
                wakeLock.release();
                Log.d("Debug", "wakeLock: wakeLock released");
            } catch (Exception e) {
                Log.d("Debug", "wakeLock: no wakeLock found so nothing to release");
            }

            Intent serviceIntent = new Intent(this, FocusMaxService.class);
            stopService(serviceIntent);
        }
    }
}
