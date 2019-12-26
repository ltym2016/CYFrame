package com.caiyu.lib_base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * @author luys
 * @describe
 * @date 2019/4/22
 * @email ltym_lys@126.com
 */
public class SpUtils {

    private static final String PREFERENCE_NAME = "chi_ji_pre";
    private static SharedPreferences sharedPreferences;
    private static volatile SpUtils mSharedPreferencesUtil;
    private static SharedPreferences.Editor editor;

    private SpUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 单例模式，获取instance实例
     *
     * @return
     */
    public static SpUtils getInstance() {
        if (mSharedPreferencesUtil == null) {
            synchronized (SpUtils.class) {
                if (mSharedPreferencesUtil == null) {
                    mSharedPreferencesUtil = new SpUtils(ApplicationUtils.getApp());
                }
            }
        }
        return mSharedPreferencesUtil;
    }

    /**
     * 存储
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
