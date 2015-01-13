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
			bitmap = Bitmap.createScaledBitmap(bitmap, ITEM_IMAGE_WIDTH, (int)(height*((float)ITEM_IMAGE_WIDTH/width)), true);
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}

	public static Bitmap refineItemPreviewImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_PREVIEW_IMAGE_WIDTH, -1);

		// Scale by ITEM_IMAGE_PREVIEW_WIDTH
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > ITEM_PREVIEW_IMAGE_WIDTH) {
			bitmap = Bitmap.createScaledBitmap(bitmap, ITEM_PREVIEW_IMAGE_WIDTH, (int)(height*((float)ITEM_PREVIEW_IMAGE_WIDTH/width)), true);
		}

		return bitmap;
	}

	public static Bitmap refineItemThumbnailImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_THUMBNAIL_IMAGE_SIZE, ITEM_THUMBNAIL_IMAGE_SIZE);

		// Crop to Square
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > height){
			bitmap = Bitmap.createBitmap(bitmap, (int)((float)width/2 - (float)height/2), 0, height, height);
		} else if(width < height) {
			bitmap = Bitmap.createBitmap(bitmap, 0, (int)((float)height/2 - (float)width/2), width, width);
		}

		// If image is big, Resize
		if(bitmap.getWidth() > ITEM_THUMBNAIL_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, ITEM_THUMBNAIL_IMAGE_SIZE, ITEM_THUMBNAIL_IMAGE_SIZE, true);
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
			bitmap = Bitmap.createBitmap(bitmap, (int)((float)width/2 - (float)height/2), 0, height, height);
		} else if(width < height) {
			bitmap = Bitmap.createBitmap(bitmap, 0, (int)((float)height/2 - (float)width/2), width, width);
		}

		// If image is big, Resize
		if(bitmap.getWidth() > PROFILE_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, true);
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineProfileThumbnailImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE);

		// If image is big, Resize
		if(bitmap.getWidth() > PROFILE_THUMBNAIL_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE, true);
		}

		return bitmap;
	}
}
