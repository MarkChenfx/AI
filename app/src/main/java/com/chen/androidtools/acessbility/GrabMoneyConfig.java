package com.chen.androidtools.acessbility;

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.utils.LogUtils;
import com.chen.utils.Utils;


/**
 * Created by CHEN on 2016/12/19.
 */
public class GrabMoneyConfig {

    public static final String ACTION_GRABMONEY_SERVICE_DISCONNECT = "com.qc.grabmoney.ACCESSBILITY_DISCONNECT";
    public static final String ACTION_GRABMONEY_SERVICE_CONNECT = "com.qc.grabmoney.ACCESSBILITY_CONNECT";

    public static final String PREFERENCE_NAME = "config";
    public static final String KEY_ENABLE_WECHAT = "KEY_ENABLE_WECHAT";
    public static final String KEY_WECHAT_AFTER_OPEN_HONGBAO = "KEY_WECHAT_AFTER_OPEN_HONGBAO";
    public static final String KEY_WECHAT_DELAY_TIME = "KEY_WECHAT_DELAY_TIME";

    public static final String AUTO_ATTENT_WECHAT = "AUTO_ATTENT_WECHAT";

    public static final String NEW_VERSION_DATA = "NEW_VERSION_DATA";

    //抢红包数量
    public static final String MONEY_COUNT = "MONEY_COUNT";
    //抢红包金额
    public static final String MONEY_MONEY = "MONEY_MONEY";

    //打开微信红包
    public static final int WX_AFTER_OPEN_HONGBAO = 0;
    //查看大家手气
    public static final int WX_AFTER_OPEN_SEE = 1;

    public static final String NEW_MESSAGE = "NEW_MESSAGE";


    /**
     * 加号按钮ID
     */
    public static String ADD_ID_KEY = "";


    public static enum WX_EVENT {
        //微信红包
        WX_AUTO_MONEY,
        //微信关注
        WX_AUTO_ATTENT,
        //停止微信自动抢红包
        WX_STOP_AUTO
    }


    public static final String IS_AUTO_INSTALL_APK = "IS_AUTO_INSTALL_APK";

    SharedPreferences preferences;


    private static volatile GrabMoneyConfig sInst = null;


    public static GrabMoneyConfig getInstance(Context context) {
        if (sInst == null) {
            synchronized (GrabMoneyConfig.class) {
                GrabMoneyConfig inst = sInst;
                if (inst == null) {
                    inst = new GrabMoneyConfig(context);
                    sInst = inst;
                } else {
                }
            }
        }
        return sInst;
    }


    private GrabMoneyConfig(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 是否启动微信抢红包
     */
    public boolean isEnableWechat() {
        return preferences.getBoolean(KEY_ENABLE_WECHAT, true);
    }

    /**
     * 微信打开红包后的事件
     */
    public int getWechatAfterOpenHongBaoEvent() {
        int defaultValue = 0;
        String result = preferences.getString(KEY_WECHAT_AFTER_OPEN_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 微信打开红包后延时时间
     */
    public int getWechatOpenDelayTime() {
        int defaultValue = 0;
        String result = preferences.getString(KEY_WECHAT_DELAY_TIME, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    /**
     * 获取微信下处理事件
     * 默认处理抢红包
     *
     * @return
     */
    public WX_EVENT getWxEvent() {
        return WX_EVENT.valueOf(preferences.getString(AUTO_ATTENT_WECHAT, WX_EVENT.WX_AUTO_MONEY.name()));
    }

    /**
     * 设置微信下处理事件
     *
     * @param au
     */
    public void setWechatEvent(WX_EVENT au) {
        preferences.edit().putString(AUTO_ATTENT_WECHAT, au.name()).commit();
    }

    /**
     * 增加抢红包次数
     *
     */
    public void setMoneyCount(){
       preferences.edit().putInt(MONEY_COUNT, preferences.getInt(MONEY_COUNT,0)+1).commit();
    }

    /**
     * 获取抢红包数量
     *
     * @return
     */
    public String getMoneyCount(){
        return preferences.getInt(MONEY_COUNT,0)+"";
    }

    /**
     * 设置红包总量
     *
     * @param money
     */
    public void setMoneyMoney(String money){

        LogUtils.i("有效的红包计数,金额："+ money);
        try{
            Double dm = Double.valueOf(money);
            Double tm = Double.valueOf(preferences.getString(MONEY_MONEY,"0.00"));
            String co = Utils.formatDouble(dm+tm);
            preferences.edit().putString(MONEY_MONEY,co).commit();
        }catch (Exception e){
            LogUtils.e("微信更新版本参数获取错误");
        }

    }

    /**
     * 获取红包总金额
     *
     * @return
     */
    public String getMoneyMoney(){
        return preferences.getString(MONEY_MONEY,"0.00");
    }





    /**
     * 获取是否开启自动安装
     *
     * @return
     */
    public boolean getIsAutoInstallApk() {
        return preferences.getBoolean(IS_AUTO_INSTALL_APK, false);
    }

    /**
     * 保存是否开启自动安装的APK
     *
     * @param key    IS_AUTO_INSTALL_APK
     * @param isAuto true/false
     */
    public void saveIsAutoInstallApk(String key, boolean isAuto) {
        preferences.edit().putBoolean(key, isAuto).commit();
    }
}
