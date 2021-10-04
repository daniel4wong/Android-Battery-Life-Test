package com.daniel4wong.core.Dao.Core;

public interface IRepositoryDao<T, TDisplay extends T>
        extends IListDao<T, TDisplay> {

    T getRecordById(long id);
    TDisplay getRecordDisplayById(long id);
}
