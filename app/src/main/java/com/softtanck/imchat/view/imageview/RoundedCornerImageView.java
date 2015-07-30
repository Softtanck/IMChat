package com.softtanck.imchat.view.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 圆角ImageView
 * 
 * @author wangpeng
 * @date 2015-01-07
 */
public class RoundedCornerImageView extends BaseImageView {

	public RoundedCornerImageView(Context context) {
		super(context);
	}

	public RoundedCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static Bitmap getBitmap(int width, int height, int roundSize) {
		final int color = 0xff424242;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundSize, roundSize, paint);

		paint.setXfermode(new android.graphics.PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return getBitmap(getWidth(), getHeight(), dip(getContext(), 10));
	}

	public static int dip(Context context, int value) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
	}

}
