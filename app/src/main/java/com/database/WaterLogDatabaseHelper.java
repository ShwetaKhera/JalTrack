package com.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WaterLogDatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "notes_db";


    public WaterLogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Log.CREATE_TABLE_WATERLOG);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Log.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertLog(int amount) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Log.COLUMN_AMOUNT, amount);

        // insert row
        long id = db.insert(Log.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Log getLog(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Log.TABLE_NAME,
                new String[]{Log.COLUMN_ID, Log.COLUMN_AMOUNT, Log.COLUMN_TIMESTAMP},
                Log.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Log log = new Log(
                cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return log;
    }

    public List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Log.TABLE_NAME + " ORDER BY " +
                Log.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.setId(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)));
                log.setAmount(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)));
                log.setTimestamp(cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

                logs.add(log);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return logs;
    }

    public List<Log> getTodayLogs() {
        List<Log> logs = new ArrayList<>();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date date = new Date();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Log.TABLE_NAME
                + " WHERE " + Log.COLUMN_TIMESTAMP + " >= date('" + fmt.format(date) + "') "
                + " ORDER BY " + Log.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.setId(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)));
                log.setAmount(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)));
                log.setTimestamp(cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

                logs.add(log);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return logs;
    }

    public List<Log> getMonthlyLogs(String monthAndYear) {
        List<Log> logs = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Log.TABLE_NAME
                + " WHERE " + "strftime('%Y-%m', " + Log.COLUMN_TIMESTAMP + ") = '" + monthAndYear + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.setId(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)));
                log.setAmount(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)));
                log.setTimestamp(cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

                logs.add(log);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return logs;
    }

    public List<Log> getWeeklyLogs(Date start, Date end) {
        List<Log> logs = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStart = simpleDateFormat.format(start);
        String dateEnd = simpleDateFormat.format(end);

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Log.TABLE_NAME
                + " WHERE " + Log.COLUMN_TIMESTAMP
                + " BETWEEN '" + dateStart + "' AND '" + dateEnd + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.setId(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)));
                log.setAmount(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)));
                log.setTimestamp(cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

                logs.add(log);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return logs;
    }

    public List<Log> getYearlyLogs(int year) {
        List<Log> logs = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Log.TABLE_NAME
                + " WHERE " + "strftime('%Y', " + Log.COLUMN_TIMESTAMP + ") = '" + year + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log log = new Log();
                log.setId(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_ID)));
                log.setAmount(cursor.getInt(cursor.getColumnIndex(Log.COLUMN_AMOUNT)));
                log.setTimestamp(cursor.getString(cursor.getColumnIndex(Log.COLUMN_TIMESTAMP)));

                logs.add(log);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return logs;
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public int getLogsCount() {
        String countQuery = "SELECT  * FROM " + Log.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateLog(Log log) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Log.COLUMN_AMOUNT, log.getAmount());
        values.put(Log.COLUMN_TIMESTAMP, log.getTimestamp());

        // updating row
        return db.update(Log.TABLE_NAME, values, Log.COLUMN_ID + " = ?",
                new String[]{String.valueOf(log.getId())});
    }

    public void deleteLog(Log log) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Log.TABLE_NAME, Log.COLUMN_ID + " = ?",
                new String[]{String.valueOf(log.getId())});
        db.close();
    }

    public void deleteAllLog() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Log.TABLE_NAME, null, null);
        db.close();
    }
}