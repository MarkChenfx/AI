package com.chen.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

/**
 * 项目名称：AccessibilityDemo
 * 包名:com.jack.accessibility.utils
 * 类名:Utils
 * 类描述：工具类
 * 创建人：YY
 * 创建时间：16/11/1 上午11:16
 * 修改人：YY(hbyeyang@yeah.net)
 * 修改时间：16/11/1 上午11:16
 */
public class Utils {

    public static long lastClickTime=1;
    /**
     * 防止按钮连续点击
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
//        LogUtils.e("last:"+lastClickTime);
        if (time - lastClickTime < 500) {
//            LogUtils.e("time:"+time+"/lastclick:"+lastClickTime);
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isAccessibilitySettingsOn(Context mContext,final String servicen, String packageName) {
        int accessibilityEnabled = 0;
        final String service = packageName + "/" + servicen;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }





    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.00");


        return df.format(d);
    }


}
