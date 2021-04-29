package com.acfm.ble_transform.SQLiteUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String  DB_NAME="test.db";
    public static final int     DB_VERSION=1;
    private Context             context;

    public SQLiteHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String createTable_BT_information="create table BT_information ( MacAddress varchar(100), Content varchar(100))";
        String createTable = "create table repeater (id integer primary key autoincrement,repeaterId text,high text,temperate text,power text,worktime text,time integer)";
        String safetyHat = "create table safetyHat (id integer primary key autoincrement,hatId text,MAC text,temperature text,high text,power text,time integer,status text,humidity text)";

        Log.d("建表","两张表");
        //db.execSQL(createTable_BT_information);
        db.execSQL(createTable);
        db.execSQL(safetyHat);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
