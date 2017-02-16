package com.chen.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by CHEN on 17/1/12.
 */
public class LockSingleTon {

    private static volatile LockSingleTon sInst = null;
    //解锁屏幕相关
    // 键盘管理器
    KeyguardManager mKeyguardManager;
    // 键盘锁
    private KeyguardManager.KeyguardLock mKeyguardLock;
    // 电源管理器
    PowerManager mPowerManager;
    // 唤醒锁
    private PowerManager.WakeLock mWakeLock;


    public static LockSingleTon getInstance(Context context) {
        if (sInst == null) {
            synchronized (LockSingleTon.class) {
                LockSingleTon inst = sInst;
                if (inst == null) {
                    inst = new LockSingleTon(context);
                    sInst = inst;
                } else {
                }
            }
        }
        return sInst;
    }

    private LockSingleTon(Context context) {
        //初始化
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }


    public void unLockScreen() {


        // 点亮亮屏
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        mWakeLock.acquire();
        // 初始化键盘锁
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        // 键盘解锁
        mKeyguardLock.disableKeyguard();
    }

    public void lockScreen() {
        if (mWakeLock != null) {
            System.out.println("----> 终止服务,释放唤醒锁");
            mWakeLock.release();
            mWakeLock = null;
        }
        if (mKeyguardLock != null) {
            System.out.println("----> 终止服务,重新锁键盘");
            mKeyguardLock.reenableKeyguard();
        }
    }

}
