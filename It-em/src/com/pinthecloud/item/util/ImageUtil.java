package com.pinthecloud.item.util;

import android.content.res.Resources;
import android.graphics.Bitmap;

public class ImageUtil {

	public static final int PROFILE_IMAGE_SIZE = 212;
	public static final int PROFILE_THUMBNAIL_IMAGE_SIZE = 75;
	public static final String PROFILE_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";
	
	public static final int ITEM_IMAGE_WIDTH = 640;
	public static final int ITEM_PREVIEW_IMAGE_WIDTH = 315;
	public static final int ITEM_THUMBNAIL_IMAGE_SIZE = 212;
	public static final String ITEM_PREVIEW_IMAGE_POSTFIX = "_preview";
	public static final String ITEM_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";
	
	public static final String JPG = ".jpg";
	
	
	public static Bitmap refineItemImage(String imagePath, int maxWidth, boolean recycle){
		try{
			int orientation = BitmapUtil.getImageOrientation(imagePath);
			Bitmap bitmap = null;
			if(orientation == 0 || orientation == 180){
				bitmap = BitmapUtil.decodeInSampleSize(imagePath, maxWidth, -1);
			} else {
				bitmap = BitmapUtil.decodeInSampleSize(imagePath, -1, maxWidth);
			}
			bitmap = BitmapUtil.rotate(bitmap, orientation, recycle);

			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if(width > maxWidth) {
				bitmap = BitmapUtil.scale(bitmap, maxWidth, (int)(height*((double)maxWidth/width)), recycle);
			}

			return bitmap;
		} catch(OutOfMemoryError e) {
			return null;
		}
	}


	public static Bitmap refineSquareImage(String imagePath, int size, boolean recycle){
		try{
			Bitmap bitmap = BitmapUtil.decodeInSampleSize(imagePath, size, size);
			bitmap = BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath), recycle);
			bitmap = BitmapUtil.cropSquare(bitmap, recycle);
			if(bitmap.getWidth() > size){
				bitmap = BitmapUtil.scale(bitmap, size, size, recycle);
			}
			return bitmap;
		} catch(OutOfMemoryError e) {
			return null;
		}
	}


	public static Bitmap refineSquareImage(Resources resources, int id, int size, boolean recycle){
		try{
			Bitmap bitmap = BitmapUtil.decodeInSampleSize(resources, id, size, size);
			bitmap = refineSquareImage(bitmap, size, recycle);
			return bitmap;
		} catch(OutOfMemoryError e) {
			return null;
		}
	}


	public static Bitmap refineSquareImage(Bitmap bitmap, int size, boolean recycle){
		try{
			bitmap = BitmapUtil.decodeInSampleSize(bitmap, size, size, recycle);
			bitmap = BitmapUtil.cropSquare(bitmap, recycle);
			if(bitmap.getWidth() > size){
				bitmap = BitmapUtil.scale(bitmap, size, size, recycle);
			}
			return bitmap;
		} catch(OutOfMemoryError e) {
			return null;
		}
	}
}
