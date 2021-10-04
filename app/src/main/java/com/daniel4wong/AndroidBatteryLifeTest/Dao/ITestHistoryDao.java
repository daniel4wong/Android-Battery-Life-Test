package com.daniel4wong.AndroidBatteryLifeTest.Dao;

import androidx.room.Dao;

import com.daniel4wong.core.Dao.Core.IWritableRepositoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Display.*;

@Dao
public interface ITestHistoryDao
        extends IWritableRepositoryDao<TestHistory, TestHistoryDisplay> {
}
