package com.dipak.calendardemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dipak on 11/9/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MenuManager";
    private static final String TABLE_MENU = "menu";

    private static final String KEY_RICE = "rice";
    private static final String KEY_VEGIE = "vegie";
    private static final String KEY_SPECIAL = "special";
    private static final String KEY_OTHER = "other";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + KEY_RICE + " TEXT,"
                + KEY_VEGIE + " TEXT,"
                + KEY_SPECIAL + " TEXT,"
                + KEY_OTHER + " TEXT" + ")";
        db.execSQL(CREATE_MENU_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
