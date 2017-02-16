package com.chen.androidtools.adb;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;


import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Utilities of Adb wifi
 * @author zeyuec
 * @since 2013-07-15
 */
public class Utility {

    private final static String TAG = "WIFI_ADB_Utility";

    private static int configPort = 5555;

    // adb wifi port, default 5555
    public static int getPort() {
        return configPort;
    }

    // not used right now, 2013-07-16
    public static void setPort(int port) {
        configPort = port;
    }

    // get phone ip
    public static String getIp() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = addr instanceof Inet4Address;

                            if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // detect if wifi is connected
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (state == State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    // detect if adbd is running
    public static boolean getAdbdStatus() {
        int lineCount = 0;
        try {
            Process process = Runtime.getRuntime().exec("ps | grep adbd");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String str = input.readLine();
            while (str != null) {
                lineCount++;
                str = input.readLine();
            }
            if (lineCount >= 2) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // set adb wifi service 
    public static boolean setAdbWifiStatus(boolean status) {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("setprop service.adb.tcp.port " + String.valueOf(getPort()) + "\n");
            os.writeBytes("stop adbd\n");

            if (status) {
                os.writeBytes("start adbd\n");
            }
            os.writeBytes("exit\n");
            os.flush();

            p.waitFor();
            if (p.exitValue() != 255) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
