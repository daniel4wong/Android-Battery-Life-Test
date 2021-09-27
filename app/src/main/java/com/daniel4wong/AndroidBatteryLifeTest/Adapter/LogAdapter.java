package com.daniel4wong.AndroidBatteryLifeTest.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daniel4wong.AndroidBatteryLifeTest.Model.TestHistory;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class LogAdapter extends ArrayAdapter<TestHistory> {
    public LogAdapter(Context context, List<TestHistory> items) {
        super(context, 0, items);
    }

    @SuppressLint("SimpleDateFormat")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TestHistory testHistory = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_test_history, parent, false);
        }

        TextView name = convertView.findViewById(R.id.textViewName);
        TextView description = convertView.findViewById(R.id.textviewDescription);

        name.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(testHistory.logTs) + " [ " + testHistory.type + " ]");
        description.setText(testHistory.dataText);

        return convertView;
    }
}
