package com.pepin.nate.mentalhitt.Data;

public class FormatTime {

    public static String convertMsToS(int ms) {
        int totalSeconds = ms / 1000;
        int minutes = 0;
        int seconds = 0;

        while (totalSeconds >= 60) {
            minutes++;
            totalSeconds -= 60;
        }

        seconds = totalSeconds;

        String secondsString = String.valueOf(seconds);

        if (secondsString.length() == 1) {
            secondsString = "0" + secondsString;
        }

        return (String.valueOf(minutes) + ":" + secondsString.toString());
    }

    public static String convertSeconds(int secondsInput) {
        int totalSeconds = secondsInput;
        int minutes = 0;
        int seconds = 0;

        while (totalSeconds >= 60) {
            minutes++;
            totalSeconds -= 60;
        }

        seconds = totalSeconds;

        String secondsString = String.valueOf(seconds);

        if (secondsString.length() == 1) {
            secondsString = "0" + secondsString;
        }

        return (String.valueOf(minutes) + ":" + secondsString.toString());
    }

    public static String convertSecondsFullLength(int secondsInput) {
        int totalSeconds = secondsInput;
        int minutes = 0;
        int seconds = 0;

        while (totalSeconds >= 60) {
            minutes++;
            totalSeconds -= 60;
        }

        seconds = totalSeconds;

        String secondsString = String.valueOf(seconds);

        if (secondsString.length() == 1) {
            secondsString = "0" + secondsString;
        }

        String minutesString = String.valueOf(minutes);

        if (minutesString.length() == 1) {
            minutesString = "0" + minutesString;
        }

        return (minutesString + ":" + secondsString.toString());
    }

    //The time string in only ever expected to be 4 chars long
    public static int convertTimeString(String time) {

        int seconds=0;

        //Goes through each length case and assigns value based on length

        //TM:IS
        if (time.length() == 4) {
            String T = Character.toString(time.charAt(0));
            String M = Character.toString(time.charAt(1));
            String I = Character.toString(time.charAt(2));
            String S = Character.toString(time.charAt(3));

            //60 seconds in a minute times 10 per power
            seconds += Integer.parseInt(T) * 60 * 10;
            //60 seconds in a minute
            seconds += Integer.parseInt(M) * 60;
            //10 seconds per power
            seconds += Integer.parseInt(I) * 10;
            //Just seconds
            seconds += Integer.parseInt(S);
        }

        //M:IS
        if (time.length() == 3) {
            String M = Character.toString(time.charAt(0));
            String I = Character.toString(time.charAt(1));
            String S = Character.toString(time.charAt(2));
            //60 seconds in a minute
            seconds += Integer.parseInt(M) * 60;
            //10 seconds per power
            seconds += Integer.parseInt(I) * 10;
            //Just seconds
            seconds += Integer.parseInt(S);
        }

        //IS
        if (time.length() == 2) {
            String I = Character.toString(time.charAt(0));
            String S = Character.toString(time.charAt(1));
            //10 seconds per power
            seconds += Integer.parseInt(I) * 10;
            //Just seconds
            seconds += Integer.parseInt(S);
        }

        //S
        if (time.length() == 1) {
            String S = Character.toString(time.charAt(0));
            seconds += Integer.parseInt(S);
        }

        return seconds;
    }

    public static String deboneTime(String time) {
        String string = time;
        string.replace(":", "");

        return string;
    }

}