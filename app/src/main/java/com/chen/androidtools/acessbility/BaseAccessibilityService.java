package com.chen.androidtools.acessbility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.chen.androidtools.BuildConfig;
import com.chen.androidtools.autoinstall.InstallationAccessibility;
import com.chen.androidtools.autoinstall.SamsungAccessibility;
import com.chen.androidtools.wechat.GrabMoneyAccessbilityJob;
import com.chen.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by CHEN on 2016/12/19.
 */
public class BaseAccessibilityService extends AccessibilityService {

    private GrabMoneyConfig mConfig;

    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    //检测包名
    private String[] PACKAGES = {"com.tencent.mm", "com.qc.grabmoney",
            "com.android.packageinstaller", "com.lenovo.security",
            "com.samsung.android.packageinstaller", "com.miui.securitycenter"};
    //回调处理不同的辅助功能类
    private static final Class[] ACCESSBILITY_JOBS = {
            GrabMoneyAccessbilityJob.class,
            InstallationAccessibility.class,
//            LenovoPhoneAccessibility.class, AutoAttentWechatAccessbility.class,
            SamsungAccessibility.class,
// XiaomiAccessibility.class
    };

    private static BaseAccessibilityService service;


    private List<AccessbilityJob> mAccessbilityJobs;


    private PackageInfo mWechatPackageInfo = null;


    @Override
    public void onCreate() {
        super.onCreate();

        init();

    }


    private void init() {

        mAccessbilityJobs = new ArrayList<>();
        mConfig = GrabMoneyConfig.getInstance(this);

        //初始化辅助插件工作
        for (Class clazz : ACCESSBILITY_JOBS) {
            try {
                Object object = clazz.newInstance();
                if (object instanceof AccessbilityJob) {
                    AccessbilityJob job = (AccessbilityJob) object;
                    job.onCreateJob(this);
                    mAccessbilityJobs.add(job);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        //初始化
//        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

    }

    /**
     * 初始化所检测的包名
     */
    private void initConfig() {

        LogUtils.e("initConfig");
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        //指定包名
        accessibilityServiceInfo.packageNames = PACKAGES;
        //指定事件类型
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED|AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        accessibilityServiceInfo.notificationTimeout = 100;
        setServiceInfo(accessibilityServiceInfo);

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        //发送广播，已经连接上了
        Intent intent = new Intent(GrabMoneyConfig.ACTION_GRABMONEY_SERVICE_CONNECT);
        sendBroadcast(intent);
        Toast.makeText(this, "已连接智能管家服务", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (BuildConfig.DEBUG) {
            int eventType = event.getEventType();
            String eventText = "";
            LogUtils.i("==============Start====================");
            switch (eventType) {
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    eventText = "TYPE_VIEW_CLICKED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    eventText = "TYPE_VIEW_FOCUSED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                    eventText = "TYPE_VIEW_LONG_CLICKED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_SELECTED:
                    eventText = "TYPE_VIEW_SELECTED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    eventText = "TYPE_VIEW_TEXT_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    eventText = "TYPE_WINDOW_STATE_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                    eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                    break;
                case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                    eventText = "TYPE_ANNOUNCEMENT";
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                    eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                    eventText = "TYPE_VIEW_HOVER_ENTER";
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                    eventText = "TYPE_VIEW_HOVER_EXIT";
                    break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    eventText = "TYPE_VIEW_SCROLLED";
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                    eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                    break;
            }
            eventText = eventText + ":" + eventType;
            LogUtils.i(event.getPackageName() + "");
            LogUtils.i("界面名字:" + event.getClassName());
//            LogUtils.e(event+"");
//            LogUtils.e(event.toString());
            LogUtils.i(eventText);
            LogUtils.i("=============END=====================");
        }


        String pkn = String.valueOf(event.getPackageName());

        LogUtils.e("长度：" + mAccessbilityJobs.size());
        if (mAccessbilityJobs != null && !mAccessbilityJobs.isEmpty()) {
            for (AccessbilityJob job : mAccessbilityJobs) {
//                LogUtils.e("开始分发：" + job.isEnable() + "/" + pkn + "/" + job.getTargetPackageName());

                if (pkn.equals(job.getTargetPackageName()) && job.isEnable()) {
                    job.onReceiveJob(event);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.d("grabmoney service interrupt");
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    public GrabMoneyConfig getConfig() {
        return mConfig;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("grabmoney service destory");
        if (mAccessbilityJobs != null && !mAccessbilityJobs.isEmpty()) {
            for (AccessbilityJob job : mAccessbilityJobs) {
                job.onStopJob();
            }
            mAccessbilityJobs.clear();
        }
        service = null;
        mAccessbilityJobs = null;
        //发送广播，已经断开辅助服务
        Intent intent = new Intent(GrabMoneyConfig.ACTION_GRABMONEY_SERVICE_DISCONNECT);
        sendBroadcast(intent);
    }


    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (service == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = service.getServiceInfo();
        if (info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if (i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }

        if (!isConnect) {
            return false;
        }
        return true;
    }
}
