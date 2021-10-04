package com.daniel4wong.AndroidBatteryLifeTest.Model;

import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.daniel4wong.core.Model.Annotation.RuntimeColumnInfo;
import com.daniel4wong.core.Model.Annotation.RuntimeEntity;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.EntityTable;
import com.daniel4wong.core.Model.Core.BaseModel;
import com.daniel4wong.core.Model.TypeConverter.DateTypeConverter;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

@RuntimeEntity(tableName = EntityTable.BTRY_HIST)
@Entity(tableName = EntityTable.BTRY_HIST)
public class BatteryHistory extends BaseModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_HIST_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @Size(32)
    @ColumnInfo(name = "SSN_ID", typeAffinity = ColumnInfo.TEXT, collate = ColumnInfo.NOCASE)
    @RuntimeColumnInfo(name = "SSN_ID")
    public String sessionId;

    @NotNull
    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "LOG_TS")
    @RuntimeColumnInfo(name = "LOG_TS")
    public Date logTs;

    @NotNull
    @ColumnInfo(name = "BTRY_LVL", typeAffinity = ColumnInfo.INTEGER)
    @RuntimeColumnInfo(name = "BTRY_LVL")
    public Long btryLvl;

    @NotNull
    @ColumnInfo(name = "BTRY_SCL", typeAffinity = ColumnInfo.INTEGER)
    @RuntimeColumnInfo(name = "BTRY_SCL")
    public Long btryScl;

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
