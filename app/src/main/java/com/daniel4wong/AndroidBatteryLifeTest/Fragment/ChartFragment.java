package com.daniel4wong.AndroidBatteryLifeTest.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.core.Helper.FormatHelper;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.FragmentChartBinding;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.LineChartHelper;
import com.daniel4wong.core.Ui.InputButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

public class ChartFragment extends Fragment {

    private FragmentChartBinding binding;

    private LineChartHelper lineChartHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View[] views = {
                binding.switchRecordBattery,
                binding.textViewDate,
                binding.textViewTime,
                binding.buttonPickDate,
                binding.buttonPickTime,
                binding.radioGroupChartType
        };
        InputButton.prepareInputs(views);

        lineChartHelper = new LineChartHelper();
        lineChartHelper.initChart(getActivity(), binding.lineChart);
        lineChartHelper.startDataTimer();

        binding.buttonSearch.setOnClickListener(view -> {
            String dateText = String.format("%s %s", binding.textViewDate.getText(), binding.textViewTime.getText());
            Date bgnDate = FormatHelper.stringToDate(dateText);
            if (bgnDate == null)
                return;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bgnDate);
            calendar.add(Calendar.HOUR, 1);
            Date endDate = calendar.getTime();
            lineChartHelper.setTimeRange(bgnDate, endDate);
        });

        binding.buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreference.getInstance().savePreference(binding.buttonPickDate, "");
                AppPreference.getInstance().savePreference(binding.buttonPickTime, "");
                lineChartHelper.setTimeRange(null, null);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        lineChartHelper.stopDataTimer();
    }
}