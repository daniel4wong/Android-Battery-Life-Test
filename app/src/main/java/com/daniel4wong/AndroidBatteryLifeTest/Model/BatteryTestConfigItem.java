package com.daniel4wong.AndroidBatteryLifeTest.Model;

import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.daniel4wong.core.Model.Annotation.RuntimeColumnInfo;
import com.daniel4wong.core.Model.Annotation.RuntimeEntity;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.EntityTable;
import com.daniel4wong.core.Model.Core.BaseModel;

import java.io.Serializable;

@RuntimeEntity(tableName = EntityTable.BTRY_TEST_CNFG_ITEM)
@Entity(tableName = EntityTable.BTRY_TEST_CNFG_ITEM)
public class BatteryTestConfigItem extends BaseModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_TEST_CNFG_ITEM_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @Size(20)
    @ColumnInfo(name = "ITEM_CODE", typeAffinity = ColumnInfo.TEXT, collate = ColumnInfo.NOCASE)
    @RuntimeColumnInfo(name = "ITEM_CODE")
    public String itemCode;

    @Size(100)
    @ColumnInfo(name = "ITEM_VAL", typeAffinity = ColumnInfo.TEXT, collate = ColumnInfo.NOCASE)
    public String itemValue;

    @Size(20)
    @ColumnInfo(name = "ITEM_VAL_TYPE", typeAffinity = ColumnInfo.TEXT, collate = ColumnInfo.NOCASE)
    public String itemValueType;

    @Override
    public Long getKey() {
        return id;
    }
    @Override
    public void setKey(Long key) {
        this.key = key;
        this.id = key;
    }
}
