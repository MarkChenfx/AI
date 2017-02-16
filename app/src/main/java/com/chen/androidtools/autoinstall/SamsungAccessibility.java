package com.chen.androidtools.autoinstall;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.chen.androidtools.acessbility.BaseAccessbilityJob;
import com.chen.utils.SpUtils;

import java.util.List;

/**
 * 项目名称：grabMoney
 * 包名:com.qc.autoinstall
 * 类名:
 * 类描述：
 * 创建人：YY
 * 创建时间：16/12/26 下午5:58
 * 修改人：YY(hbyeyang@yeah.net)
 * 修改时间：16/12/26 下午5:58
 */
public class SamsungAccessibility extends BaseAccessbilityJob {
    public static int INVOKE_TYPE = 0;
    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_UNINSTALL_APP = 3;
//    private String MYAPP_PACKAGENAME = "com.samsung.android.packageinstaller";
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
        return (boolean)SpUtils.get(getContext(),SpUtils.AUTO_INSTALL,true);
    }

    private void processAccessibilityEnvent(AccessibilityEvent event) {

        Log.d("test", event.eventTypeToString(event.getEventType()));
        if (event.getSource() == null) {
            Log.d("test", "the source = null");
        } else {
            processinstallApplication(event);
        }
    }

    /**
     * 模拟用户点击
     *
     * @param event
     */
    private void click_on_the_simulation(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> jiechujinzhi = event.getSource().findAccessibilityNodeInfosByText("解除禁止");
        if (jiechujinzhi != null && !jiechujinzhi.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < jiechujinzhi.size(); i++) {
                node = jiechujinzhi.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
            if (event.getPackageName().equals("com.samsung.android.packageinstaller")) {
                click_on_the_simulation(event);
            }
        }
    }
}
