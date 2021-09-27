package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;

import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.internal.util.BlockingIgnoringReceiver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LineChartHelper {
    private static final String TAG = LineChartHelper.class.getSimpleName();

    private int LineColor = Color.RED;
    private Activity activity;
    private LineChart chart;
    private Timer timer;

    private Date bgnTime;
    private Date endTime;

    public void initChart(Activity activity, LineChart chart) {
        this.activity = activity;
        this.chart = chart;

        int textColor = ColorTemplate.getHoloBlue();
        int lineColor = LineColor;
        float textSize = 12f;
        float padding = 5f;
        float[] xRange = { 0f, 60f, 5f };
        float[] yRange = { 00f, 100f, 10f };
        boolean[] gridlines = { true, true };

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setExtraOffsets(padding, padding, padding, padding);

        Description description = chart.getDescription();
        description.setText(activity.getString(R.string.chart_description));
        description.setTextSize(textSize);

        LegendEntry legendEntry = new LegendEntry(activity.getString(R.string.chart_minutes), Legend.LegendForm.LINE, 0f, 0f, null, lineColor);
        Legend legend = chart.getLegend();
        legend.setTextSize(textSize);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setCustom(new LegendEntry[] {legendEntry});

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setTextColor(textColor);
        xAxis.setTextSize(textSize);
        xAxis.setDrawGridLines(gridlines[0]);
        xAxis.setAxisMinimum(xRange[0]);
        xAxis.setAxisMaximum(xRange[1]);
        xAxis.setLabelCount((int)(xRange[1] / xRange[2]));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTypeface(Typeface.DEFAULT);
        yAxis.setTextColor(textColor);
        yAxis.setTextSize(textSize);
        yAxis.setDrawGridLines(gridlines[1]);
        yAxis.setAxisMinimum(yRange[0]);
        yAxis.setAxisMaximum(yRange[1]);
        yAxis.setLabelCount((int)(yRange[1] / yRange[2]));

        chart.getAxisRight().setEnabled(false);
    }

    public void setData(List<Pair<Integer, Integer>> data) {
        List<Entry> values = new ArrayList<>();
        data.stream().forEach(i -> {
            values.add(new Entry(i.first, i.second));
        });
        Log.i(TAG, String.format("Got data: %d", values.size()));

        LineDataSet dataSet = new LineDataSet(values, "DataSet");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(LineColor);
        dataSet.setLineWidth(1.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighLightColor(Color.GREEN);

        activity.runOnUiThread(() -> {
            chart.setData(new LineData(dataSet));
            chart.notifyDataSetChanged();
            chart.invalidate();
        });
    }

    public Timer startDataTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshData();
            }
        }, 1000, 5000);

        this.timer = timer;
        return timer;
    }

    public void stopDataTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void refreshData() {
        Date now = new Date();
        Date fromDate = new Date(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.HOUR, 1);
        Date toDate = calendar.getTime();

        if (bgnTime != null && endTime != null) {
            fromDate = bgnTime;
            toDate = endTime;
        }

        Description description = chart.getDescription();
        description.setText(String.format("%s (%tT)", activity.getString(R.string.chart_description), fromDate));

        Log.d(TAG, String.format("Query data: from %tT to %tT", fromDate, toDate));
        AppDatabase.getInstance().batteryHistoryDao().getList(fromDate, toDate)
                .subscribeOn(Schedulers.computation())
                .doOnError(new BlockingIgnoringReceiver())
                .subscribe(models -> {
                    List<Pair<Integer, Integer>> data = new ArrayList<>();
                    models.forEach(i -> {
                        if (data.stream().anyMatch(d -> d.first == i.logTs.getMinutes()))
                            return;
                        if (i.btryLvl < 0)
                            return;
                        data.add(new Pair<>(i.logTs.getMinutes(), i.btryLvl.intValue()));
                    });
                    setData(data);
                }, e -> System.out.println("RoomWithRx: " + e.getMessage()));
    }

    public void setTimeRange(Date bgnTime, Date endTime) {
        this.bgnTime = bgnTime;
        this.endTime = endTime;
        refreshData();
    }
}
