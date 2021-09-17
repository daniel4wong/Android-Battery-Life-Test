package com.daniel4wong.AndroidBatteryLifeTest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.daniel4wong.AndroidBatteryLifeTest.core.typeConverter.DateTypeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "BTRY_HIST", inheritSuperIndices = true)
public class BatteryHistory {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_HIST_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @NotNull
    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "LOG_TS")
    public Date logTs;

    @NotNull
    @ColumnInfo(name = "BTRY_LVL")
    public Long level;

    @NotNull
    @ColumnInfo(name = "BTRY_SCL")
    public Long scale;

    @ColumnInfo(name = "SSN_ID")
    public String sessionId;

    public BatteryHistory(Date logTs, Long level, Long scale) {
        this.logTs = logTs;
        this.level = level;
        this.scale = scale;
    }
}
