package com.helper;

import java.util.Locale;

public class Constants {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String WEIGHT = "weightKey";
    public static final String AGE = "ageKey";
    public static final String WAKEUPTIME = "wakeupTimeKey";
    public static final String BEDTIME = "bedTimeKey";
    public static final String INTAKEGOAL = "intakeGoalKey";

    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";
    public static final String YEARLY = "yearly";

    public static final String[] MONTH_LIST =
            {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] DAYS_LIST =
            {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public static final Locale LOCALE_INDIA = new Locale("en", "IN");

    public static final String ERROR_WEIGHT_EMPTY = "Please enter your weight in pounds.";
    public static final String ERROR_WEIGHT_ILLEGAL_ENTRY = "The value you have entered is out of range.\nPlease enter your weight in pounds.";
    public static final String ERROR_AGE_EMPTY = "Please enter your age.";
    public static final String ERROR_AGE_ILLEGAL_ENTRY = "The value you have entered is out of range.\nPlease enter a valid age.";
    public static final String ERROR_TIME_ILLEGAL_ENTRY = "The value you have entered is out of range.\nPlease select appropriate bed time.";

}
