package com.pinthecloud.item.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class ImageUtil {

	private static final int PROFILE_IMAGE_SIZE = 212;
	private static final int PROFILE_THUMBNAIL_IMAGE_SIZE = 75;
	public static final String PROFILE_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";

	private static final int ITEM_IMAGE_WIDTH = 640;
	private static final int ITEM_PREVIEW_IMAGE_WIDTH = 315;
	private static final int ITEM_THUMBNAIL_IMAGE_SIZE = 212;
	public static final String ITEM_PREVIEW_IMAGE_POSTFIX = "_preview";
	public static final String ITEM_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";


	/*
	 * Manage Item Image
	 */
	public static Bitmap refineItemImage(Context context, String imagePath){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(context, imagePath, ITEM_IMAGE_WIDTH, ITEM_IMAGE_WIDTH);
		return refineItemImage(bitmap, imagePath);
	}

	public static Bitmap refineItemImage(Bitmap bitmap, String imagePath){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_IMAGE_WIDTH, -1);

		// Scale by ITEM_IMAGE_WIDTH
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > ITEM_IMAGE_WIDTH) {
			bitmap = BitmapUtil.scale(bitmap, ITEM_IMAGE_WIDTH, (int)(height*((float)ITEM_IMAGE_WIDTH/width)));
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}
	
	public static Bitmap refineItemLongImage(Bitmap bitmap, int maxSize){
		ItLog.log("here1");
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, -1, maxSize);

		// Scale by maxSize
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		ItLog.log("here2");
		if(height > maxSize) {
			ItLog.log("here3");
			bitmap = BitmapUtil.scale(bitmap, (int)(width*((float)maxSize/height)), maxSize);
		}
		ItLog.log("here4");
		return bitmap;
	}

	public static Bitmap refineItemPreviewImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_PREVIEW_IMAGE_WIDTH, -1);

		// Scale by ITEM_IMAGE_PREVIEW_WIDTH
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > ITEM_PREVIEW_IMAGE_WIDTH) {
			bitmap = BitmapUtil.scale(bitmap, ITEM_PREVIEW_IMAGE_WIDTH, (int)(height*((float)ITEM_PREVIEW_IMAGE_WIDTH/width)));
		}

		return bitmap;
	}

	public static Bitmap refineItemThumbnailImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_THUMBNAIL_IMAGE_SIZE, ITEM_THUMBNAIL_IMAGE_SIZE);

		// Crop to Square
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > height){
			bitmap = BitmapUtil.crop(bitmap, (int)((float)width/2 - (float)height/2), 0, height, height);
		} else if(width < height) {
			bitmap = BitmapUtil.crop(bitmap, 0, (int)((float)height/2 - (float)width/2), width, width);
		}

		// If image is big, Resize
		if(bitmap.getWidth() > ITEM_THUMBNAIL_IMAGE_SIZE){
			bitmap = BitmapUtil.scale(bitmap, ITEM_THUMBNAIL_IMAGE_SIZE, ITEM_THUMBNAIL_IMAGE_SIZE);
		}

		return bitmap;
	}


	/*
	 * Manage Profile Image
	 */
	public static Bitmap refineProfileImage(Resources resources, int id){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(resources, id, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		return refineProfileImage(bitmap, null);
	}


	public static Bitmap refineProfileImage(Context context, String imagePath){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(context, imagePath, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		return refineProfileImage(bitmap, imagePath);
	}


	public static Bitmap refineProfileImage(Bitmap bitmap, String imagePath){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);

		// Crop to Square
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > height){
			bitmap = BitmapUtil.crop(bitmap, (int)((float)width/2 - (float)height/2), 0, height, height);
		} else if(width < height) {
			bitmap = BitmapUtil.crop(bitmap, 0, (int)((float)height/2 - (float)width/2), width, width);
		}

		// If image is big, Resize
		if(bitmap.getWidth() > PROFILE_IMAGE_SIZE){
			bitmap = BitmapUtil.scale(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineProfileThumbnailImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE);

		// If image is big, Resize
		if(bitmap.getWidth() > PROFILE_THUMBNAIL_IMAGE_SIZE){
			bitmap = BitmapUtil.scale(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE);
		}

		return bitmap;
	}
}
