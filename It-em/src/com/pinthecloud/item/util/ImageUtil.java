package com.pinthecloud.item.util;

import android.content.res.Resources;
import android.graphics.Bitmap;

public class ImageUtil {

	public static final int PROFILE_IMAGE_SIZE = 212;
	public static final int PROFILE_THUMBNAIL_IMAGE_SIZE = 75;
	public static final String PROFILE_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";

	public static final int ITEM_IMAGE_WIDTH = 640;
	public static final int ITEM_THUMBNAIL_IMAGE_SIZE = 212;
	public static final String ITEM_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";


	public static Bitmap refineItemImage(String imagePath, int maxWidth){
		Bitmap bitmap = null;
		
		int orientation = BitmapUtil.getImageOrientation(imagePath);
		if(orientation == 0 || orientation == 180){
			bitmap = BitmapUtil.decodeInSampleSize(imagePath, maxWidth, -1);

			// Scale by maxWidth
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if(width > maxWidth) {
				bitmap = BitmapUtil.scale(bitmap, maxWidth, (int)(height*((double)maxWidth/width)));
			}
		} else {
			bitmap = BitmapUtil.decodeInSampleSize(imagePath, -1, maxWidth);

			// Scale by maxWidth
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if(height > maxWidth) {
				bitmap = BitmapUtil.scale(bitmap, (int)(width*((double)maxWidth/height)), maxWidth);
			}
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
