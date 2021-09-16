package com.melsontech.batterytest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "BTRY_TEST_CNFG_ITEM", inheritSuperIndices = true)
public class BatteryTestConfigItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_TEST_CNFG_ITEM_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @NotNull
    @ColumnInfo(name = "ITEM_CODE")
    public String itemCode;

    @NotNull
    @ColumnInfo(name = "ITEM_VAL")
    public String itemValue;

    @NotNull
    @ColumnInfo(name = "ITEM_VAL_TYPE")
    public String itemValueType;
}
