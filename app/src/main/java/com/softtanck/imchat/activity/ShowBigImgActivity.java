package com.softtanck.imchat.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.softtanck.imchat.App;
import com.softtanck.imchat.R;

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
