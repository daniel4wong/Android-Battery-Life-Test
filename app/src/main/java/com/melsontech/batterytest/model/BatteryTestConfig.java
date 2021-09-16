package com.melsontech.batterytest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.melsontech.batterytest.core.typeConverter.DateTypeConverter;

import java.util.Date;
import java.util.List;

@Entity(tableName = "BTRY_TEST_CNFG", inheritSuperIndices = true)
public class BatteryTestConfig {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_TEST_CNFG_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "BGN_TS")
    public Date bgnTs;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "END_TS")
    public Date endTs;

    public List<BatteryTestConfigItem> batteryTestItemList;
}
