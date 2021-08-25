package com.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.database.Schedule.COLUMN_TIME;

public class ScheduleDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "schedule_db";


    public ScheduleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Schedule.CREATE_TABLE_SCHEDULE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Schedule.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertSchedule(String time, boolean isOn, List<Integer> list) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(COLUMN_TIME, time);
        values.put(Schedule.COLUMN_ON, isOn);
        values.put(Schedule.COLUMN_DAYS_ARRAY, String.valueOf(list));

//        System.out.println(list);
        // insert row
        long id = db.insert(Schedule.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public int getScheduleCount() {
        String countQuery = "SELECT  * FROM " + Schedule.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public List<Schedule> getAllSchedule() {
        List<Schedule> schedules = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Schedule.TABLE_NAME
                + " ORDER BY " + COLUMN_TIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndex(Schedule.COLUMN_ID)));
                schedule.setOn(cursor.getInt(cursor.getColumnIndex(Schedule.COLUMN_ON)) > 0);
                schedule.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));

                List<Integer> daysList = new ArrayList<>();
                String daysStr = cursor.getString(cursor.getColumnIndex(Schedule.COLUMN_DAYS_ARRAY));
                for (int s = 0; s < daysStr.length(); s++) {
                    boolean flag = Character.isDigit(daysStr.codePointAt(s));
                    if (flag) {
                        daysList.add(Integer.parseInt(String.valueOf(daysStr.charAt(s))));
                    }
                }
                schedule.setDays(daysList);

                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return schedules;
    }

    public int updateScheduleDays(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Schedule.COLUMN_DAYS_ARRAY, String.valueOf(schedule.getDays()));

        // updating row
        return db.update(Schedule.TABLE_NAME, values, Schedule.COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
    }

    public int updateScheduleSwitch(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Schedule.COLUMN_ON, schedule.isOn());

        // updating row
        return db.update(Schedule.TABLE_NAME, values, Schedule.COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
    }

    public int updateScheduleTime(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, schedule.getTime());

        // updating row
        return db.update(Schedule.TABLE_NAME, values, Schedule.COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
    }

    public void deleteSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Schedule.TABLE_NAME, Schedule.COLUMN_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
        db.close();
    }
}