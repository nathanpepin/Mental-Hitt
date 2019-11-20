package com.pepin.nate.mentalhitt.Timers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.Data.Constants;
import com.pepin.nate.mentalhitt.Data.DataHelper;
import com.pepin.nate.mentalhitt.Data.GenerateWorkout;
import com.pepin.nate.mentalhitt.Profile_Main;
import com.pepin.nate.mentalhitt.R;

public class SmartTimer extends ManualTimer {

    Button button_completeWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_timer);

        //Sets up data to pull from
        workoutSharedPreference = Constants.CURRENT_WORKOUT;

        //Sets up timerType
        timerType = "SMART_TIMER";

        init();

        //Makes the view uneditable
        focusTime.setEnabled(false);
        restTime.setEnabled(false);
        coolDown.setEnabled(false);
        reps.setEnabled(false);

        button_completeWorkout = findViewById(R.id.b_finishWorkout);
        button_completeWorkout.setVisibility(View.GONE);
    }

    //Makes the view uneditable
    @Override
    public void reset(View view) {
        super.reset(view);

        focusTime.setEnabled(false);
        restTime.setEnabled(false);
        coolDown.setEnabled(false);
        reps.setEnabled(false);
    }

    @Override
    public void updateSharedPreferences() {
        //Do nothing
    }

    public void finishWorkout(View view) {
        //Get the data
        DataHelper dataHelper = new DataHelper(this);

        //Check the data
        if (GenerateWorkout.checkMaxedOut(dataHelper.level, dataHelper.focus, dataHelper.rest, dataHelper.totalTime)) {
            Toast.makeText(this, "Congrats! For your time, your level is maxed out!", Toast.LENGTH_LONG).show();
        } else {
            dataHelper.getNextWorkout();
        }

        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
    }

    @Override
    public void onTimerFinish() {
        super.onTimerFinish();

        //Button states changed so that you can only get the next workout
        button_completeWorkout.setVisibility(View.VISIBLE);
        button_completeWorkout.setEnabled(true);
        button_start.setEnabled(false);
        button_stop.setEnabled(false);
        button_reset.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (isMyServiceRunning(TimerService.class)) {
            Toast.makeText(this, "Workout In Session", Toast.LENGTH_SHORT).show();
        } else {
            //This shouldn't need to be called, but it is done incase someone goes crazy with the button pressing
            stopTimerService();

            Intent intent = new Intent(this, Profile_Main.class);
            startActivity(intent);
        }
    }
}