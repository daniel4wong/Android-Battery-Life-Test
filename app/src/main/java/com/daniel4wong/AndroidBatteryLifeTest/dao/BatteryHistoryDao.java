package com.daniel4wong.AndroidBatteryLifeTest.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.daniel4wong.AndroidBatteryLifeTest.core.typeConverter.DateTypeConverter;
import com.daniel4wong.AndroidBatteryLifeTest.model.BatteryHistory;

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
