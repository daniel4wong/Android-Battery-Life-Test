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

import java.io.Serializable;
import java.util.Date;

@RuntimeEntity(tableName = EntityTable.BTRY_TEST)
@Entity(tableName = EntityTable.BTRY_TEST)
public class BatteryTest extends BaseModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BTRY_TEST_KEY", typeAffinity = ColumnInfo.INTEGER)
    public Long id;

    @Size(32)
    @ColumnInfo(name = "SSN_ID", typeAffinity = ColumnInfo.TEXT, collate = ColumnInfo.NOCASE)
    @RuntimeColumnInfo(name = "SSN_ID")
    public String sessionId;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "BGN_TS")
    @RuntimeColumnInfo(name = "BGN_TS")
    public Date bgnTs;

    @TypeConverters({DateTypeConverter.class})
    @ColumnInfo(name = "END_TS")
    @RuntimeColumnInfo(name = "END_TS")
    public Date endTs;

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
