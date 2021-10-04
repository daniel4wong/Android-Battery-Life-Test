package com.daniel4wong.AndroidBatteryLifeTest.Dao.Impl;

import androidx.room.Dao;

import com.daniel4wong.core.Dao.Core.AbstractBaseModelRepositoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Display.*;

@Dao
public abstract class BatteryHistoryDao
        extends AbstractBaseModelRepositoryDao<BatteryHistory, BatteryHistoryDisplay>
        implements IBatteryHistoryDao {
}
