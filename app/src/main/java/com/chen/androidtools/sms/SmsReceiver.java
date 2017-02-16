package com.chen.androidtools.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.chen.utils.LogUtils;

/**
 * 项目名称：AndroidTools
 * 包名:com.chen.androidtools.receiver
 * 类描述：
 * 创建人：CHEN
 * 创建时间：17/2/6 下午4:59
 */
public class SmsReceiver extends BroadcastReceiver {


    //发送短信到指定手机号
    private static final String PhoneNumber = "";
    //admin控制是否继续转发
    private static final String ADMIN = "";

    private boolean SEND = true;
    private String mAddress;
    private String mBody;
    private long mDate;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            SmsMessage sms = messages[0];
            if (messages.length == 1 || sms.isReplace()) {
                mBody = sms.getDisplayMessageBody();
            } else {
                StringBuilder bodyText = new StringBuilder();
                for (SmsMessage message : messages) {
                    bodyText.append(message.getMessageBody());
                }
                mBody = bodyText.toString();
            }

            mAddress = sms.getDisplayOriginatingAddress();
            mDate = sms.getTimestampMillis();

            LogUtils.e("收到短信:" + mAddress + "/" + mBody + "/" + mDate);

            if (mAddress.equals(ADMIN) && mBody.equals("停止"))
                SEND = false;

            if (mAddress.equals(ADMIN) && mBody.equals("开始"))
                SEND = true;


            SmsHelper.addMessageToInbox(context, mAddress, mBody, mDate);

            if (!SEND)
                SmsHelper.sendSMS(PhoneNumber, mAddress, mBody);
        }
    }
}