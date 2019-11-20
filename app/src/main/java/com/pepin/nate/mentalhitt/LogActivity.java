package com.pepin.nate.mentalhitt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pepin.nate.mentalhitt.DB.DB_LogAdapter;
import com.pepin.nate.mentalhitt.DB.DB_Workout;
import com.pepin.nate.mentalhitt.Data.FormatTime;

import java.text.DateFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class LogActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<String> mFocus = new ArrayList<>();
    private ArrayList<String> mFocusMax = new ArrayList<>();
    private ArrayList<String> mRest = new ArrayList<>();
    private ArrayList<String> mReps = new ArrayList<>();
    private ArrayList<String> mCoolDown = new ArrayList<>();
    private ArrayList<String> mTotalTime = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mLevel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        initData();
    }

    private void initData(){

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

        RealmResults<DB_Workout> results = realm.where(DB_Workout.class)
                .sort("date",Sort.DESCENDING)
                .findAll();

        //Build Focus
        for(DB_Workout db_workout : results){
            String builder = "Focus: " + FormatTime.convertSeconds(db_workout.getFocus());
            mFocus.add(builder);
        }

        //Build FocusMax
        for(DB_Workout db_workout : results){
            String builder = "Max: " + FormatTime.convertSeconds(db_workout.getFocusMax());
            mFocusMax.add(builder);
        }

        //Build Rest
        for(DB_Workout db_workout : results){
            String builder = "Rest: " + FormatTime.convertSeconds(db_workout.getRest());
            mRest.add(builder);
        }

        //Build Reps
        for(DB_Workout db_workout : results){
            String builder = "Reps: " + String.valueOf(db_workout.getReps());
            mReps.add(builder);
        }

        //Build CoolDown
        for(DB_Workout db_workout : results){
            String builder = "Cool Down: " + FormatTime.convertSeconds(db_workout.getCoolDown());
            mCoolDown.add(builder);
        }

        //Build TotalTime
        for(DB_Workout db_workout : results){
            String builder = "Time: " + FormatTime.convertSeconds(db_workout.getTotalTime());
            mTotalTime.add(builder);
        }

        //Build Date
        for(DB_Workout db_workout : results){
            String builder = "Date: " + String.valueOf(
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(db_workout.getDate())
            );
            mDate.add(builder);
        }

        //Build Level
        for(DB_Workout db_workout : results){
            String builder = "Level: " + String.valueOf(db_workout.getLevel());
            mLevel.add(builder);
        }

        initRecyclerView();

        realm.close();
    }

    private void initRecyclerView(){
        android.util.Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        DB_LogAdapter adapter = new DB_LogAdapter(this, mFocus, mFocusMax, mRest, mReps, mCoolDown, mTotalTime, mDate, mLevel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
