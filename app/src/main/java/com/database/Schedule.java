package com.database;

import java.util.List;

public class Schedule {

    public static final String TABLE_NAME = "SCHEDULE";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_ON = "isOn";
    public static final String COLUMN_DAYS_ARRAY = "days";

    private int id;
    private String time;
    private boolean isOn;
    private List<Integer> days;

    public static final String CREATE_TABLE_SCHEDULE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIME + " TEXT NOT NULL, "
                    + COLUMN_ON + " INTEGER NOT NULL, "
                    + COLUMN_DAYS_ARRAY + " TEXT"
                    + ")";

    public Schedule() {
    }

    public Schedule(String time, boolean isOn, List<Integer> days) {
        this.time = time;
        this.isOn = isOn;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List days) {
        this.days = days;
    }

    public void addDay(int day) {
        this.days.add(day);
    }

    public void removeDay(int day) {
        for (int i = 0; i < days.size(); i++) {
            if (day == days.get(i)) {
                days.remove(i);
                return;
            }
        }
    }

    public boolean hasDay(int index) {
        for (int day : days) {
            if (day == index) {
                return true;
            }
        }
        return false;
    }

    public void setId(int id) {
        this.id = id;
    }
}
