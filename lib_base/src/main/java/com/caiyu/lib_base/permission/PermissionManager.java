package com.caiyu.lib_base.permission;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

import com.samluys.jutils.ToastUtils;
import com.samluys.jutils.Utils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author luys
 * @describe 权限管理 基于AndPermission https://github.com/yanzhenjie/AndPermission
 * @date 2019/2/27
 * @email samluys@foxmail.com
 */
public class PermissionManager {

    public final static int REQUEST_CODE_SETTING = 1;
    public SoftReference<PermissionRequestListener> mListener;

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null) {
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }


    /**
     * 申请权限
     * @param listener 结果回调
     * @param permissionGroup 权限或者权限组
     */
    public void requestPermission(final PermissionRequestListener listener,
                                   final String... permissionGroup) {

        mListener = new SoftReference<>(listener);
        // Android 6.0以上运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查是否有权限，有就直接return
            if (hasPermissions(Utils.getContext().getApplicationContext(), permissionGroup)) {
                if (mListener.get() != null) {
                    mListener.get().onGranted();
                }

                return;
            }

            // 没有权限则申请权限
            AndPermission.with(Utils.getContext().getApplicationContext())
                    .runtime()
                    .permission(permissionGroup)
                    // 当用户不给权限时优先执行
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data,
                                                  final RequestExecutor executor) {
                            // 当用户拒绝之后，下一次请求此权限的时候，作出说明，以便用户判断是否需要授权
                            executor.execute();
                        }
                    })
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            if (mListener.get() != null) {
                                mListener.get().onGranted();
                            }
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {

                            if(AndPermission.hasAlwaysDeniedPermission(Utils.getContext().getApplicationContext(),permissionGroup)) {
                                // 用户勾选了"不再提示"，并拒绝，这个时候可以提示用户去设置里允许
                                ToastUtils.showLong("请检查您的手机权限");
                            }

                            if (mListener.get() != null) {
                                mListener.get().onDenied(data);
                            }

                        }
                    })
                    .start();
        } else {
            if (mListener.get() != null) {
                mListener.get().onGranted();
            }
        }
    }


//    /**
//     * 用户拒绝后，下次请求弹框说明，申请权限的原因，并引导用户去收取
//     *
//     * @param context
//     * @param listener
//     */
//    private static void showRationalDialog(final Context context, PermissionRequestListener listener, final RequestExecutor executor) {
//        new AlertDialog.Builder(context)
//                .setCancelable(false)
//                .setTitle("权限申请提醒")
//                .setMessage(listener.showPermissionRational())
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        executor.execute();
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        executor.cancel();
//                        dialog.dismiss();
//                    }
//                }).show();
//    }

//    /**
//     * 引导用户去设置页面授权
//     * @param context
//     * @param listener
//     */
//    private static void showSettingDialog(final Context context, PermissionRequestListener listener) {
//        new AlertDialog.Builder(context)
//                .setCancelable(false)
//                .setMessage(listener.alwaysDeniedMessage())
//                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        PermissionManager.goSetting((Activity) context);
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//    }

    /**
     * 判断是否有这个权限或权限组
     * @param context
     * @param permissionGroup
     * @return
     */
    public boolean hasPermissions (final Context context, final String... permissionGroup) {
        return AndPermission.hasPermissions(Utils.getContext().getApplicationContext(), permissionGroup);
    }


    /**
     * 提示用户去系统设置中授权
     *
     * @param activity
     */
    public void goSetting(Activity activity) {

        AndPermission.with(activity)
                .runtime()
                .setting()
                .start(REQUEST_CODE_SETTING);
    }

    /**
     * 检查App是否具有悬浮窗权限
     * @param context
     * @return
     */
    public boolean checkFloatPermission(Context context) {

        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }
}
