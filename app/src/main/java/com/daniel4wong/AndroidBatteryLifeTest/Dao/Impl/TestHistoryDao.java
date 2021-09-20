package com.daniel4wong.AndroidBatteryLifeTest.Dao.Impl;

import androidx.room.Dao;

import com.daniel4wong.AndroidBatteryLifeTest.Dao.Core.AbstractBaseModelRepositoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Display.*;

@Dao
public abstract class TestHistoryDao
        extends AbstractBaseModelRepositoryDao<TestHistory, TestHistoryDisplay>
        implements ITestHistoryDao {
}