package com.pepin.nate.mentalhitt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pepin.nate.mentalhitt.DB.DB_Workout;
import com.pepin.nate.mentalhitt.Data.FormatTime;

import java.text.DateFormat;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class Statistics extends AppCompatActivity {

    TextView firstWorkoutDate, lastWorkoutDate, numberOfWorkouts;
    TextView averageFocus, averageRest, averageCoolDown, averageWorkout, averageReps;
    TextView totalFocus, totalRest, totalCoolDown, totalWorkout, totalReps, totalMeditation;

    Realm realm;
    RealmResults<DB_Workout> results;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

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

        if (!realm.isEmpty()) {

            initViews();

            results = realm.where(DB_Workout.class).findAll();
            total = results.size();

            getStats();
        }

        realm.close();
    }

    private void initViews() {
        firstWorkoutDate = findViewById(R.id.stat_firstWorkoutDate);
        lastWorkoutDate = findViewById(R.id.stat_lastWorkoutDate);
        numberOfWorkouts = findViewById(R.id.stat_numberOfWorkouts);

        averageFocus = findViewById(R.id.stat_averageFocus);
        averageRest = findViewById(R.id.stat_averageRest);
        averageCoolDown = findViewById(R.id.stat_averageCoolDown);
        averageWorkout = findViewById(R.id.stat_averageWorkoutTime);
        averageReps = findViewById(R.id.stat_averageReps);

        totalFocus = findViewById(R.id.stat_totalFocus);
        totalRest = findViewById(R.id.stat_totalRest);
        totalCoolDown = findViewById(R.id.stat_totalCoolDown);
        totalWorkout = findViewById(R.id.stat_totalWorkout);
        totalReps = findViewById(R.id.stat_totalReps);
        totalMeditation = findViewById(R.id.stat_totalMeditation);
    }

    private void getStats() {
        getTime();
        getAverageAndSum();
    }

    private void getTime() {
        //First Workout Date
        DB_Workout first = results.first();
        String sFirst =
                String.valueOf(
                        DateFormat.getDateInstance(DateFormat.SHORT).format(first.getDate())
                );
        firstWorkoutDate.setText(sFirst);

        //Last Workout Date
        DB_Workout last = results.last();
        String sLast =
                String.valueOf(
                        DateFormat.getDateInstance(DateFormat.SHORT).format(last.getDate())
                );
        lastWorkoutDate.setText(sLast);

        //Number of Workouts
        numberOfWorkouts.setText(String.valueOf(total));
    }

    private void getAverageAndSum() {
        //Init Vars
        int focusTotal = 0;
        int focusAverage = 0;
        int restTotal = 0;
        int restAverage = 0;
        int coolDownTotal = 0;
        int workoutTotal = 0;
        int repTotal = 0;
        int meditationTime = 0;

        //Go through and add values to get sum
        for (DB_Workout db_workout : results) {
            focusTotal += db_workout.getFocus() * db_workout.getReps();
            focusAverage += db_workout.getFocus();
            restTotal += db_workout.getRest() * db_workout.getReps();
            restAverage += db_workout.getRest();
            coolDownTotal += db_workout.getCoolDown();
            workoutTotal += db_workout.getTotalTime();
            repTotal += db_workout.getReps();
        }

        //While we can use this for the sum value
        //Then do the average after
        //Sets up average strings
        String fT = FormatTime.convertSeconds((focusTotal));
        String rT = FormatTime.convertSeconds((restTotal));
        String cT = FormatTime.convertSeconds(coolDownTotal);
        String wT = FormatTime.convertSeconds(workoutTotal);
        String repT = String.valueOf(repTotal);
        String mT = FormatTime.convertSeconds(focusTotal + restTotal + coolDownTotal);

        //Sets string values
        totalFocus.setText(fT);
        totalRest.setText(rT);
        totalCoolDown.setText(cT);
        totalWorkout.setText(wT);
        totalReps.setText(repT);
        totalMeditation.setText(mT);

        //Gets average
        focusAverage /= total;
        restAverage /= total;
        coolDownTotal /= total;
        workoutTotal /= total;
        repTotal /= total;

        //Sets up average strings
        String fA = FormatTime.convertSeconds(focusAverage);
        String rA = FormatTime.convertSeconds(restAverage);
        String cA = FormatTime.convertSeconds(coolDownTotal);
        String wA = FormatTime.convertSeconds(workoutTotal);
        String repA = String.valueOf(repTotal);


        //Sets string values
        averageFocus.setText(fA);
        averageRest.setText(rA);
        averageCoolDown.setText(cA);
        averageWorkout.setText(wA);
        averageReps.setText(repA);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
