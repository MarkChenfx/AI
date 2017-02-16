package com.chen.androidtools.wechat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.chen.androidtools.BuildConfig;
import com.chen.androidtools.acessbility.BaseAccessbilityJob;
import com.chen.androidtools.acessbility.BaseAccessibilityService;
import com.chen.utils.LockSingleTon;
import com.chen.utils.LogUtils;
import com.chen.utils.SpUtils;

import java.util.List;

/**
 * Created by CHEN on 2016/12/19.
 * <p>
 * 单独处理微信抢红包
 */
public class GrabMoneyAccessbilityJob extends BaseAccessbilityJob {

    /**
     * 微信的包名
     */
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    /**
     * 红包消息的关键字
     */
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    /**
     * 不能再使用文字匹配的最小版本号
     */
    private static final int USE_ID_MIN_VERSION = 700;// 6.3.8 对应code为680,6.3.9对应code为700


    //获取抢红包金额
    private static final int GET_MONET = 2;


    //获取抢红包金额索引
    private static final String GET_MONEY_INDEX="元";
    private boolean isFirstChecked;
    private Handler mHandler = null;

    private boolean isAuto = false;


    //是否开启抢红包后自动返回
    private boolean mAutoBack = false;


    private boolean mAutoCount = false;


    private boolean mOpenNotify = false;


    @Override
    public void onCreateJob(BaseAccessibilityService service) {
        super.onCreateJob(service);
    }


