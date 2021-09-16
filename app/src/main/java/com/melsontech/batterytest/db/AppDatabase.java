package com.melsontech.batterytest.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.amitshekhar.DebugDB;
import com.melsontech.batterytest.dao.*;
import com.melsontech.batterytest.model.*;

@Database(entities =  {BatteryHistory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = AppDatabase.class.getName();

    public static String DB_NAME = "app-db";
    protected Context context;
    public abstract BatteryHistoryDao batteryHistoryDao();

    public static void init(Context context) {
        //removeDb(context);

        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();

            instance.context = context;

            String log = DebugDB.getAddressLog();
            Log.d(TAG, log);
        }
    }

    private static AppDatabase instance;
    public static AppDatabase getInstance() {
        return instance;
    }

    public static void removeDb(Context context) {
        context.deleteDatabase(DB_NAME);
    }

    public String getPath() {
        SupportSQLiteDatabase db = getOpenHelper().getWritableDatabase();
        String path = db.getPath();
        Log.d(TAG, path);

        return path;
    }
}
