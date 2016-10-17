package com.franci22.qrtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "QRTools";
    public static final String TABLE_NAME = "scannedQR";
    public static final int VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String text = "text";
    public static final String date = "date";
    public static final String format = "format";
    public static final String SCRIPT = "create table " + TABLE_NAME + " ("
            + KEY_ID + " integer primary key autoincrement, " + text
            + " text not null, " + date + " text not null, " + format + " text not null );";

    public DBHelper(Context context, String name,
                    CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

}
