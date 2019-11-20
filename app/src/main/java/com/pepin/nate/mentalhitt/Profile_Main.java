package com.pepin.nate.mentalhitt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.Data.DataHelper;
import com.pepin.nate.mentalhitt.Data.FormatTime;
import com.pepin.nate.mentalhitt.Data.GenerateWorkout;
import com.pepin.nate.mentalhitt.Timers.Focus_max;
import com.pepin.nate.mentalhitt.Timers.SmartTimer;

import io.realm.Realm;


public class Profile_Main extends AppCompatActivity {

    private DataHelper dataHelper;

    public TextView
            currentFocus, currentFocusMax, currentRest, currentCoolDown, currentTotalTime, currentLevel, currentReps, stageText;

    ProgressBar _XPBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        init();

        final Context context = this;
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "Profile_Main: Trying to get Realm.getDefaultInstance");
        } catch (Exception e) {
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "Profile_Main: Realm.getDefaultInstance is null so Realm.init");
        }


        if (realm.isEmpty()) {
            //Don't allow these buttons if there is no data
            Button button_Log = findViewById(R.id.b_Logs);
            button_Log.setBackgroundResource(R.drawable.smart_trainer_button_log_inactive);
            //button_Log.setEnabled(false);

            Button button_Stats = findViewById(R.id.b_stats);
            button_Stats.setBackgroundResource(R.drawable.smart_trainer_button_statistics_inactive);
            //button_Stats.setEnabled(false);
        }

        realm.close();
    }

    private void init() {
        //Initialize Views
        currentFocus = findViewById(R.id.tv_currentFocusAmount);
        currentFocusMax = findViewById(R.id.tv_currentFocusMaxTime);
        currentRest = findViewById(R.id.tv_currentRestAmount);
        currentReps = findViewById(R.id.tv_currentRepsAmount);
        currentCoolDown = findViewById(R.id.tv_currentCoolDownAmount);
        currentTotalTime = findViewById(R.id.tv_currentTotalTimeAmount);
        currentLevel = findViewById(R.id.tv_level);
        _XPBar = findViewById(R.id.xpBar);
        _XPBar.setMax(100);
        _XPBar.setProgress(0);
        stageText = findViewById(R.id.tv_stage);

        Button progress = findViewById(R.id.b_progress);
        Button testMax = findViewById(R.id.button_goToFocusMax);
        //Is the advanced options are true
        if (DataHelper.getAdvancedOptionsState(this)) {
            progress.setVisibility(View.VISIBLE);
            testMax.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            testMax.setVisibility(View.GONE);
        }

        //Setup datehelper
        dataHelper = new DataHelper(this);

        updateUI();
    }

    private void updateUI() {
        currentFocus.setText(FormatTime.convertSeconds(dataHelper.focus));
        currentFocusMax.setText(FormatTime.convertSeconds(dataHelper.focusMax));
        currentRest.setText(FormatTime.convertSeconds(dataHelper.rest));
        currentReps.setText(String.valueOf(dataHelper.reps));
        currentCoolDown.setText(FormatTime.convertSeconds(dataHelper.coolDown));
        currentTotalTime.setText(FormatTime.convertSeconds(dataHelper.totalTime));
        currentLevel.setText(String.valueOf(dataHelper.level));
        _XPBar.setProgress(dataHelper.XP);
        //stageText.setText(String.valueOf(GenerateWorkout.getPercentage(dataHelper.level, dataHelper.XP)) + "%");
        stageText.setText(GenerateWorkout.getStageText(dataHelper.level, dataHelper.XP));
    }

    public void startNextWorkout(View view) {
        view.setBackgroundResource(R.drawable.smart_trainer_button_next_workout_hover);

        //If the next workout is focusMax test, then go to it instead
        if ((GenerateWorkout.checkTestMax(dataHelper.level, dataHelper.XP))) {
            Intent intent = new Intent(this, Focus_max.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SmartTimer.class);
            intent.putExtra("timerType", "smartTimer");
            startActivity(intent);
        }
    }

    public void clearData(View view) {
        dataHelper.clearData();
        updateUI();
    }

    public void getNextWorkout(View view) {
        if (GenerateWorkout.checkMaxedOut(dataHelper.level, dataHelper.focus, dataHelper.rest, dataHelper.totalTime)) {
            Toast.makeText(this, "Congrats! For your time, your level is maxed out!", Toast.LENGTH_SHORT).show();
        } else {
            if ((GenerateWorkout.checkTestMax(dataHelper.level, dataHelper.XP))) {
                Intent intent = new Intent(this, Focus_max.class);
                startActivity(intent);
            } else {
                dataHelper.initDataHelper();
                dataHelper.getNextWorkout();
                updateUI();
            }
        }
    }

    public void goToFocusMax(View view) {
        view.setBackgroundResource(R.drawable.smart_trainer_button_test_max_hover);

        Intent intent = new Intent(this, Focus_max.class);
        startActivity(intent);
    }

    public void goToStatistics(View view) {

        final Context context = this;
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "LogActivity: Trying to get Realm.getDefaultInstance");
        } catch (Exception e) {
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "LogActivity: Realm.getDefaultInstance is null so Realm.init");
        }

        if (realm.isEmpty()) {
            Toast.makeText(this, "No data yet, start a workout", Toast.LENGTH_LONG).show();
            return;
        }
        realm.close();

        view.setBackgroundResource(R.drawable.smart_trainer_button_statistics_hover);

        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
    }


    public void go_Settings(View view) {
        view.setBackgroundResource(R.drawable.smart_trainer_button_settings_hover);

        Intent intent = new Intent(this, Profile_Settings.class);
        startActivity(intent);
    }

    public void goToLogs(View view) {

        final Context context = this;
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "LogActivity: Trying to get Realm.getDefaultInstance");
        } catch (Exception e) {
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "LogActivity: Realm.getDefaultInstance is null so Realm.init");
        }

        if (realm.isEmpty()) {
            Toast.makeText(this, "No data yet, start a workout", Toast.LENGTH_LONG).show();
            realm.close();
            return;
        }
        realm.close();

        view.setBackgroundResource(R.drawable.smart_trainer_button_log_hover);

        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        dataHelper.initDataHelper();
        updateUI();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}