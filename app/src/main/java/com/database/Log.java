package com.database;

public class Log {
    public static final String TABLE_NAME = "WATERLOG";

    // Table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TIMESTAMP = "date";

    private int id;
    private int amount;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE_WATERLOG =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_AMOUNT + " INTEGER NOT NULL, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Log() {
    }

    public Log(int id, int amount, String timestamp) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

