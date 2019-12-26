package com.caiyu.lib_base.constants;

import com.caiyu.lib_base.R;
import com.caiyu.lib_base.utils.Utils;

import java.io.File;

/**
 * @author luys
 * @describe
 * @date 2019-05-16
 * @email samluys@foxmail.com
 */
public class Constants {


    /**
     * 接口地址
     */
    public final static String HOST = Utils.getStringFromConfig(R.string.host);
    /**
     * 上传地址
     */
    public final static String UPLOAD_HOST = Utils.getStringFromConfig(R.string.upload_host);

    /**
     * 上传图片临时路径
     */
    public static final String TEMP = "temp" + File.separator;

    /**
     * "false"
     */
    public final static String FALSE = "false";

    /**
     * 请求时长
     */
    public static final long HTTP_TIME = 30;

    /**
     * 主渠道
     */
    public static final String SP_CHANNEL_ID = "sp_channel_id";

    /**
     * 自渠道
     */
    public static final String SP_SUB_CHANNEL_ID = "sp_sub_channel_id";


    /**
     * 文件前缀
     */
    public static final String FILE_PREX = "file://";

}
