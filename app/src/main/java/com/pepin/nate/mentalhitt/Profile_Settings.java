package com.pepin.nate.mentalhitt;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.Animation.ProgressBarAnimation;
import com.pepin.nate.mentalhitt.Data.DataHelper;
import com.pepin.nate.mentalhitt.Data.FormatTime;
import com.pepin.nate.mentalhitt.Data.GenerateWorkout;
import com.santalu.widget.MaskEditText;


public class Profile_Settings extends AppCompatActivity {

    //private EditText setMaxTime;
    private MaskEditText setMaxTime;
    private MaskEditText setCoolDown;
    private ProgressBar confirmTimer;
    private TextView restartText;
    private Button b_clearData;
    private String buttonState = "initial";

    private final String resetButton_defaultText = "Reset";
    private final String resetButton_confirmText = "Yes";

    private Switch aSwitch;

    private boolean advancedOptionsEnabled;

    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        ////////////////////////////////////
        //Initialize Views
        ////////////////////////////////////
        setMaxTime = findViewById(R.id.et_setMaxTime);
        setCoolDown = findViewById(R.id.et_setCoolDown);
        confirmTimer = findViewById(R.id.pb_confirmRestart);
        restartText = findViewById(R.id.d_restartText);
        b_clearData = findViewById(R.id.b_clearData);

        dataHelper = new DataHelper(this);

        aSwitch = findViewById(R.id.enableAdvancedOptions);
        advancedOptionsEnabled = DataHelper.getAdvancedOptionsState(this);
        aSwitch.setChecked(advancedOptionsEnabled);

        setMaxTime.setText(FormatTime.convertSecondsFullLength(dataHelper.totalTime));

        int cd = dataHelper.coolDown - GenerateWorkout.getLeftOverTime(dataHelper.focus, dataHelper.rest, dataHelper.totalTime);
        setCoolDown.setText(FormatTime.convertSecondsFullLength(cd));

        //setMaxTime.addTextChangedListener(maxTimeWatcher);
    }

    private TextWatcher maxTimeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setMaxTime.removeTextChangedListener(maxTimeWatcher);

            /*int convertMasked = FormatTime.convertTimeString(setMaxTime.getText().toString());
            String conversion = FormatTime.convertSeconds(convertMasked);*/

            String c = FormatTime.deboneTime(setMaxTime.getText().toString());

            Log.i("INFO", c);

            if (c.length() == 3) {
                String val = s.charAt(0) + ":" + s.charAt(1) + s.charAt(2);
                setMaxTime.setText(val);
            }

            if (c.length() == 4) {
                String val = s.charAt(0) + s.charAt(1) + ":" + s.charAt(2) + s.charAt(3);
                setMaxTime.setText(val);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            setMaxTime.addTextChangedListener(maxTimeWatcher);
        }
    };

    public void saveSettings(View view) {
        String pleaseReformatTime = "Please reformat/insert the time";

        DataHelper dataHelper = new DataHelper(this);

        String nullCheckTextTotalTime = setMaxTime.getText().toString();
        if (!nullCheckTextTotalTime.equals("")) {
            String nullCheckTexTotalTime2 = setMaxTime.getRawText();
            if (nullCheckTexTotalTime2.equals(":")) {
                Toast.makeText(this, pleaseReformatTime, Toast.LENGTH_SHORT).show();
                return;
            } else {
                int convertMasked = FormatTime.convertTimeString(setMaxTime.getRawText());
                dataHelper.setTotalTime(convertMasked);
            }
        } else {
            Toast.makeText(this, pleaseReformatTime, Toast.LENGTH_SHORT).show();
            return;
        }

        String nullCheckTextCoolDown = setCoolDown.getText().toString();
        if (!nullCheckTextCoolDown.equals("")) {
            String nullCheckText2 = setCoolDown.getRawText();
            if (nullCheckText2.equals(":")) {
                Toast.makeText(this, pleaseReformatTime, Toast.LENGTH_SHORT).show();
                return;
            } else {
                int convertMasked = FormatTime.convertTimeString(setCoolDown.getRawText());
                dataHelper.setCoolDown(convertMasked);
            }
        } else {
            Toast.makeText(this, pleaseReformatTime, Toast.LENGTH_SHORT).show();
            return;
        }

        //Set switch
        DataHelper.setAdvancedOptions(this, advancedOptionsEnabled);

        //There would have been a lot easier way to do this with setters and getters
        dataHelper.commitNextWorkout();

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
    }

    public void switchAdvanced(View view) {
        if (advancedOptionsEnabled) {
            advancedOptionsEnabled = false;
        } else {
            advancedOptionsEnabled = true;
        }
    }

    public void clearData(View view) {

        switch (buttonState) {
            case "initial":
                startTimer();
                break;
            case "clearEnabled":
                dataHelper.clearData();
                Toast.makeText(this, "Data Cleared", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Profile_Main.class);
                startActivity(intent);
                break;
        }
    }

    private void startTimer() {
        b_clearData.setEnabled(false);
        confirmTimer.setVisibility(View.VISIBLE);
        restartText.setVisibility(View.VISIBLE);
        final ProgressBarAnimation anim = new ProgressBarAnimation(confirmTimer, 100, 0);
        anim.setDuration(2000);
        confirmTimer.startAnimation(anim);

        b_clearData.setText(resetButton_confirmText);

        CountDownTimer timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                confirmTimer.setProgress(0);
                b_clearData.setEnabled(true);
                confirmTimer.startAnimation(anim);
                buttonState = "clearEnabled";

                CountDownTimer secondTimer = new CountDownTimer(2000, 2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        b_clearData.setText(resetButton_defaultText);
                        buttonState = "initial";
                        confirmTimer.setVisibility(View.GONE);
                        restartText.setVisibility(View.GONE);
                    }
                };

                secondTimer.start();
            }
        };

        timer.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Profile_Main.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
