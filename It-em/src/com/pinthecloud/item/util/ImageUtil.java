package com.pinthecloud.item.util;

import android.content.res.Resources;
import android.graphics.Bitmap;

public class ImageUtil {

	public static final int PROFILE_IMAGE_SIZE = 212;
	public static final int PROFILE_THUMBNAIL_IMAGE_SIZE = 75;
	public static final String PROFILE_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";

	public static final int ITEM_IMAGE_WIDTH = 640;
//	public static final int ITEM_PREVIEW_IMAGE_WIDTH = 315;
	public static final int ITEM_THUMBNAIL_IMAGE_SIZE = 212;
//	public static final String ITEM_PREVIEW_IMAGE_POSTFIX = "_preview";
	public static final String ITEM_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";


	public static Bitmap refineItemImage(String imagePath, int maxWidth){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(imagePath, maxWidth, -1);

		// Scale by maxWidth
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > maxWidth) {
			bitmap = BitmapUtil.scale(bitmap, maxWidth, (int)(height*((float)maxWidth/width)));
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineSquareImage(String imagePath, int size){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(imagePath, size, size);
		bitmap = BitmapUtil.cropSquare(bitmap);
		if(bitmap.getWidth() > size){
			bitmap = BitmapUtil.scale(bitmap, size, size);
		}
		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineSquareImage(Resources resources, int id, int size){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(resources, id, size, size);
		return refineSquareImage(bitmap, size);
	}


	public static Bitmap refineSquareImage(Bitmap bitmap, int size){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, size, size);
		bitmap = BitmapUtil.cropSquare(bitmap);
		if(bitmap.getWidth() > size){
			bitmap = BitmapUtil.scale(bitmap, size, size);
		}
		return bitmap;
	}
}
