package com.softtanck.imforchat.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.softtanck.imforchat.App;
import com.softtanck.imforchat.R;

import uk.co.senab.photoview.PhotoView;

/**
 * @author : Tanck
 * @Description : TODO
 * @date 8/7/2015
 */
public class ShowBigImgActivity extends Activity {
    private PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        imageView = (PhotoView) findViewById(R.id.iv_show_img);
        String url = getIntent().getStringExtra("img_remote");
        showImg(url);
    }

    private void showImg(String url) {
        Log.d("Tanck", "--->" + url);
        App.getInstance().imageLoader.displayImage(url, imageView, App.getInstance().imageLoaderConfig.setImageLoaderByBig(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ((PhotoView) view).setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}
