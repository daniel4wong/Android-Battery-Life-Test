package com.daniel4wong.AndroidBatteryLifeTest.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.InputButtonHelper;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.FragmentChartBinding;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.LineChartHelper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
                binding.buttonPickTime
        };
        InputButtonHelper.prepareInputs(views);

        lineChartHelper = new LineChartHelper();
        lineChartHelper.initChart(getActivity(), binding.lineChart);
        lineChartHelper.startDataTimer();

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