package com.daniel4wong.AndroidBatteryLifeTest.Database;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.amitshekhar.DebugDB;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.Impl.BatteryHistoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.Impl.TestHistoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.core.Db.BaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@Database(entities =  {
        BatteryHistory.class, TestHistory.class,
        BatteryTest.class,
        BatteryTestConfig.class, BatteryTestConfigItem.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends BaseDatabase {
    private static final String TAG = AppDatabase.class.getSimpleName();
    private static String DB_NAME = "AndroidBatteryLifeTest.db";

    public abstract BatteryHistoryDao batteryHistoryDao();
    public abstract TestHistoryDao testHistoryDao();

    private static AppDatabase instance;
    public static AppDatabase getInstance() {
        return instance;
    }

    public static void init(Context context, String name) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

            Log.i(TAG, DebugDB.getAddressLog());
            Log.i(TAG, "Run: adb forward tcp:8080 tcp:8080");
        }
        instance.context = context;
    }

    @Override
    protected String getDbName() {
        return DB_NAME;
    }
}
