package com.daniel4wong.AndroidBatteryLifeTest.Dao.Core;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

public interface IWritableRepositoryDao<T, TDisplay extends T>
        extends IRepositoryDao<T, TDisplay> {

    @Insert
    long insertRecord(T model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdateRecord(T model);

    @Update
    int updateRecord(T model);

    @Update
    int deleteRecord(T model);

    @Delete
    int hardDeleteRecord(T model);
}