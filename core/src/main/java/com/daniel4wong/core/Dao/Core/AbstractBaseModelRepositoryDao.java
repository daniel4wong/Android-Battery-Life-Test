package com.daniel4wong.core.Dao.Core;

import android.util.Pair;

import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.daniel4wong.core.Dao.Data.ICriteriaFactory;
import com.daniel4wong.core.Model.Annotation.RuntimeColumnInfo;
import com.daniel4wong.core.Model.Annotation.RuntimeEntity;
import com.daniel4wong.core.Model.Constant.EntityTableColumn;
import com.daniel4wong.core.Model.Core.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractBaseModelRepositoryDao<T extends BaseModel, TDisplay extends T>
        implements IRepositoryDao<T,TDisplay> {

    @RawQuery
    protected abstract List<T> selectList(SupportSQLiteQuery query);
    @RawQuery
    protected abstract List<TDisplay> selectDisplayList(SupportSQLiteQuery query);
    @RawQuery
    protected abstract Integer countList(SupportSQLiteQuery query);
    @RawQuery
    protected abstract Integer countDisplayList(SupportSQLiteQuery query);


    protected String getTableName() {
        Class entityClass = (Class)((ParameterizedType)getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
        RuntimeEntity annotation = (RuntimeEntity)entityClass.getAnnotation(RuntimeEntity.class);
        return annotation.tableName();
    }

    protected String getTableAlias() {
        String alias = "";
        String[] names = getTableName().split("_");
        for (int i=0; i< names.length; i++) {
            alias += names[i] != null && names[i] != "" ? names[i].charAt(0) : "";
        }
        return alias.toLowerCase();
    }

    protected String getPrimaryKey() {
        return getTableName()
                .replace("REF_", "")
                .replace("CHS_", "")
                .replace("SRF_", "") + "_KEY";
    }

    public String getDisplayColumns() {
        return "";
    }
    public String getDisplayJoinSql() {
        return "";
    }

    protected String getSelectFormatSql(boolean isDisplay) {
        String tableName = getTableName();
        String tableAlias = getTableAlias();

        String sql =
                "select " + tableAlias  +  ".* " + (isDisplay ? getDisplayColumns() : "") +
                        "from " + tableName + " " + tableAlias + " "
                        + (isDisplay ? getDisplayJoinSql() : "") +
                        "where " + tableAlias + "." + EntityTableColumn.LAST_REC_TXN_TYPE_CODE + " <> 'D' ";

        return sql;
    }

    protected String getCountFormatSql(boolean isDisplay) {
        String tableName = getTableName();
        String tableAlias = getTableAlias();

        String sql =
                "select count(*) " +
                        "from " + tableName + " " + tableAlias + " "
                        + (isDisplay ? getDisplayJoinSql() : "") +
                        "where " + tableAlias + "." + EntityTableColumn.LAST_REC_TXN_TYPE_CODE + " <> 'D' ";

        return sql;
    }

    protected String getSelectByIdFormatSql(long id, boolean isDisplay) {
        String tableAlias = getTableAlias();
        String primaryKey = getPrimaryKey();

        String sql = getSelectFormatSql(isDisplay) +
                "and " + tableAlias + "." + primaryKey + " = %d ";

        return String.format(sql, id);
    }

    protected Pair<String, Object[]> getCriteriaSqlMap(ICriteriaFactory criteria) {
        String sql = "";
        List<Object> params = new ArrayList<>();

        Map<String, Object> map = criteria.ToMap();

        Class entityClass = (Class)((ParameterizedType)getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entityClass.getDeclaredFields()));

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object entryValue = entry.getValue();
            if (entryValue == null) continue;
            Optional<Field> field = fields.stream().filter(f -> f.getName() == entry.getKey()).findFirst();
            if (field.isPresent()) {
                RuntimeColumnInfo columnInfo = field.get().getDeclaredAnnotation(RuntimeColumnInfo.class);
                if (columnInfo != null) {
                    sql += " and " + columnInfo.name() + " = ?";

                    Object value = entryValue;
                    params.add(value);
                }
            }
        }
        return new Pair<>(sql, params.toArray());
    }

    //region IListDao

    @Override
    public T getRecord(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getSelectFormatSql(false) + sqlMap.first;

            List<T> result = selectList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (T item : result) {
                item.setKey(item.getKey());
            }
            return result.size() == 1 ? result.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> getRecordList(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getSelectFormatSql(false) + sqlMap.first;

            List<T> result = selectList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (T item : result) {
                item.setKey(item.getKey());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TDisplay getRecordDisplay(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getSelectFormatSql(true) + sqlMap.first;

            List<TDisplay> result = selectDisplayList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (TDisplay item : result) {
                item.setKey(item.getKey());
            }
            return result.size() == 1 ? result.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TDisplay> getRecordDisplayList(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getSelectFormatSql(true) + sqlMap.first;

            List<TDisplay> result = selectDisplayList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (TDisplay item : result) {
                item.setKey(item.getKey());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer countRecordList(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getCountFormatSql(false) + sqlMap.first;

            Integer count = countList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer countRecordDisplayList(ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = getCountFormatSql(true) + sqlMap.first;

            Integer count = countDisplayList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T queryForObject(SimpleSQLiteQuery query, ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = query.getSql() + sqlMap.first;

            List<T> result =  selectList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (T item : result) {
                item.setKey(item.getKey());
            }
            return result.size() == 1 ? result.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> queryForList(SimpleSQLiteQuery query, ICriteriaFactory criteria) {
        try {
            Pair<String, Object[]> sqlMap = getCriteriaSqlMap(criteria);
            String sql = query.getSql() + sqlMap.first;

            List<T> result = selectList(new SimpleSQLiteQuery(
                    sql,
                    sqlMap.second
            ));
            // Set BaseModel.key
            for (T item : result) {
                item.setKey(item.getKey());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //endregion

    //region IRepositoryDao

    @Override
    public T getRecordById(long id) {
        try {
            String sql = getSelectByIdFormatSql(id, false);

            List<T> result = selectList(new SimpleSQLiteQuery(
                    sql
            ));
            // Set BaseModel.key
            for (T item : result) {
                item.setKey(item.getKey());
            }
            return result.size() == 1 ? result.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TDisplay getRecordDisplayById(long id) {
        try {
            String sql = getSelectByIdFormatSql(id, true);

            List<TDisplay> result = selectDisplayList(new SimpleSQLiteQuery(
                    sql
            ));
            // Set BaseModel.key
            for (TDisplay item : result) {
                item.setKey(item.getKey());
            }
            return result.size() == 1 ? result.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //endregion
}