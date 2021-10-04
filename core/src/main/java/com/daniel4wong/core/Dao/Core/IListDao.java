package com.daniel4wong.core.Dao.Core;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.daniel4wong.core.Dao.Data.ICriteriaFactory;

import java.util.List;

public interface IListDao<T, TDisplay extends T> {

    T getRecord(ICriteriaFactory criteria);
    List<T> getRecordList(ICriteriaFactory criteria);
    TDisplay getRecordDisplay(ICriteriaFactory criteria);
    List<TDisplay> getRecordDisplayList(ICriteriaFactory criteria);

    Integer countRecordList(ICriteriaFactory criteria);
    Integer countRecordDisplayList(ICriteriaFactory criteria);

    T queryForObject(SimpleSQLiteQuery query, ICriteriaFactory criteria);
    List<T> queryForList(SimpleSQLiteQuery query, ICriteriaFactory criteria);
}

