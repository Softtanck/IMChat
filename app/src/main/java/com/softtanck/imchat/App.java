package com.softtanck.imchat;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
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
    private SDKReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化百度地图
         */
        SDKInitializer.initialize(this);
        /**
         * 初始化融云
         */
        RongIMClient.init(this);
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        Log.d("Tanck", "start");
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


    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d("Tanck", "action: " + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Log.d("Tanck", "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Log.d("Tanck", "网络错误");
            }
        }
    }
}
