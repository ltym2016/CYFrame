package com.caiyu.lib_base.http;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.HashMap;

/**
 * @author luys
 * @describe
 * @date 2020-02-10
 * @email samluys@foxmail.com
 */
public class HttpUtils {


    public static Builder init() {
        return new Builder();
    }


    public static class Builder {

        private final HttpConfig mHttpConfig;

        private Builder() {
            mHttpConfig = HttpConfig.getInstance();
        }

        /**
         * 缓存存放的文件名称
         * @param cacheName
         * @return
         */
        public Builder cacheName(String cacheName) {
            mHttpConfig.cacheName = cacheName;
            return this;
        }

        /**
         * 缓存存放的文件最大size
         * @param cacheSize
         * @return
         */
        public Builder cacheSize(int cacheSize) {
            mHttpConfig.cacheSize = cacheSize;
            return this;
        }

        /**
         * 请求超时
         * @param timeout
         * @return
         */
        public Builder timeout(int timeout) {
            mHttpConfig.timeout = timeout;
            return this;
        }

        /**
         * 设置请求头
         * @param hashMap
         * @return
         */
        public Builder headers(HashMap<String,String> hashMap) {
            mHttpConfig.headerHashMap = hashMap;
            return this;
        }

        /**
         * 是否为debug模式
         * @param isDebug
         * @return
         */
        public Builder isDebug(boolean isDebug) {
            mHttpConfig.isDebug = isDebug;
            return this;
        }

        /**
         * 是否开启MD5签名
         * @param isSign
         * @return
         */
        public Builder isSign(boolean isSign) {
            mHttpConfig.isSign = isSign;
            return this;
        }

//        public void forResult(int requestCode) {
//            if (mActivity == null) {
//                return;
//            }
//
//            Intent intent = new Intent(mActivity, PhotoActivity.class);
//
//            if (mFragment != null) {
//                mFragment.startActivityForResult(intent, requestCode);
//            } else {
//                mActivity.startActivityForResult(intent, requestCode);
//            }
//        }
    }
}