package com.example.pruebamedicamento;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "points.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_POINTS = "points";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TITLE = "title";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_POINTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                    COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                    COLUMN_TITLE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        onCreate(db);
    }

    // Método para insertar un punto
    public long insertPoint(double latitude, double longitude, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TITLE, title);
        return db.insert(TABLE_POINTS, null, values);
    }

    // Método para obtener todos los puntos
    public List<PointModel> getAllPoints() {
        List<PointModel> points = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_POINTS,
                new String[]{COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TITLE},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") PointModel point = new PointModel(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                );
                points.add(point);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return points;
    }
}