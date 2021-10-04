package com.daniel4wong.core.Dao.Data;

import java.util.HashMap;
import java.util.Map;

public class CriteriaMap implements ICriteriaFactory {

    private Map<String, Object> map;

    private CriteriaMap()
    {
        map = new HashMap<String, Object>();
    }

    public static CriteriaMap newCriteria()
    {
        return new CriteriaMap();
    }

    public static CriteriaMap criteria(String key, Object value)
    {
        return newCriteria().put(key, value);
    }

    public CriteriaMap put(String key, Object value)
    {
        this.map.put(key, value);
        return this;
    }

    public CriteriaMap putAll(ICriteriaFactory factory)
    {
        map.putAll(factory.ToMap());
        return this;
    }

    @Override
    public Map<String, Object> ToMap() {
        return this.map;
    }
}