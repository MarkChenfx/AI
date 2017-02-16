package com.chen.androidtools.sms;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 项目名称：AndroidTools
 * 包名:com.chen.androidtools.service
 * 类描述：
 * 创建人：CHEN
 * 创建时间：17/2/6 下午5:03
 */
public class HeadlessSmsSendService extends IntentService {
    private static final String TAG = "HeadlessSmsSendService";

    public HeadlessSmsSendService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
}