package com.daniel4wong.core;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.daniel4wong.core.Helper.BatteryHelper;

import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.daniel4wong.core.test", appContext.getPackageName());
    }

    @Test
    public void textBatteryHelper() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Map<String, Object> info = BatteryHelper.getBatteryInfo(appContext);
        assertNotNull(info);
    }
}