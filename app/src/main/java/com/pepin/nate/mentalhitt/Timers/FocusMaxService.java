package com.pepin.nate.mentalhitt.Timers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ankushgrover.hourglass.Hourglass;
import com.pepin.nate.mentalhitt.Profile_Main;
import com.pepin.nate.mentalhitt.R;

import java.util.ArrayList;

import static com.pepin.nate.mentalhitt.Timers.App.CHANNEL_ID;


public class FocusMaxService extends Service {

    private Hourglass timer;

    public int maxTime;

    public boolean isTheTimerStarted = false;

    public Context context;

    private String timerType;

    public ArrayList<Integer> clickData;

    public int timeCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //This is what is run when the channel runs
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isTheTimerStarted = true;
        clickData = new ArrayList<>();

        context = this;

        //Get two values, maxTime and the timer type
        final int maxTime = intent.getIntExtra("totalTime", 300);
        timerType = intent.getStringExtra("timeType");

        //This starts it
        startForeground(2, getMyActivityNotification(String.valueOf("In Progress")).build());

        timer = setTimer(maxTime);

        if (!timer.isRunning())
            timer.startTimer();

        //This will keep it destroyed
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.stopTimer();
    }


    private NotificationCompat.Builder getMyActivityNotification(String text) {
        //This sets it up to what activity when you click on the notification bar
        Intent notificationIntent;
        notificationIntent = new Intent(this, Focus_max.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //This builds the notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Testing Focus Max")
                .setContentText(text)
                .setSmallIcon(R.raw.icon_focus_2)
                .setContentIntent(pendingIntent);

        return notification;
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(String text) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, getMyActivityNotification(text).build());
    }

    private boolean countDownOver = false;
    private int countInTime = 3;

    public Hourglass setTimer(final int iMaxTime) {

        maxTime = iMaxTime;

        final MediaPlayer s_stat = MediaPlayer.create(this, R.raw.s_focus);
        final MediaPlayer s_complete = MediaPlayer.create(this, R.raw.s_win);
        final MediaPlayer s_click = MediaPlayer.create(this, R.raw.s_click);

        final MediaPlayer s_count = MediaPlayer.create(this, R.raw.s_count_in);
        s_count.start();

        Hourglass hgTimer = new Hourglass(((iMaxTime + 1) * 1000) + countInTime * 1000, 1000) {
            int i = 0;

            @Override
            public void onTimerTick(long timeRemaining) {

                if (countInTime > 0) {
                    countInTime--;
                    return;
                }

                if (!countDownOver) {
                    countDownOver = true;
                    s_stat.start();
                }

                if (timeCount % 5 == 0) {
                    if (timeCount != 0) {
                        s_click.start();
                        clickData.add(timeCount);
                        i++;
                        Log.i("Debug", "Click: " + i + " Time: " + timeCount);
                    }
                }
                timeCount++;
                sendMessage();
            }

            @Override
            public void onTimerFinish() {
                Log.i("Debug", "Time is: " + timeCount + " and iMaxTime is: " + iMaxTime);
                if (timeCount == iMaxTime) {
                    s_complete.start();
                }

                sendMessage();

                s_stat.release();
                if (!s_complete.isPlaying()) s_complete.release();
                s_click.release();
            }
        };

        return hgTimer;
    }


    private void sendMessage() {
        Intent intent = new Intent("UPDATE_UI");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Binding
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        FocusMaxService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FocusMaxService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void killNotificationBar() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


}
