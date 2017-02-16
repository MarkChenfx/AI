package com.chen.androidtools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.androidtools.acessbility.BaseAccessibilityService;
import com.chen.androidtools.barcode.BarcodeActivity;
import com.chen.utils.LogUtils;
import com.chen.utils.SpUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String DEFAULT_SMS = "com.android.mms";
    @BindView(R.id.test)
    TextView mTest;
    @BindView(R.id.switchBtn_wx)
    SwitchButton openWxHongbao;
    @BindView(R.id.barcode)
    TextView barcode;
    @BindView(R.id.switchBtn_wifi)
    SwitchButton mSwitchBtnWifi;
    @BindView(R.id.switchBtn_sms)
    SwitchButton mSwitchBtnSms;
    @BindView(R.id.wx)
    TextView mWx;
    @BindView(R.id.wx_view)
    RelativeLayout mWxView;
    @BindView(R.id.wifi)
    TextView mWifi;
    @BindView(R.id.wifi_view)
    RelativeLayout mWifiView;
    @BindView(R.id.sms)
    TextView mSms;
    @BindView(R.id.sms_num)
    TextView mSmsNum;
    @BindView(R.id.sms_view)
    RelativeLayout mSmsView;


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();

        initListener();

        initSms();

        mTest.setText(stringFromJNI());
        LogUtils.e("IMSI:" + getIMSI(this));
        LogUtils.e("IMEI:" + getIMEI(this));
    }

    /**
     * 获取手机IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }


    private void initListener() {

        barcode.setOnClickListener(this);
        mTest.setOnClickListener(this);
        openWxHongbao.setOnCheckedChangeListener(this);
        mSwitchBtnWifi.setOnCheckedChangeListener(this);
        mSwitchBtnSms.setOnCheckedChangeListener(this);

    }

    private void initSms() {
        String defaultSmsApp = "";
        String currentPn = getPackageName();//获取当前程序包名
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
        }
        mSwitchBtnSms.setChecked(defaultSmsApp.equals(currentPn));
    }

    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "开启", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获得系统进程信息
    private void getRunningAppProcessInfo() {

        final ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            // 获得该进程占用的内存
            int[] myMempid = new int[]{pid};
            // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(myMempid);
            // 获取进程占内存用信息 kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;

            Log.i(TAG, "processName: " + processName + "  pid: " + pid
                    + " uid:" + uid + " memorySize is -->" + memSize + "kb");

            // 获得每个进程里运行的应用程序(包),即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            Log.i(TAG, "process id is " + pid + "has " + packageList.length);
            for (String pkg : packageList) {
                Log.i(TAG, "packageName " + pkg + " in process id is -->" + pid);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseAccessibilityService.isRunning()) {

        } else {
            openAccessibilityServiceSettings();
        }
//        openAccessibilityServiceSettings();
    }

    private void init() {


        mSmsNum.setText((String) SpUtils.get(this, SpUtils.SMS_AUTO_SNED_NUM, "未设置自动转发短信接收手机号"));

        //应用程序最大可用内存
        int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
        //应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
        //应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024 / 1024;

//        ---> maxMemory=128M,totalMemory=26M,freeMemory=9M
//        maxMemory=512M,totalMemory=26M,freeMemory=9M
        System.out.println("---> maxMemory=" + maxMemory + "M,totalMemory=" + totalMemory + "M,freeMemory=" + freeMemory + "M");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.barcode:
                startActivity(new Intent(this, BarcodeActivity.class));
                break;
            case R.id.test:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.switchBtn_wx:
                SpUtils.putAndApply(getApplicationContext(), SpUtils.AUTO_OPEN_HONGBOA, isChecked);
                break;

            case R.id.switchBtn_sms:
//                if (isChecked)
                if (isChecked)
                    intent2DefaultSms(getPackageName());
                else
                    intent2DefaultSms(DEFAULT_SMS);
                break;
            case R.id.switchBtn_wifi:
                break;

        }
    }

    //
    private void intent2DefaultSms(String defaultSms) {

        String defaultSmsApp = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
            LogUtils.e("短信：" + defaultSmsApp);
        }
        if (!defaultSmsApp.equals(defaultSms)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSms);
                startActivity(intent);
            } else {
                Toast.makeText(this, "系统版本小于4.4没做短信功能", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
