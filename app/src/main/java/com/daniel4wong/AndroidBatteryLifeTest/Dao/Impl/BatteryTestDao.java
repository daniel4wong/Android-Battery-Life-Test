package com.daniel4wong.AndroidBatteryLifeTest.Dao.Impl;

import com.daniel4wong.core.Dao.Core.AbstractBaseModelRepositoryDao;
import com.daniel4wong.AndroidBatteryLifeTest.Dao.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.*;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Display.*;

public abstract class BatteryTestDao
        extends AbstractBaseModelRepositoryDao<BatteryTest, BatteryTestDisplay>
        implements IBatteryTestDao {
}