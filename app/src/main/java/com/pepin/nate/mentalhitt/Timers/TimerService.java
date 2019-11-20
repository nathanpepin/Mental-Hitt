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
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ankushgrover.hourglass.Hourglass;
import com.pepin.nate.mentalhitt.R;


import static com.pepin.nate.mentalhitt.Timers.App.CHANNEL_ID;
import static io.fabric.sdk.android.Fabric.TAG;


public class TimerService extends Service {

    public Hourglass timer;

    public int time;
    public int focus;
    public int rest;
    public int reps;
    public int coolDown;

    public boolean currentlyActive = false;

    public Context context;

    private String timerType;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //This is what is run when the channel runs
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentlyActive = true;

        context = this;

        //This gets whatever inputs came from the timer creation
        final int focus = intent.getIntExtra("focus", 5);
        final int rest = intent.getIntExtra("rest", 10);
        final int reps = intent.getIntExtra("reps", 8);
        final int coolDown = intent.getIntExtra("coolDown", 0);
        final int totalTime = intent.getIntExtra("totalTime", 300);

        timerType = intent.getStringExtra("timeType");

        //This starts it
        startForeground(1, getMyActivityNotification(String.valueOf("Exercise Starting")).build());

        timer = setTimer(totalTime, focus, rest, reps, coolDown);

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
        if (timerType.equals("SMART_TIMER")) {
            Log.d("SMART", timerType);
            notificationIntent = new Intent(this, SmartTimer.class);
            notificationIntent.putExtra("timerType", timerType);
        } else {
            Log.d("MANUAL", timerType);
            notificationIntent = new Intent(this, ManualTimer.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //This builds the notification
        NotificationCompat.Builder notification;

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Workout in Progress")
                .setContentText(text)
                .setSmallIcon(R.raw.icon_focus_2)
                .setContentIntent(pendingIntent);

        return notification;
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.notify(1, getMyActivityNotification(text).build());
        } else {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, getMyActivityNotification(text).build());
        }
    }

    private boolean countDownOver = false;
    private int countInTime;

    public Hourglass setTimer(final int totalTime, final int focusTime, final int restTime, final int repTotal, final int coolDownTime) {
        time = totalTime + 1;
        focus = focusTime;
        rest = restTime;
        reps = repTotal;
        coolDown = coolDownTime;
        countInTime = 3;

        final MediaPlayer s_focus, s_relax, s_win, s_coolDown;
        s_focus = MediaPlayer.create(this, R.raw.s_focus);
        s_relax = MediaPlayer.create(this, R.raw.s_relax);
        s_win = MediaPlayer.create(this, R.raw.s_win);
        s_coolDown = MediaPlayer.create(this, R.raw.s_cooldown);
        final MediaPlayer s_count = MediaPlayer.create(this, R.raw.s_count_in);

        s_count.start();

        Hourglass hgTimer = new Hourglass((totalTime * 1000) + countInTime * 1000, 1000) {

            @Override
            public void onTimerTick(long timeRemaining) {

                if (countInTime > 0) {
                    updateNotification(currentInfoText());
                    sendMessage();
                    countInTime--;
                    time = totalTime;//Ensures the right time is displayed
                    return;
                }

                if (!countDownOver) {
                    countDownOver = true;
                    s_focus.start();
                    time++;//Ensures the right time is displayed
                }

                //Focus and Rest function
                if (time == totalTime + 1) {
                    //Do nothing
                } else if (focus > 0 && reps > 0) {
                    focus--;
                    if (focus == 0) s_relax.start();
                } else if (rest > 0 && reps > 0) {
                    rest--;
                }

                //Reps and CoolDown function
                if (focus == 0 && rest == 0 && reps > 0) {
                    if (--reps > 0) {
                        focus = focusTime;
                        rest = restTime;
                    }
                } else {
                    coolDown--;
                }

                if (focus == focusTime) s_focus.start();
                if (time == coolDownTime + 1) s_coolDown.start();

                //Update time
                setTimeValue(time - 1);

                updateNotification(currentInfoText());
            }

            @Override
            public void onTimerFinish() {
                if (time == 0) {
                    s_win.start();
                    s_win.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            s_win.release();
                        }
                    });
                    updateNotification(currentInfoText());
                    sendMessage();
                }
                s_focus.release();
                s_relax.release();
                if (!s_win.isPlaying()) s_win.release();
                s_coolDown.release();
                s_count.release();
            }
        };

        return hgTimer;
    }

    public String currentInfoText() {
        String notifyText;

        if (!countDownOver) {
            notifyText = "Count Down: " + (countInTime + 1);
            return notifyText;
        }

        if (focus > 0) {
            notifyText = " Rep: " + reps + " Focus: " + focus;
        } else if (rest > 0) {
            notifyText = "Rep: " + reps + " Rest: " + rest;
        } else if (reps > 0) {
            notifyText = " Rep: " + reps + " Focus: " + focus;
        } else if (time > 0) {
            notifyText = "Cooldown: " + time;
        } else {
            notifyText = "Workout Complete!";
        }

        return notifyText;
    }

    private void setTimeValue(int value) {

        time = value;

        //Broadcast
        sendMessage();
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
        TimerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * method for clients
     */
    public int getFocus() {
        return focus;
    }

    public void killNotificationBar() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
}

