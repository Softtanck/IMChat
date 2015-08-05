package com.softtanck.imchat.view.recorderview;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.softtanck.imchat.utils.SoundMeter;

public class MySounderView extends TextView {

    private boolean isPressed;

    private boolean isCancled;

    private boolean isDoubleClicked;

    private int DEFALUT_TIME = 500;

    private int mHeight;
    private int mWidth;

    private long oldTime;

    private long currentTime;

    private SoundMeter msounder;

    private OnRecordListener listener;

    public void setOnRecordListener(OnRecordListener listener) {
        this.listener = listener;
    }

    private String fileSrc;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (0x1 == msg.what && !isDoubleClicked) {
                Log.d("Tanck", "开始录音");
                fileSrc = String.valueOf(System.currentTimeMillis());
                if (null != listener)
                    listener.onStartRecord(fileSrc);
                msounder.start(fileSrc);
            }
        }

        ;
    };

    public MySounderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public MySounderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySounderView(Context context) {
        this(context, null);
    }

    private void initView() {
        msounder = new SoundMeter(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (isCancled && action == MotionEvent.ACTION_MOVE) {
            msounder.stop();
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isPressed) {
                    currentTime = System.currentTimeMillis();
                    if (DEFALUT_TIME > (currentTime - oldTime)) {
                        Log.d("Tanck", "click");
                        isDoubleClicked = true;
                        msounder.stop();
                        oldTime = currentTime;
                        return false;
                    }
                }
                oldTime = System.currentTimeMillis();
                isCancled = false;
                isPressed = true;
                isDoubleClicked = false;
                Log.d("Tanck", "DOWN");
                mHandler.sendEmptyMessageDelayed(0x1, 1000);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("Tanck", "MOVE");
                if (event.getY() < 0) {
                    Log.d("Tanck", "Over");
                    msounder.stop();
                    isCancled = true;
                    if (null != listener)
                        listener.onEndRecod(fileSrc);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isCancled) {
                    Log.d("Tanck", "UP");
                    if (null != listener)
                        listener.onEndRecod(fileSrc);
                    msounder.stop();
                }
                break;
        }
        return true;
    }

    /**
     * 录音接口
     *
     * @author Administrator
     */
    public interface OnRecordListener {

        /**
         * 开始录音的时候
         */
        public void onStartRecord(String fileSrc);

        /**
         * 结束录音的时候
         */
        public void onEndRecod(String fileSrc);
//
//        /**
//         * 快速点击的时候
//         */
//        public void onQuickClik();

    }

}
