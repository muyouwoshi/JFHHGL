package com.juxin.library.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络状态工具类
 *
 * @author mcxiaoke
 * @version 2.0 2014.02.12
 */
public final class NetworkUtils {

    //用户上网方式（2017-06-20）Wifi 1 4G 2 3G/2G 3 其它 4
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_4G = 2;
    public static final int NETWORK_2_OR_3G = 3;
    public static final int NETWORK_OTHER = 4;

    private static String USER_AGENT;
    /**
     * 网络状态枚举
     */
    public enum NetworkType {
        WIFI, MOBILE, OTHER, NONE
    }

    private NetworkUtils() {
    }

    /**
     * @return 获取网络mac地址
     */
    public static String getMacAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifiManager.getConnectionInfo().getMacAddress();
            if (mac.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}")) return mac;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return "00:00:00:00:00:00";
    }

    public static String getNetworkTypeName(Context context) {
        String result = "(No Network)";

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return result;

            final NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnectedOrConnecting()) return result;

            result = info.getTypeName();
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) result += info.getSubtypeName();
        } catch (Throwable ignored) {
        }
        return result;
    }

    /**
     * 获取当前网络类型
     *
     * @return 返回网络类型
     */
    public static NetworkType getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return NetworkType.NONE;
        }
        int type = info.getType();
        if (ConnectivityManager.TYPE_WIFI == type) {
            return NetworkType.WIFI;
        } else if (ConnectivityManager.TYPE_MOBILE == type) {
            return NetworkType.MOBILE;
        } else {
            return NetworkType.OTHER;
        }
    }

    /**
     * 获取网络类型
     *
     * @return WIFI/4G/3G/2G
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return NETWORK_OTHER;
        }
        int type = info.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            return getNetWorkClass(context);
        } else {
            return NETWORK_OTHER;
        }
    }

    public static String getOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperator();
    }

    /**
     * 网络连接是否断开
     *
     * @param context Context
     * @return 是否断开s
     */
    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    /**
     * 是否有网络连接
     *
     * @param context Context
     * @return 是否连接
     */
    public static boolean isConnected(Context context) {
        if (context == null) return true;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * 当前是否是WIFI网络
     *
     * @param context Context
     * @return 是否WIFI
     */
    public static boolean isWifi(Context context) {
        return NetworkType.WIFI.equals(getNetworkType(context));
    }

    /**
     * 当前是否移动网络
     *
     * @param context Context
     * @return 是否移动网络
     */
    public static boolean isMobile(Context context) {
        return NetworkType.MOBILE.equals(getNetworkType(context));
    }

    /**
     * @return 获取当前ip地址
     */
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int getNetWorkClass(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                //return NETWORK_2G;//以上是2G
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //return NETWORK_3G;//以上是3G
                return NETWORK_2_OR_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_4G;
            default:
                return NETWORK_OTHER;
        }
    }

    public static String getDomainName(String url) {
        int index = url.indexOf("://");
        String header = "";
        if (index != -1) {
            header = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index);
        }
        return header + url;
    }

    public static String getUserAgent() {
        if (!TextUtils.isEmpty(USER_AGENT))
            return USER_AGENT;

        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build/");
            buffer.append(id);
        }

        Resources res = Resources.getSystem();

        int agentId = res.getIdentifier("web_user_agent", "string", "android");
        final String agent = agentId > 0 ? res.getText(agentId).toString() :
                "Mozilla/5.0 (Linux; U; Android %s; ZTE BV0730 Build/MRA58K)  Version/4.0 %s";

        int targetId = res.getIdentifier("web_user_agent_target_content", "string", "android");
        final String target = targetId > 0 ? res.getText(targetId).toString() : "";

        USER_AGENT = String.format(agent, buffer, target);
        return USER_AGENT;
    }
}
