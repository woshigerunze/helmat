package com.acfm.ble_transform.SQLiteUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONException;
import org.json.JSONObject;

public class SqliteDao {
    private final SQLiteHelper mHelper;

    public SqliteDao(Context context){
        mHelper = new SQLiteHelper(context);
    }
    //repeaterId text,high text,temperate text,power text,worktime text
    public void insertrepeater(String repeaterId,String high,String temperate,String worktime,long time)
    {
        SQLiteDatabase db=mHelper.getWritableDatabase();
        String sql ="insert into repeater (repeaterId,high,temperate,worktime,time) values(?,?,?,?,?)";
        db.execSQL(sql,new Object[]{repeaterId,high,temperate,worktime,time});
        db.close();
    }
    public void updatebyrepeaterid(String repeaterId,String high,String temperate,String worktime,long time)
    {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update repeater set high = ?,temperate =?,worktime =?,time =? where repeaterId = ?";
        db.execSQL(sql,new Object[]{repeaterId,high,temperate,worktime,time});
        db.close();
    }
    public JSONObject findbyrepeaterid(String repeaterId) throws JSONException
    {
        JSONObject jsonObject   = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from repeater where repeaterId = '"+repeaterId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high"))== null?"":cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature"))== null?"":cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("worktime",cursor.getString(cursor.getColumnIndex("worktime"))== null?"":cursor.getString(cursor.getColumnIndex("worktime")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            return jsonObject;
        }else{
            db.close();
            return null;
        }

    }
    public JSONObject findbyrepeater(int id) throws JSONException {
        JSONObject jsonObject   = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from repeater where id = "+id;
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            jsonObject.put("repeaterId",cursor.getString(cursor.getColumnIndex("repeaterId")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high"))== null?"":cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature"))== null?"":cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("worktime",cursor.getString(cursor.getColumnIndex("worktime"))== null?"":cursor.getString(cursor.getColumnIndex("worktime")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            return jsonObject;
        }else{
            db.close();
            return null;
        }

    }



    public void insert(String hatId,String MAC,String temperature,String high,String power,long time,String status,String humidity){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "insert into safetyHat (hatId,MAC,temperature,high,power,time,status,humidity) values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql,new Object[]{hatId,MAC,temperature,high,power,time,status,humidity});
        db.close();
    }


    public void updateByMac(String hatId,String MAC,String temperature,String high,String power,long time,String status,String humidity){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update safetyHat set hatId = ?,temperature = ?,high = ?,power = ?,time = ?,status = ?,humidity = ? where MAC = ?";
        db.execSQL(sql,new Object[]{hatId,temperature,high,power,time,status,humidity,MAC});
        db.close();
    }

    public void updateByHatId(String hatId, String temperature, String high, String power, long time, String status, String humidity){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update safetyHat set temperature = ?,high = ?,power = ?,time = ?,status = ?,humidity = ? where hatId = ?";
        db.execSQL(sql,new Object[]{temperature,high,power,time,status,humidity,hatId});
        db.close();
    }

    public JSONObject findById(int id) throws JSONException {
        JSONObject jsonObject   = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where id = "+id;
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")) == null?"":cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")) == null?"":cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")) == null?"":cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")) == null?"":cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")) == null?"":cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac",cursor.getString(cursor.getColumnIndex("MAC")));
            jsonObject.put("hatId",cursor.getString(cursor.getColumnIndex("hatId")) == null?"":cursor.getString(cursor.getColumnIndex("hatId")));
            db.close();
            return jsonObject;
        }else{
            db.close();
            return null;
        }
    }
    public JSONObject findByHatId(String hatId) throws JSONException {
        JSONObject jsonObject   = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where hatId = '"+hatId+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")) == null?"":cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")) == null?"":cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")) == null?"":cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")) == null?"":cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")) == null?"":cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac",cursor.getString(cursor.getColumnIndex("MAC")));
            db.close();
            return jsonObject;
        }else{
            db.close();
            return null;
        }
    }

    public JSONObject findByHatMac(String Mac) throws JSONException {
        JSONObject jsonObject   = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where MAC = '"+Mac+"'";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")) == null?"":cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")) == null?"":cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")) == null?"":cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")) == null?"":cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")) == null?"":cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("hatId",cursor.getString(cursor.getColumnIndex("hatId")) == null?"":cursor.getString(cursor.getColumnIndex("hatId")));

            db.close();
            return jsonObject;
        }else{
            db.close();
            return null;
        }
    }

}
