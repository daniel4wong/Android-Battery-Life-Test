package com.melsontech.batterytest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.melsontech.batterytest.core.typeConverter.DateTypeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "BTRY_TEST", inheritSuperIndices = true)
public class BatteryTest {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_TEST_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @NotNull
    @ColumnInfo(name = "SSN_ID")
    public String sessionId;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "BGN_TS")
    public Date bgnTs;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "END_TS")
    public Date endTs;
}
