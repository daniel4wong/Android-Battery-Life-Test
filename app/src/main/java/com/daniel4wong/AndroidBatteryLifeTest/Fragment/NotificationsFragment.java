package com.daniel4wong.AndroidBatteryLifeTest.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daniel4wong.AndroidBatteryLifeTest.Adapter.LogAdapter;
import com.daniel4wong.core.Dao.Data.CriteriaMap;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Model.TestHistory;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private ListView listView;
    private LogAdapter logAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<TestHistory> testHistories = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.listViewLog;
        logAdapter = new LogAdapter(getContext(), testHistories);
        listView.setAdapter(logAdapter);

        swipeRefreshLayout = binding.layoutSwipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void refreshData() {
        testHistories = AppDatabase.getInstance().testHistoryDao().getRecordList(CriteriaMap.newCriteria());
        Collections.sort(testHistories, (testHistory, t1) -> t1.logTs.compareTo(testHistory.logTs));
        logAdapter.clear();
        logAdapter.addAll(testHistories);
        swipeRefreshLayout.setRefreshing(false);
    }
}