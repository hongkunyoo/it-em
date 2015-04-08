package com.pinthecloud.item.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class RoundedTopCornerTransformation implements Transformation {

	private int radius;
	private int margin;

	public RoundedTopCornerTransformation(int radius, int margin) {
		this.radius = radius;
		this.margin = margin;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		int width = source.getWidth();
		int height = source.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(source, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));

		Rect rect = new Rect(margin, margin, width - margin, height - margin);
		RectF rectF = new RectF(rect);
		Rect bottomRect = new Rect(margin, height/2 - margin, width - margin, height - margin);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		canvas.drawRect(bottomRect, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		if (source != bitmap) {
            source.recycle();
        }

		return bitmap;
	}

	@Override
	public String key() {
		return "RoundedTopCornerTransformation(radius=" + radius + ", margin=" + margin + ")";
	}
}
