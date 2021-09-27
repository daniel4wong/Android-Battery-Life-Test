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
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = AppDatabase.class.getSimpleName();

    public static String DB_NAME = "AndroidBatteryLifeTest.db";
    protected Context context;
    public abstract BatteryHistoryDao batteryHistoryDao();
    public abstract TestHistoryDao testHistoryDao();

    public static void init(Context context) {
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

    public void copyDatabase() {
        synchronized (AppDatabase.class) {
            try {
                File extDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

                if (extDir.canWrite()) {
                    File currentDbFile = new File(getPath());
                    File extDbFile = new File(extDir, DB_NAME);

                    if (extDbFile.exists()) {
                        extDbFile.delete();
                    }
                    if (currentDbFile.exists()) {
                        FileChannel src = new FileInputStream(currentDbFile).getChannel();
                        FileChannel dst = new FileOutputStream(extDbFile).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        MediaScannerConnection.scanFile(context,
                                new String[] { extDbFile.getAbsolutePath() },
                                new String[] { "application/x-sqlite3" },
                                new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {

                            }

                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                                Log.i(TAG, extDbFile.getAbsolutePath());
                                MainApplication.currentActivity.runOnUiThread(() ->
                                        Toast.makeText(context, R.string.msg_database_downloaded_success, Toast.LENGTH_LONG).show()
                                );
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Fail to write database.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
