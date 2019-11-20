package com.pepin.nate.mentalhitt.Data;

import android.util.Log;
import android.widget.Toast;

import com.pepin.nate.mentalhitt.Profile_Main;

public class GenerateWorkout {

    public static int testFocusMaxLevel = 20;

    public static int getFocus(int level, int focusMax, int newFocusMax) {

        if (newFocusMax > focusMax) {
            return newFocusMax;
        }

        if (level < 4) {
            focusMax += 3;
        } else if (level < 8) {
            focusMax += 2;
        } else if (level < 20) {
            focusMax += 1;
        } else {
            focusMax = newFocusMax;
        }

        return focusMax;
    }

    public static double getPercentage(int level, int XP) {
        int stageNumber=0;
        int ofStage;


        if (level < 6) {
            ofStage = 1;
        } else if (level < 15) {
            ofStage = 2;
        } else if (level < 20) {
            ofStage = 3;
        } else if (level < 30) {
            ofStage = 4;
        } else if (level < 40) {
            ofStage = 5;
        } else {
            ofStage = 6;
        }

        int levelXP = increaseXP(level, 0);

        stageNumber = XP / levelXP;
        stageNumber++;

        int percent = (100 - (ofStage * 10)) + (stageNumber * 10);

        return percent;
    }

    //This determines the number of states that occur
    public static int increaseXP(int level, int XP) {
        //Checks level ranges
        if (level < 6) {
            //Stages: 1
            XP += 100;
        } else if (level < 15) {
            //Stages: 2
            XP += 50;
        } else if (level < 20) {
            //Stages: 3
            XP += 34;
        } else if (level < 30) {
            //Stages: 4
            XP += 25;
        } else if (level < 40) {
            //Stages: 5
            XP += 20;
        } else {
            //Stages: 6
            XP += 17;
        }
        return XP;
    }

    public static Boolean checkTestMax(int level, int XP) {

        //Gets the stage
        int stageNumber;
        int ofStage;

        if (level < 6) {
            ofStage = 1;
        } else if (level < 15) {
            ofStage = 2;
        } else if (level < 20) {
            ofStage = 3;
        } else if (level < 30) {
            ofStage = 4;
        } else if (level < 40) {
            ofStage = 5;
        } else {
            ofStage = 6;
        }

        int levelXP = increaseXP(level, 0);

        stageNumber = XP / levelXP;
        stageNumber++;

        //If the level is high enough and you are on the last stage then test your max
        if (level > testFocusMaxLevel & stageNumber / ofStage == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static String getStageText(int level, int XP) {
        int stageNumber;
        int ofStage;


        if (level < 6) {
            ofStage = 1;
        } else if (level < 15) {
            ofStage = 2;
        } else if (level < 20) {
            ofStage = 3;
        } else if (level < 30) {
            ofStage = 4;
        } else if (level < 40) {
            ofStage = 5;
        } else {
            ofStage = 6;
        }

        int levelXP = increaseXP(level, 0);

        stageNumber = XP / levelXP;
        stageNumber++;

        int percent = (100 - (ofStage * 10)) + (stageNumber * 10);

        if (level > testFocusMaxLevel & stageNumber / ofStage == 1) {
            return ("Test Your Focus Max");
        } else {
            return ("Focus Intensity: " + percent + "%");
        }
    }

    public static int getRest(int level, int rest) {
        if (level % 10 == 0) {
            rest += 15;
        }
        return rest;
    }

    public static int getTotalTime(int focusTime, int restTime, int reps, int coolDown) {
        int totalTime = ((focusTime + restTime) * reps) + coolDown;
        return totalTime;
    }

    public static int getReps(int focusTime, int restTime, int totalTime) {
        int reps = 0;

        int repTime = focusTime + restTime;
        while (totalTime >= repTime) {
            reps++;
            totalTime -= repTime;
        }

        return reps;
    }

    public static int getCoolDown(int focusTime, int restTime, int totalTime, int coolDown) {

        int leftOverTime = getLeftOverTime(focusTime, restTime, totalTime);

        return leftOverTime + coolDown;
    }

    public static int getCoolDown(int focusTime, int restTime, int totalTime) {

        int leftOverTime = getLeftOverTime(focusTime, restTime, totalTime);

        return leftOverTime;
    }

    public static int getLeftOverTime(int focusTime, int restTime, int totalTime) {
        int reps = getReps(focusTime, restTime, totalTime);
        int repTime = focusTime + restTime;

        int leftOverTime = (totalTime - (reps * repTime));

        return leftOverTime;

    }

    public static Boolean checkMaxedOut(int level, int focusTime, int restTime, int totalTime) {
        int focus, rest;
        focus = getFocus(level, focusTime, 0);
        rest = getRest(level, restTime);

        if (getReps(focus, rest, totalTime) < 2) {
            return true;
        } else {
            return false;
        }
    }
}