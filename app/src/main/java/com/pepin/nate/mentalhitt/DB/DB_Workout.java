package com.pepin.nate.mentalhitt.DB;

import java.util.Date;

import io.realm.RealmObject;

public class DB_Workout extends RealmObject {

    private int focus;
    private int focusMax;
    private int rest;
    private int reps;
    private int coolDown;
    private int totalTime;
    private int level;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFocusMax() {
        return focusMax;
    }

    public void setFocusMax(int focusMax) {
        this.focusMax = focusMax;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFocus() {
        return focus;
    }

    public int getRest() {
        return rest;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }
}
