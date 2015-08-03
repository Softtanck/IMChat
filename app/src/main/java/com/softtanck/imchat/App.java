package com.softtanck.imchat;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;

import io.rong.imlib.RongIMClient;

/**
 * @author : Tanck
 * @Description : TODO
 * @date 7/29/2015
 */
public class App extends Application {

    public ImageLoader imageLoader;
    public ImageLoaderConfig imageLoaderConfig;
    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化融云
         */
        RongIMClient.init(this);
        mApp = this;
        imageLoader = ImageLoader.getInstance();//图片加载器
        imageLoaderConfig = new ImageLoaderConfig(this);
        imageLoader.init(imageLoaderConfig.getConfig());//初始化ImageLoader
    }

    public static App getInstance() {
        if (null == mApp) {
            mApp = new App();
        }
        return mApp;
    }
}
