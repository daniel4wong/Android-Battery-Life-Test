package com.daniel4wong.core.Db;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public abstract class BaseDatabase extends RoomDatabase {
    private static final String TAG = BaseDatabase.class.getSimpleName();

    protected abstract String getDbName();
    protected Context context;

    public void removeDb(Context context) {
        context.deleteDatabase(getDbName());
    }

    public String getPath() {
        SupportSQLiteDatabase db = getOpenHelper().getWritableDatabase();
        String path = db.getPath();
        Log.d(TAG, path);

        return path;
    }

    public void copyDatabase(Activity activity, String message) {
        synchronized (BaseDatabase.class) {
            try {
                File extDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

                if (extDir.canWrite()) {
                    File currentDbFile = new File(getPath());
                    File extDbFile = new File(extDir, getDbName());

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
                                        activity.runOnUiThread(() ->
                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
