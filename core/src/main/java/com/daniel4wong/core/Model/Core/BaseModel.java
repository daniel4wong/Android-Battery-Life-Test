package com.daniel4wong.core.Model.Core;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.daniel4wong.core.Model.Constant.EntityTableColumn;
import com.daniel4wong.core.Model.TypeConverter.DateTypeConverter;

import java.util.Calendar;
import java.util.Date;

@Entity
public abstract class BaseModel implements IBaseModel {

    public static final String FLAG_INSERT = "I";
    public static final String FLAG_UPDATE = "U";
    public static final String FLAG_DELETE = "D";

    @Ignore
    public transient Long key;
    public abstract Long getKey();
    public abstract void setKey(Long key);

    @NonNull
    @Size(1)
    @ColumnInfo(name = EntityTableColumn.LAST_REC_TXN_TYPE_CODE, typeAffinity = ColumnInfo.TEXT)
    public String lastRecTxnTypeCode = BaseModel.FLAG_INSERT;

    @NonNull
    @ColumnInfo(name = EntityTableColumn.LAST_REC_TXN_DATE)
    @TypeConverters({DateTypeConverter.class})
    public Date lastRecTxnDate = Calendar.getInstance().getTime();

    @SuppressLint("Range")
    @NonNull
    @Size(20)
    @ColumnInfo(name = EntityTableColumn.LAST_REC_TXN_USER_ID)
    public String lastRecTxnUserId = "SYSTEM";

    @Ignore
    private transient String editFlag;
    protected BaseModel()
    {
        this.editFlag = BaseModel.FLAG_INSERT;
    }

    protected void doMarkInsert() {
        setKey(null);
    }
    public void markInsert() {
        doMarkInsert();
        this.editFlag = FLAG_INSERT;
    }
    public void markUpdate() {
        if (this.editFlag.indexOf(FLAG_UPDATE) > 0) return;
        this.editFlag = this.editFlag + FLAG_UPDATE;
    }
    public void markDelete() {
        if (this.editFlag.indexOf(FLAG_DELETE) > 0) return;
        this.editFlag = this.editFlag + FLAG_DELETE;
    }
    public void markClean()
    {
        this.editFlag = "";
    }

    protected boolean isLoadFromDb() {
        return this.key != null && this.key > 0;
    }
    public boolean isDeleted() {
        return this.isLoadFromDb() && this.editFlag.contains(FLAG_DELETE);
    }
    public boolean isUpdated() {
        return this.isLoadFromDb() && this.editFlag.contains(FLAG_UPDATE) && !this.editFlag.contains(FLAG_DELETE);
    }
    public boolean isNew() {
        return !this.isLoadFromDb() && this.editFlag.contains(FLAG_INSERT) && !this.editFlag.contains(FLAG_DELETE);
    }
}
