package com.melsontech.batterytest.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.melsontech.batterytest.core.typeConverter.DateTypeConverter;
import com.melsontech.batterytest.model.BatteryHistory;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface BatteryHistoryDao {

    @Query("SELECT * FROM BTRY_HIST")
    Single<List<BatteryHistory>> getAll();

    @TypeConverters({DateTypeConverter.class})
    @Query("SELECT * FROM BTRY_HIST WHERE LOG_TS >= :fromDate AND LOG_TS < :toDate")
    Single<List<BatteryHistory>> getList(Date fromDate, Date toDate);

    @Insert
    void insert(BatteryHistory model);

}
