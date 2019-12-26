package com.caiyu.lib_base.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luys
 * @describe Activity任务栈
 * @date 2019/5/30
 * @email samluys@foxmail.com
 */
public class ActivityStackUtils {

    private static ActivityStackUtils instance;

    public static ActivityStackUtils getInstance() {
        if (instance == null) {
            synchronized (ActivityStackUtils.class) {
                if (instance == null) {
                    instance = new ActivityStackUtils();
                }
            }
        }
        return instance;
    }


    private List<Activity> activityStack = new ArrayList<>();

    public void pushActivity(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    public void clearActivityStack() {
        for (Activity activity : activityStack) {
            activity.finish();
        }
        activityStack.clear();
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public List<Activity> getActivties() {
        return activityStack;
    }
}
