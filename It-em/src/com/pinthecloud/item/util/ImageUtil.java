package com.pinthecloud.item.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class ImageUtil {

	public static final int PROFILE_IMAGE_SIZE = 160;
	public static final int PROFILE_THUMBNAIL_IMAGE_SIZE = 32;
	public static final String PROFILE_THUMBNAIL_IMAGE_POSTFIX = "_thumbnail";

	public static final int ITEM_IMAGE_SIZE = 600;
	public static final int ITEM_PREVIEW_IMAGE_SIZE = 192;
	public static final String ITEM_PREVIEW_IMAGE_POSTFIX = "_preview";


	public static Bitmap refineItemImage(Context context, String imagePath){
		Bitmap bitmap = BitmapUtil.decodeInSampleSize(context, imagePath, ITEM_IMAGE_SIZE, ITEM_IMAGE_SIZE);
		return refineItemImage(bitmap, imagePath);
	}


	public static Bitmap refineItemImage(Bitmap bitmap, String imagePath){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_IMAGE_SIZE, ITEM_IMAGE_SIZE);

		// Scale by ITEM_IMAGE_SIZE
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width >= height && height > ITEM_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, (int)(ITEM_IMAGE_SIZE*((float)width/height)), ITEM_IMAGE_SIZE, true);
		} else if(width < height && width > ITEM_IMAGE_SIZE) {
			bitmap = Bitmap.createScaledBitmap(bitmap, ITEM_IMAGE_SIZE, (int)(ITEM_IMAGE_SIZE*((float)height/width)), true);
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineItemPreviewImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, ITEM_PREVIEW_IMAGE_SIZE, ITEM_PREVIEW_IMAGE_SIZE);

		// Scale by ITEM_IMAGE_PREVIEW_SIZE
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width >= height && height > ITEM_PREVIEW_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, (int)(ITEM_PREVIEW_IMAGE_SIZE*((float)width/height)), ITEM_PREVIEW_IMAGE_SIZE, true);
		} else if(width < height && width > ITEM_PREVIEW_IMAGE_SIZE) {
			bitmap = Bitmap.createScaledBitmap(bitmap, ITEM_PREVIEW_IMAGE_SIZE, (int)(ITEM_PREVIEW_IMAGE_SIZE*((float)height/width)), true);
		}

		return bitmap;
	}


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

		// If image is too big, Resize
		if(bitmap.getWidth() > PROFILE_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, true);
		}

		return BitmapUtil.rotate(bitmap, BitmapUtil.getImageOrientation(imagePath));
	}


	public static Bitmap refineProfileThumbnailImage(Bitmap bitmap){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE);

		// If image is too big, Resize
		if(bitmap.getWidth() > PROFILE_THUMBNAIL_IMAGE_SIZE){
			bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_THUMBNAIL_IMAGE_SIZE, PROFILE_THUMBNAIL_IMAGE_SIZE, true);
		}

		return bitmap;
	}
}
