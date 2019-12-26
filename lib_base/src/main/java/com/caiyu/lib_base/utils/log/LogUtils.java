package com.caiyu.lib_base.utils.log;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @author luys
 * @describe
 * @date 2019/3/4
 * @email samluys@foxmail.com
 */
public class LogUtils {

    /**
     * debug 模式开启LOG
     */
    private final boolean debug;

    /**
     * 是否开启保存LOG信息到本地
     */
    private final boolean isSaveLocal;
    /**
     * 保存指定的TAG
     */
    private final String saveTag;
    /**
     * 保存Log文件路径
     */
    private final String savePath;
    /**
     * 设置全局TAG
     */
    private final String tag;

    private LogUtils() {
        throw new UnsupportedOperationException("");
    }

    private LogUtils(Builder builder) {
        isSaveLocal = builder.isSaveLocal;
        savePath = builder.savePath;
        saveTag = builder.saveTag;
        tag = builder.tag;
        debug = builder.debug;
        init();
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {

        boolean debug;
        boolean isSaveLocal;
        String saveTag;
        String savePath;
        String tag;

        private Builder() {

        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder saveTag(String saveTag) {
            this.saveTag = saveTag;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder savePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public Builder isSaveLocal(boolean isSaveLocal) {
            this.isSaveLocal = isSaveLocal;
            return this;
        }

        public LogUtils build () {
            return new LogUtils(this);
        }
    }

    private void init() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(new LogCatStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(TextUtils.isEmpty(tag) ? "QF_LOG" : tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();


        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return debug;
            }
        });

        // 保存指定TAG的LOG信息
        if(isSaveLocal) {
            if (!TextUtils.isEmpty(savePath) && !TextUtils.isEmpty(saveTag)) {
                FormatStrategy formatStrategy2 = CsvFormatStrategy.newBuilder()
                        .tag(saveTag)
                        .build(savePath);
                Logger.addLogAdapter(new DiskLogAdapter(formatStrategy2));
            } else if (!TextUtils.isEmpty(savePath)) {
                Logger.addLogAdapter(new DiskLogAdapter(savePath));
            } else if (!TextUtils.isEmpty(saveTag)) {
                FormatStrategy formatStrategy2 = CsvFormatStrategy.newBuilder()
                        .tag(saveTag)
                        .build();
                Logger.addLogAdapter(new DiskLogAdapter(formatStrategy2));
            } else {
                Logger.addLogAdapter(new DiskLogAdapter());
            }
        }
    }

    /**
     * 解决在Android Studio 3.0以上的版本中Logger无法对齐的问题
     */
    public static class LogCatStrategy implements LogStrategy {

        @Override
        public void log(int priority, String tag, String message) {
            Log.println(priority, randomKey() + tag, message);
        }

        private int last;

        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);
        }
    }

    /**
     * @param tag
     * @param message
     * @return void
     * @description: logcat info
     */
    public static void i(String tag, String message) {
        Logger.t(tag).i(message);
    }

    public static void i(String message) {
        Logger.i(message);
    }

    /**
     * @param tag
     * @param message
     * @return void
     * @description: logcat warn
     */
    public static void w(String tag, String message) {
        Logger.t(tag).w(message);
    }

    public static void w(String message) {
        Logger.w(message);
    }

    /**
     * @param tag
     * @param message
     * @return void
     * @description: logcat debug
     */
    public static void d(String tag, String message) {
        Logger.t(tag).d(message);
    }

    public static void d(String message) {
        Logger.d(message);
    }

    /**
     * Logger.d(MAP);
     * Logger.d(SET);
     * Logger.d(LIST);
     * Logger.d(ARRAY);
     *
     * @param object
     */
    public static void d(Object object) {
        Logger.d(object);
    }

    /**
     * @param tag
     * @param message
     * @return void
     * @description: logcat error
     */
    public static void e(String tag, String message) {
        Logger.t(tag).e(message);
    }

    public static void e(String message) {
        Logger.e(message);
    }

    /**
     * @param message
     * @return void
     * @description: 分段输出string的内容
     */
    public static void v(String tag, String message) {
        Logger.t(tag).v(message);
    }

    public static void v(String message) {
        Logger.v(message);
    }


    public static void wtf(String tag, String message) {
        Logger.t(tag).wtf(message);
    }

    public static void wtf(String message) {
        Logger.wtf(message);
    }

    public static void json(String tag, String json) {
        if (!TextUtils.isEmpty(json)) {
            Logger.t(tag).json(json);
        }
    }

    public static void json(String json) {
        if (!TextUtils.isEmpty(json)) {
            Logger.json(json);
        }
    }

    public static void xml(String tag, String xml) {
        if (!TextUtils.isEmpty(xml)) {
            Logger.t(tag).xml(xml);
        }
    }

    public static void xml(String xml) {
        if (!TextUtils.isEmpty(xml)) {
            Logger.xml(xml);
        }
    }
}
