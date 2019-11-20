package com.pepin.nate.mentalhitt.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.DB.DB_Workout;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataHelper {
    public int focus, rest, reps, coolDown, totalTime, level, XP;
    public int focusMax;
    public int nextFocusMax = 0;

    public static Context context;


    public DataHelper(Context contextValue) {
        context = contextValue;

        initDataHelper();
    }

    public void clearData() {
        SharedPreferences currentWorkout = context.getSharedPreferences(Constants.CURRENT_WORKOUT, 0);
        SharedPreferences.Editor editorCW = currentWorkout.edit();
        editorCW.putInt(Constants.FOCUS, Constants.DEFAULT_FOCUS_TIME);
        editorCW.putInt(Constants.FOCUS_MAX, Constants.DEFAULT_FOCUS_MAX);
        editorCW.putInt(Constants.REST, Constants.DEFAULT_REST_TIME);
        editorCW.putInt(Constants.REPS, Constants.DEFAULT_REPS);
        editorCW.putInt(Constants.COOL_DOWN, Constants.DEFAULT_COOL_DOWN);
        editorCW.putInt(Constants.TOTAL_TIME, Constants.DEFAULT_TOTAL_TIME);
        editorCW.putInt(Constants.LEVEL, Constants.DEFAULT_LEVEL);
        editorCW.putInt(Constants.XP, 0);
        editorCW.apply();

        initDataHelper();

        clearLog();
    }

    public static void commitSinglePreference(Context contextValue, String TABLE, String ROW, int VALUE) {
        SharedPreferences lastWorkout = contextValue.getSharedPreferences(TABLE, 0);
        SharedPreferences.Editor editorLW = lastWorkout.edit();
        editorLW.putInt(ROW, VALUE);
        editorLW.apply();
    }

    public static void commitSinglePreference(String TABLE, String ROW, int VALUE) {
        SharedPreferences lastWorkout = context.getSharedPreferences(TABLE, 0);
        SharedPreferences.Editor editorLW = lastWorkout.edit();
        editorLW.putInt(ROW, VALUE);
        editorLW.apply();
    }

    public static void setAdvancedOptions(Context c, Boolean value) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(Constants.CURRENT_WORKOUT, 0);
        SharedPreferences.Editor editorLW = sharedPreferences.edit();
        editorLW.putBoolean("ADVANCED_OPTIONS", value);
        editorLW.apply();
    }


    public static Boolean getAdvancedOptionsState(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(Constants.CURRENT_WORKOUT, 0);
        Boolean state = sharedPreferences.getBoolean("ADVANCED_OPTIONS", false);
        return state;
    }

    public void initDataHelper() {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.CURRENT_WORKOUT, 0);

        focus = sharedPref.getInt(Constants.FOCUS, Constants.DEFAULT_FOCUS_TIME);
        focusMax = sharedPref.getInt(Constants.FOCUS_MAX, Constants.DEFAULT_FOCUS_MAX);
        rest = sharedPref.getInt(Constants.REST, Constants.DEFAULT_REST_TIME);
        reps = sharedPref.getInt(Constants.REPS, Constants.DEFAULT_REPS);
        coolDown = sharedPref.getInt(Constants.COOL_DOWN, Constants.DEFAULT_COOL_DOWN);
        totalTime = sharedPref.getInt(Constants.TOTAL_TIME, Constants.DEFAULT_TOTAL_TIME);
        level = sharedPref.getInt(Constants.LEVEL, Constants.DEFAULT_LEVEL);
        XP = sharedPref.getInt(Constants.XP, 0);
    }

    public void commitNextWorkout() {
        SharedPreferences currentWorkout = context.getSharedPreferences(Constants.CURRENT_WORKOUT, 0);
        SharedPreferences.Editor editorCW = currentWorkout.edit();
        editorCW.putInt(Constants.FOCUS, focus);
        editorCW.putInt(Constants.FOCUS_MAX, focusMax);
        editorCW.putInt(Constants.REST, rest);
        editorCW.putInt(Constants.REPS, reps);
        editorCW.putInt(Constants.COOL_DOWN, coolDown);
        editorCW.putInt(Constants.TOTAL_TIME, totalTime);
        editorCW.putInt(Constants.LEVEL, level);
        editorCW.putInt(Constants.XP, XP);
        editorCW.apply();
    }

    public void setTotalTime(int i_totalTime) {
        totalTime = i_totalTime;
        reps = GenerateWorkout.getReps(focus, rest, totalTime);
    }

    public void setCoolDown(int iCoolDown) {
        coolDown = GenerateWorkout.getCoolDown(focus, rest, totalTime, iCoolDown);
    }

    public void getNextWorkout() {

        boolean levelUp = false;
        int tempCD = coolDown - GenerateWorkout.getLeftOverTime(focus, rest, totalTime);

        //Increase XP
        XP = GenerateWorkout.increaseXP(level, XP);

        //Check if level up
        if (XP >= 100) {
            XP = 0;
            levelUp = true;
        }

        if (levelUp) {
            Log.d("Debug", "nextFocusMax: " + nextFocusMax);
            Log.d("Debug", "focusMax: " + focusMax);

            //Keeps at same focusMax is nextFocus max is less
            if (nextFocusMax < focusMax) {
                nextFocusMax = focusMax;
            }

            //nextFocusMax is only used when it exceeds focusMax
            focusMax = GenerateWorkout.getFocus(level, focusMax, nextFocusMax);
            rest = GenerateWorkout.getRest(level, rest);
            level = level + 1;
        }

        double percent = (GenerateWorkout.getPercentage(level, XP));
        double result = Math.round((focusMax * percent) / 100);
        focus = (int) result;
        reps = GenerateWorkout.getReps(focus, rest, totalTime);

        coolDown = GenerateWorkout.getCoolDown(focus, rest, totalTime, tempCD);

        commitNextWorkout();
    }

    public void restartNextWorkout() {
        int tempCD = coolDown - GenerateWorkout.getLeftOverTime(focus, rest, totalTime);
        //Increase XP
        XP = 0;
        double percent = (GenerateWorkout.getPercentage(level, XP));
        double result = Math.round((focusMax * percent) / 100);
        focus = (int) result;
        reps = GenerateWorkout.getReps(focus, rest, totalTime);
        coolDown = GenerateWorkout.getCoolDown(focus, rest, totalTime, tempCD);

        commitNextWorkout();
    }

    public void storeLog() {

        Realm realm = initRealm();

        realm.beginTransaction();

        DB_Workout db_workout = realm.createObject(DB_Workout.class);
        db_workout.setFocus(focus);
        db_workout.setFocusMax(focusMax);
        db_workout.setRest(rest);
        db_workout.setCoolDown(coolDown);
        db_workout.setReps(reps);
        db_workout.setTotalTime(totalTime);
        db_workout.setLevel(level);

        Date currentTime = Calendar.getInstance().getTime();
        db_workout.setDate(currentTime);

        realm.commitTransaction();

        realm.close();
    }

    public void clearLog() {

        Realm realm = initRealm();

        RealmResults<DB_Workout> results = realm.where(DB_Workout.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();

        realm.close();
    }

    private Realm initRealm() {
        Realm realm;

        try {
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "Datahelper.initRealm: Trying to getDefaultInstance");
        } catch (Exception e) {
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            Log.i("Debug", "Datahelper.initRealm: ...Realm.init...");
            Toast.makeText(context, "Loaded Data", Toast.LENGTH_SHORT).show();
        }

        return realm;
    }

}

