package com.softtanck.imforchat;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 7/8/2015.
 */
public class ImageLoaderConfig {

    private Context context;

    private ImageLoaderConfiguration config;

    private int cacheSize;

    private File cacheDir;

    public ImageLoaderConfig(Context context) {
        init(context);
    }

    /**
     * 初始化数据
     *
     * @param context
     */
    private void init(Context context) {
        cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);//内存缓存大小
        cacheDir = StorageUtils.getOwnCacheDirectory(context, ConValue.App.UNIVERSAL_IMAGE_PATH);//本地缓存目录
        config = new ImageLoaderConfiguration.Builder(context)
                // max width, max height，即保存的每个缓存文件的最大长宽
                .memoryCacheExtraOptions(480, 800)
                        // 线程池内加载的数量
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                        // You can pass your own memory cache
                        // implementation/你可以通过自己的内存缓存实现
                .memoryCache(new UsingFreqLimitedMemoryCache(1 * 1024 * 1024))
                        //本地存放的大小
                .memoryCacheSize(cacheSize).diskCacheSize(50 * 1024 * 1024)
                        // 将保存的时候的URI名称用MD5 加密
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())

                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // 缓存的文件数量
                .discCacheFileCount(100)
                        // 自定义缓存路径
                .discCache(new UnlimitedDiskCache(cacheDir))

                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        // connectTimeout
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                        // (5
                        // s),
                        // readTimeout
                        // (30
                        // s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();// 开始构建
    }


    /**
     * 获取ImageLoader整体配置
     *
     * @return
     */
    public ImageLoaderConfiguration getConfig() {
        return config;
    }

    /**
     * 设置显示加载图标的方式
     *
     * @return
     */
    public DisplayImageOptions setImageLoaderByIcon() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(null) // 加载中的
                .showImageForEmptyUri(null)
                .showImageOnFail(null)//加载失败的
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(100))//设置图片渐显的时间
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        return options;
    }

    /**
     * 设置显示加载图片的方式:暂时先考虑用565
     *
     * @return
     */
    public DisplayImageOptions setImageLoaderByNormal() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(R.drawable.bg_chat_img_fail_message) // 加载中的
                .showImageForEmptyUri(null)
                .showImageOnFail(R.drawable.bg_chat_img_fail_message)//加载失败的
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(100))//设置图片渐显的时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    /**
     * 设置显示加载图片的方式:暂时先考虑用565
     *
     * @return
     */
    public DisplayImageOptions setImageLoaderByBig() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(R.drawable.bg_chat_img_fail_message) // 加载中的
                .showImageForEmptyUri(R.drawable.bg_chat_img_fail_message)
                .showImageOnFail(R.drawable.bg_chat_img_fail_message)//加载失败的
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(100))//设置图片渐显的时间
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.NONE_SAFE)
                .build();
        return options;
    }
}
