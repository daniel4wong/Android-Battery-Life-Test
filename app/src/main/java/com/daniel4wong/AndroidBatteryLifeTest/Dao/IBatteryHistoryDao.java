package com.daniel4wong.AndroidBatteryLifeTest.Dao;

import androidx.room.Query;
import androidx.room.TypeConverters;

import com.daniel4wong.AndroidBatteryLifeTest.Core.TypeConverter.DateTypeConverter;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.Core.IWritableRepositoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Display.*;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface IBatteryHistoryDao
        extends IWritableRepositoryDao<BatteryHistory, BatteryHistoryDisplay> {

    @TypeConverters({DateTypeConverter.class})
    @Query("SELECT * FROM BTRY_HIST WHERE LOG_TS >= :fromDate AND LOG_TS < :toDate")
    Single<List<BatteryHistory>> getList(Date fromDate, Date toDate);
}
