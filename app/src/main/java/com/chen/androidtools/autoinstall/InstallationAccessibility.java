package com.chen.androidtools.autoinstall;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.chen.androidtools.acessbility.BaseAccessbilityJob;

import java.util.List;

/**
 * 项目名称：grabMoney
 * 包名:com.qc.grabmoney.service
 * 类名:InstallationAccessibility
 * 类描述：应用安装
 * 创建人：YY
 * 创建时间：16/12/20 下午2:28
 * 修改人：YY(hbyeyang@yeah.net)
 * 修改时间：16/12/20 下午2:28
 */
public class InstallationAccessibility extends BaseAccessbilityJob {

    public static int INVOKE_TYPE = 0;
    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_UNINSTALL_APP = 3;

    private String MYAPP_PACKAGENAME = "com.android.packageinstaller";

    @Override
    public String getTargetPackageName() {
        return MYAPP_PACKAGENAME;
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event) {






        processAccessibilityEnvent(event);
    }

    @Override
    public void onStopJob() {

    }

    @Override
    public boolean isEnable() {
       return true;
    }

    private void processAccessibilityEnvent(AccessibilityEvent event) {

        Log.d("test", event.eventTypeToString(event.getEventType()));
        if (event.getSource() == null) {
            Log.d("test", "the source = null");
        } else {
            Log.d("test", "event = " + event.toString());
            switch (INVOKE_TYPE) {
                case TYPE_KILL_APP:
                    processKillApplication(event);
                    break;
                case TYPE_INSTALL_APP:
                    processinstallApplication(event);
                    break;
                case TYPE_UNINSTALL_APP:
                    processUninstallApplication(event);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 模拟用户点击
     *
     * @param event
     */
    private void click_on_the_simulation(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> jinyunxuyici = event.getSource().findAccessibilityNodeInfosByText("仅允许一次");
        if (jinyunxuyici != null && !jinyunxuyici.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < jinyunxuyici.size(); i++) {
                node = jinyunxuyici.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    try {
                        Thread.sleep(1000 * 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<AccessibilityNodeInfo> jiechujinzhi = event.getSource().findAccessibilityNodeInfosByText("解除禁止");
        if (jiechujinzhi != null && !jiechujinzhi.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < jiechujinzhi.size(); i++) {
                node = jiechujinzhi.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    try {
                        Thread.sleep(1000 * 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<AccessibilityNodeInfo> unintall_nodes = event.getSource().findAccessibilityNodeInfosByText("安装");
        if (unintall_nodes != null && !unintall_nodes.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < unintall_nodes.size(); i++) {
                node = unintall_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    try {
                        Thread.sleep(1000 * 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<AccessibilityNodeInfo> next_nodes = event.getSource().findAccessibilityNodeInfosByText("下一步");
        if (next_nodes != null && !next_nodes.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < next_nodes.size(); i++) {
                node = next_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }

//				List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("打开");
        List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("完成");
        if (ok_nodes != null && !ok_nodes.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < ok_nodes.size(); i++) {
                node = ok_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    private void processinstallApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.packageinstaller")) {
                click_on_the_simulation(event);
            }
        }

    }

    private void processUninstallApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.packageinstaller")) {
                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (ok_nodes != null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < ok_nodes.size(); i++) {
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }

                }
            }
        }

    }

    private void processKillApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.settings")) {
                List<AccessibilityNodeInfo> stop_nodes = event.getSource().findAccessibilityNodeInfosByText("强行停止");
                if (stop_nodes != null && !stop_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < stop_nodes.size(); i++) {
                        node = stop_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            if (node.isEnabled()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }

                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (ok_nodes != null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < ok_nodes.size(); i++) {
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Log.d("action", "click ok");
                        }
                    }
                }
            }
        }
    }
}
