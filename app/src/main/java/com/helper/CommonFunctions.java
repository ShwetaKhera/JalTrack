package com.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.util.TimeZone;

public class CommonFunctions {
    public static String formatTime(int hh, int mm) {
        String time;
        if (hh < 10) {
            if (mm < 10) {
                time = "0" + hh + ":" + "0" + mm;
            } else {
                time = "0" + hh + ":" + mm;
            }
        } else {
            if (mm < 10) {
                time = hh + ":" + "0" + mm;
            } else {
                time = hh + ":" + mm;
            }
        }
        return time;
    }

    public static int calculateIntakeFromWeightAndAge(int age, int weightInPounds) {
        int volume = Math.round(weightInPounds / 2.2f);
        if (age < 30) {
            volume = volume * 40;
        } else if (age < 55) {
            volume = volume * 35;
        } else {
            volume = volume * 30;
        }
        return volume;
    }

    public static int getOffset() {
        return TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
    }

    public static int convertDpToPixel(Context context, int value) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                r.getDisplayMetrics()
        );
    }
}
