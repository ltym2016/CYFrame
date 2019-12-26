package com.caiyu.lib_base.permission;

import java.util.List;

/**
 * @author luys
 * @describe 权限申请回调接口
 * @date 2019/2/27
 * @email samluys@foxmail.com
 */
public interface PermissionRequestListener {

    /**
     * 允许授予
     */
    void onGranted();

    /**
     * 拒绝操作
     */
    void onDenied(List<String> data);

//    /**
//     * 权限被拒绝后下次再次请求的时候，添加说明，表明申请权限的原因
//     */
//    String showPermissionRational();
//
//    /**
//     * 总是被拒绝（用户选择了“不再提示”），这里可以设置提示信息提示用户去设置授权
//     */
//    String alwaysDeniedMessage();

}
