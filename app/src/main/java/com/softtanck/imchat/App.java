package com.softtanck.imchat;

import android.app.Application;

import io.rong.imlib.RongIMClient;

/**
 * @author : Tanck
 * @Description : TODO
 * @date 7/29/2015
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化融云
         */
        RongIMClient.init(this);
    }
}
