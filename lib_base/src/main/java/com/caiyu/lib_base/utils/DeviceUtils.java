package com.caiyu.lib_base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;


public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getSimpleName();
    public static final int MIN_STORAGE_SIZE = 52428800;
    private static int huaweiBadgenumber;
    private static int vivoBadgenumber;
    private static String deviceId;

    public DeviceUtils() {
    }

    public static String getGPUInfo() {
        String renderer = GLES20.glGetString(7937);
        String vendor = GLES20.glGetString(7936);
        String version = GLES20.glGetString(7938);
        return renderer + "; " + vendor + "; " + version;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
    }

    @TargetApi(18)
    public static long getAvailableSize(StatFs statFs) {
        long availableBytes;
        if (Build.VERSION.SDK_INT >= 18) {
            availableBytes = statFs.getAvailableBytes();
        } else {
            availableBytes = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }

        return availableBytes;
    }

    public static boolean isExternalStorageSpaceEnough(long fileSize) {
        File sdcard = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdcard.getAbsolutePath());
        return getAvailableSize(statFs) > fileSize;
    }


    public static long getRuntimeRemainSize(int memoryClass) {
        long remainMemory = Runtime.getRuntime().maxMemory() - getHeapAllocatedSizeInKb() * 1024L;
        switch (memoryClass) {
            case 0:
            default:
                break;
            case 1:
                remainMemory /= 1024L;
                break;
            case 2:
                remainMemory /= 1048576L;
        }

        return remainMemory;
    }

    public static long getHeapAllocatedSizeInKb() {
        long heapAllocated = getRuntimeTotalMemory(1) - getRuntimeFreeMemory(1);
        return heapAllocated;
    }

    private static long getRuntimeTotalMemory(int memoryClass) {
        long totalMemory = 0L;
        switch (memoryClass) {
            case 0:
                totalMemory = Runtime.getRuntime().totalMemory();
                break;
            case 1:
                totalMemory = Runtime.getRuntime().totalMemory() / 1024L;
                break;
            case 2:
                totalMemory = Runtime.getRuntime().totalMemory() / 1024L / 1024L;
                break;
            default:
                totalMemory = Runtime.getRuntime().totalMemory();
        }

        return totalMemory;
    }

    private static long getRuntimeFreeMemory(int memoryClass) {
        long freeMemory = 0L;
        switch (memoryClass) {
            case 0:
                freeMemory = Runtime.getRuntime().freeMemory();
                break;
            case 1:
                freeMemory = Runtime.getRuntime().freeMemory() / 1024L;
                break;
            case 2:
                freeMemory = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
                break;
            default:
                freeMemory = Runtime.getRuntime().freeMemory();
        }

        return freeMemory;
    }



    public static String getDeviceId() {
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = getOldDeviceId();
        }
        return deviceId;
    }

    /**
     * 获取设备的Pseudo-Unique ID
     *
     * @return
     */
    public static String getOldDeviceId() {

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * @param context
     * @return int
     * @description: 获取屏幕宽
     */
    public static int screenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context
     * @return int
     * @description: 获取屏幕高
     */
    public static int screenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Get Screen Real Height
     *
     * @param context Context
     * @return Real Height
     */
    public static int getRealHeight(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * Get Screen Real Width
     *
     * @param context Context
     * @return Real Width
     */
    public static int getRealWidth(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * Get Display
     *
     * @param context Context for get WindowManager
     * @return Display
     */
    private static Display getDisplay(Context context) {
        WindowManager wm;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            wm = activity.getWindowManager();
        } else {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        if (wm != null) {
            return wm.getDefaultDisplay();
        }
        return null;
    }

    public static class MEMORY_CLASS {
        public static final int IN_B = 0;
        public static final int IN_KB = 1;
        public static final int IN_MB = 2;

        public MEMORY_CLASS() {
        }
    }

    private static Field field;
    private static boolean hasField = true;

    /**
     * 解决华为手机特有的mlastsrvview 无法释放内存的引起的泄漏问题
     * 通过反射在页面销毁的时候通过反射将mlastsrvview设置为null
     *
     * @param context
     */
    public static void fixLeak(Context context) {
        if (!hasField) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mLastSrvView"};
        for (String param : arr) {
            try {
                if (field == null) {
                    field = imm.getClass().getDeclaredField(param);
                }
                if (field == null) {
                    hasField = false;
                }
                if (field != null) {
                    field.setAccessible(true);
                    field.set(imm, null);
                }
            } catch (Throwable t) {

            }
        }
    }

    /**
     * 设置华为手机角标
     * @param context
     * @param num
     */
    public static void setHuaweiBadgeNum(Context context, int num) {
        try {
            if (RomVersionUtils.isHuawei()) {
                if (num == 0) {
                    setHuaweiBadgenumber(0);
                }
                int badgeNumber = getHuaweiBadgenumber() + num;
                Bundle bunlde = new Bundle();
                bunlde.putString("package", context.getPackageName()); // 包名
                bunlde.putString("class", "com.caiyu.chat.ui.start.StartActivity"); // 应用入口Activity类
                bunlde.putInt("badgenumber", badgeNumber);
                context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
                setHuaweiBadgenumber(badgeNumber);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 设置Vivo手机角标
     * @param context
     * @param num
     */
    public static void setVivoBadgeNum(Context context, int num) {
        try {
            if (RomVersionUtils.isVivo()) {
                if (num == 0) {
                    setVivoBadgenumber(0);
                }
                int badgeNumber = getVivoBadgenumber() + num;

                Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
                intent.putExtra("packageName", context.getPackageName());
                String launchClassName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
                intent.putExtra("className", launchClassName);
                intent.putExtra("notificationNum", badgeNumber);
                context.sendBroadcast(intent);

                setVivoBadgenumber(badgeNumber);
            }
        } catch (Exception e) {

        }
    }

    public static int getHuaweiBadgenumber() {
        return huaweiBadgenumber;
    }

    public static void setHuaweiBadgenumber(int huaweiBadgenumber) {
        DeviceUtils.huaweiBadgenumber = huaweiBadgenumber;
    }

    public static int getVivoBadgenumber() {
        return vivoBadgenumber;
    }

    public static void setVivoBadgenumber(int vivoBadgenumber) {
        DeviceUtils.vivoBadgenumber = vivoBadgenumber;
    }
}
