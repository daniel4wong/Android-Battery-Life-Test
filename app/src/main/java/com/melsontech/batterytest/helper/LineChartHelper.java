package com.melsontech.batterytest.helper;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class LineChartHelper {
    private static final String TAG = LineChartHelper.class.getName();

    private int LineColor = Color.RED;
    private Activity activity;
    private LineChart chart;

    public void initChart(Activity activity, LineChart chart) {
        this.activity = activity;
        this.chart = chart;

        int textColor = ColorTemplate.getHoloBlue();
        int lineColor = LineColor;
        float textSize = 10f;
        float padding = 5f;
        float[] xRange = { 0f, 60f, 5f };
        float[] yRange = { 00f, 100f, 10f };
        boolean[] gridlines = { false, true };

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setExtraOffsets(padding, padding, padding, padding);

        Description description = chart.getDescription();
        description.setText("Power percentage over recent hour");

        LegendEntry legendEntry = new LegendEntry("minutes", Legend.LegendForm.LINE, 0f, 0f, null, lineColor);
        Legend legend = chart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setCustom(new LegendEntry[] {legendEntry});

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        xAxis.setTextColor(textColor);
        xAxis.setTextSize(textSize);
        xAxis.setDrawGridLines(gridlines[0]);
        xAxis.setAxisMinimum(xRange[0]);
        xAxis.setAxisMaximum(xRange[1]);
        xAxis.setLabelCount((int)(xRange[1]/xRange[2]));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTypeface(Typeface.DEFAULT_BOLD);
        yAxis.setTextColor(textColor);
        yAxis.setTextSize(textSize);
        yAxis.setDrawGridLines(gridlines[1]);
        yAxis.setAxisMinimum(yRange[0]);
        yAxis.setAxisMaximum(yRange[1]);
        yAxis.setLabelCount((int)(yRange[1]/yRange[2]));

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
        dataSet.setLineWidth(2f);
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

}