    @Override
    public void onReceiveJob(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        LogUtils.i("========start========");
        LogUtils.i("接收到抢红包事件：" + event);
        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence t : texts) {
                    String text = String.valueOf(t);
                    if (text.contains(HONGBAO_TEXT_KEY)) {
//                        getService().unLockScreen();
                        LockSingleTon.getInstance(getContext()).unLockScreen();
                        openNotify(event);
                        break;
                    }
                }
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            LogUtils.i("打开红包" + "TYPE_WINDOW_STATE_CHANGED");
            openHongBao(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && isFirstChecked) {
            LogUtils.i("打开处于群聊的红包");
            openHongBaoInChat(event);

//            openHongBao(event);
        } else if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            isFirstChecked = true;
//            openHongBao(event);
        } else if (mAutoCount) {
//            LogUtils.e("红包已经抢过了");
//            mAutoCount = false;
        } else {
            LogUtils.i("接收到未处理事件" + isFirstChecked + "/" + event);
//            isFirstChecked = false;

        }
        LogUtils.i("========end========" + mAutoCount);
    }

    /**
     * 打开通知栏消息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotify(AccessibilityEvent event) {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            LogUtils.e("notify为空");
            return;
        }

        LogUtils.i("openNotify");
        //以下是精华，将微信的通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;

        isFirstChecked = true;
        try {
            mOpenNotify = true;
            pendingIntent.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openHongBaoInChat(AccessibilityEvent event) {

        LogUtils.i("聊天中红包：" + event);
        handleChatListHongBao();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openHongBao(AccessibilityEvent event) {

        LogUtils.i("报名：" + event.getClassName()+"mAutoCount:"+mAutoCount+"/"+isAuto);

        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            //点中了红包，下一步就是去拆红包
            LogUtils.e("去点击抢");

            mAutoCount = true;
            handleLuckyMoneyReceive();

        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName()) && isAuto) {
            //拆完红包后看详细的纪录界面
            LogUtils.i("返回");
            if (mOpenNotify) {
                LogUtils.i("抢红包后锁屏");
//                getService().lockScreen();
                LockSingleTon.getInstance(getContext()).lockScreen();

                mOpenNotify = false;
            }
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName()) && mAutoCount) {
            updateCountMoney();
            LockSingleTon.getInstance(getContext()).lockScreen();
            LogUtils.e("更新红包信息");
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            //在聊天界面,去点中红包
            LogUtils.e("在聊天界面打开红包");
            handleChatListHongBao();
//            autoBack();
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName()) && isFirstChecked) {
//            handleLuckyMoneyReceive();
//            isFirstChecked = false;
        } else {

            LogUtils.e("未点击红包：" + isFirstChecked + event);
        }
    }

    private void autoBack() {
        if (isAuto) {
            isAuto = false;
            LogUtils.i("关闭聊天界面");
            try {
                if (mAutoBack)
                    back2WxLauncher();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mAutoBack) {
                back2Home();
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void updateCountMoney() {

        mAutoCount = false;


        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();

        AccessibilityNodeInfo targetNode = null;
        if (nodeInfo == null) {
            LogUtils.i("rootWindow为空");
            return;
        }

        List<AccessibilityNodeInfo> ll = nodeInfo.findAccessibilityNodeInfosByText(GET_MONEY_INDEX);

        if (!ll.isEmpty()) {
            AccessibilityNodeInfo p = ll.get(0).getParent();
            if (p != null) {
                for (int i = 0; i < p.getChildCount(); i++) {
                    AccessibilityNodeInfo node = p.getChild(i);

                    if ("android.widget.TextView".equals(node.getClassName())) {
                        LogUtils.e("找到留言按钮");
                        targetNode = node.getParent();
                        break;
                    }
                }
            }
        }

        if (targetNode == null) {
            LogUtils.e("获取红包金额异常");
        } else {
            AccessibilityNodeInfo money = targetNode.getChild(GET_MONET);
            String m = money.getText().toString();
//            GrabMoneyConfig.getInstance(getContext()).setMoneyCount();
//            GrabMoneyConfig.getInstance(getContext()).setMoneyMoney(m);
            LogUtils.e("数量更新："+m);
        }
    }


    /**
     * 回到系统桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);

        getService().startActivity(home);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void back2Chat() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();

        if (nodeInfo == null) {
            LogUtils.i("rootWindow为空");
            return;
        }
        if (nodeInfo.getChildCount() > 0) {
            AccessibilityNodeInfo acDiaglog = nodeInfo.getChild(0);
            if (acDiaglog.getChildCount() > 2) {
                AccessibilityNodeInfo ac = acDiaglog.getChild(1);
                ac.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void back2WxLauncher() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();

        if (nodeInfo == null) {
            LogUtils.i("rootWindow为空");
            return;
        }
        if (nodeInfo.getChildCount() > 0) {
            LogUtils.i("聊天界面:" + nodeInfo.getChildCount());

            if (nodeInfo.getChildCount() > 8) {
                AccessibilityNodeInfo acDiaglog = nodeInfo.getChild(8);
                if (acDiaglog.getChildCount() > 0) {
                    LogUtils.i("界面:" + acDiaglog.getChildCount());
                    AccessibilityNodeInfo ac = acDiaglog.getChild(0);
                    ac.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

        }
    }

    /**
     * 点击聊天里的红包后，显示的界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleLuckyMoneyReceive() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();


        LogUtils.i("点击聊天里面的红包");
        if (nodeInfo == null) {
            LogUtils.w("rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        List<AccessibilityNodeInfo> list = null;
        int event = getConfig().getWechatAfterOpenHongBaoEvent();
//        if (event == GrabMoneyConfig.WX_AFTER_OPEN_HONGBAO) {
            LogUtils.e("kaishi" + event);
            //拆红包
//            if (getService().getWechatVersion() < USE_ID_MIN_VERSION) {
            if (false) {
                list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2c");
                    LogUtils.e("通过ID找按钮：" + list.size());
                }
                if (list == null || list.isEmpty()) {
                    List<AccessibilityNodeInfo> sl = nodeInfo.findAccessibilityNodeInfosByText("给你发了一个红包");
                    LogUtils.e("通过文字找按钮：" + sl.size());
                    if (!sl.isEmpty()) {
                        AccessibilityNodeInfo p = sl.get(0).getParent();
                        if (p != null) {
                            for (int i = 0; i < p.getChildCount(); i++) {
                                AccessibilityNodeInfo node = p.getChild(i);
                                if ("android.widget.Button".equals(node.getClassName())) {
                                    targetNode = node;
                                    LogUtils.e("获取到按钮");
                                    break;
                                }
                            }
                        }
                    } else {
                        List<AccessibilityNodeInfo> ll = nodeInfo.findAccessibilityNodeInfosByText("发了一个红包");
                        LogUtils.e("通过群聊文字找按钮：" + ll.size());

                        if (!ll.isEmpty()) {
                            AccessibilityNodeInfo p = ll.get(0).getParent();
                            if (p != null) {
                                for (int i = 0; i < p.getChildCount(); i++) {
                                    AccessibilityNodeInfo node = p.getChild(i);
                                    if ("android.widget.Button".equals(node.getClassName())) {
                                        targetNode = node;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }


        if (list != null && !list.isEmpty()) {
            targetNode = list.get(0);
        }

        if (targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            long sDelayTime = getConfig().getWechatOpenDelayTime();
            if (sDelayTime != 0) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }, sDelayTime);
            } else {
//                isAuto = true;
                LogUtils.i("拆");
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }


    /**
     * 收到聊天里的红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleChatListHongBao() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        LogUtils.i("点击聊天里面的红包");
        if (nodeInfo == null) {
            LogUtils.w("rootWindow为空");
            return;
        }

        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        if (list != null && list.isEmpty()) {
            // 从消息列表查找红包
            list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");

            if (list == null || list.isEmpty()) {
                return;
            }

            for (AccessibilityNodeInfo n : list) {
                if (BuildConfig.DEBUG) {
                    LogUtils.i("-->微信红包:" + n);
                }
//                isAuto = true;
                LogUtils.i("开始返回");

                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        } else if (list != null) {
            //最新的红包领起
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                if (BuildConfig.DEBUG) {
                    LogUtils.i("-->领取红包:" + parent);
                }
                if (parent != null) {
                    if (isFirstChecked) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        LogUtils.i("-->领取到红包");
                        isFirstChecked = false;
                    }
                    break;
                }
            }
        }
    }


    @Override
    public String getTargetPackageName() {
        return WECHAT_PACKAGENAME;
    }

    @Override
    public void onStopJob() {
        LockSingleTon.getInstance(getContext()).lockScreen();
    }

    @Override
    public boolean isEnable() {

        return (boolean)SpUtils.get(getContext(),SpUtils.AUTO_OPEN_HONGBOA,false);
    }


    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }


}
