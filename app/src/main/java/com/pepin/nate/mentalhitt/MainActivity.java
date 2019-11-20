package com.pepin.nate.mentalhitt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pepin.nate.mentalhitt.Timers.ManualTimer;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final String comingSoonMessage = "This Feature Coming Soon!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView version = findViewById(R.id.tv_version);
        String versionCode = BuildConfig.VERSION_NAME;
        version.setText(versionCode);

        final Context context = this;
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.close();
            Log.i("Debug", "MainActivity: Trying to get Realm.getDefaultInstance");
        } catch (Exception e) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Realm.init(context);
                    Log.i("Debug", "MainActivity: ...Realm.init...");
                }
            }).start();
            Log.i("Debug", "MainActivity: Not found so Realm.init");
        }
    }

    public void smartTraining(View view) {
        view.setBackgroundResource(R.drawable.main_button_smart_trainer_hover);

        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
    }

    public void manualTraining(View view) {
        view.setBackgroundResource(R.drawable.main_button_manual_timer_hover);

        Intent intent = new Intent(this, ManualTimer.class);
        startActivity(intent);
    }

    public void howToUse(View view) {

        view.setBackgroundResource(R.drawable.main_button_how_to_use_hover);

        final View v = view;

        //Sets the hover, and then sets back
        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                v.setBackgroundResource(R.drawable.main_button_how_to_use);
            }
        };

        timer.start();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nathanpepin.com"));
        startActivity(browserIntent);

        /*Intent intent = new Intent(this, HowToUse.class);
        startActivity(intent);*/
    }

    public void about(View view) {
        view.setBackgroundResource(R.drawable.main_button_about_hover);

        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        //Kill the process on back
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}