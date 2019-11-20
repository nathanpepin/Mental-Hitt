package com.pepin.nate.mentalhitt.Data;

import android.widget.Toast;

public class WorkoutData {

    public int focusTime=0;
    public int restTime=0;
    public int reps=0;
    public int totalTime=0;

    public int coolDown=0;

    //These hold the values for the initial start
    public int iFocusTime;
    public int iRestTime;
    public int iReps;
    public int iTotalTime;

    public boolean isCounting=false;

    public void updateTotalTime() {
        totalTime = ((focusTime + restTime) * reps) + coolDown;
    }

    public void setTotalTime(int time) {
        totalTime = time;
    }

    public int getTotalTime() {
        updateTotalTime();
        return(totalTime);
    }

    public int getWorkoutTime() {
        return totalTime = ((focusTime + restTime) * reps);
    }

    public void setIValues() {
        iFocusTime = focusTime;
        iRestTime = restTime;
        iReps = reps;
        iTotalTime = totalTime;
    }
}
