package com.caiyu.lib_base.utils.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caiyu.lib_base.utils.Utils;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;

/**
 * @author luys
 * @describe This is used to saves log messages to the disk
 * @date 2019/3/5
 * @email samluys@foxmail.com
 */
public class DiskLogAdapter implements LogAdapter {
    @NonNull
    private final FormatStrategy formatStrategy;


    public DiskLogAdapter() {
        formatStrategy = CsvFormatStrategy.newBuilder().build();
    }

    public DiskLogAdapter(String path) {
        formatStrategy = CsvFormatStrategy.newBuilder().build(path);
    }

    public DiskLogAdapter(@NonNull FormatStrategy formatStrategy) {
        this.formatStrategy = Utils.checkNotNull(formatStrategy);
    }

    @Override public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    @Override public void log(int priority, @Nullable String tag, @NonNull String message) {
        formatStrategy.log(priority, tag, message);
    }
}
