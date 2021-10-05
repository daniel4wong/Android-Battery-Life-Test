package com.daniel4wong.core.Helper;

import android.content.Context;
import android.os.BatteryManager;

import java.util.HashMap;
import java.util.Map;

public class BatteryHelper {
    public static Map<String, Object> getBatteryInfo(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        Map<String, Object> objectMap = new HashMap<>();

        Integer chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        Integer capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Integer currentAverage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        Integer status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        Integer currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        Integer energyCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
        objectMap.put("chargeCounter", chargeCounter);
        objectMap.put("capacity", capacity);
        objectMap.put("currentAverage", currentAverage);
        objectMap.put("status", status);
        objectMap.put("currentNow", currentNow);
        objectMap.put("energyCounter", energyCounter);

        return objectMap;
    }
}
