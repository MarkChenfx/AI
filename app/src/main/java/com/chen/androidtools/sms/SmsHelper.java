package com.chen.androidtools.sms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.List;

/**
 * 项目名称：AndroidTools
 * 包名:com.chen.androidtools.sms
 * 类描述：
 * 创建人：CHEN
 * 创建时间：17/2/16 下午4:44
 */
public class SmsHelper {


    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");


    public static Uri addMessageToInbox(Context context, String address, String body, long time) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put("address", address);
        cv.put("body", body);
        cv.put("date_sent", time);
        return contentResolver.insert(RECEIVED_MESSAGE_CONTENT_PROVIDER, cv);
    }

    public static  void sendSMS(String phoneNumber, String fromNum,String message) {
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            text = "短信来自："+fromNum+"\r\n内容："+text;
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }
}