package com.pinthecloud.item.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitmapUtil {

	public static final int ITEM_IMAGE_SIZE = 612;
	public static final int ITEM_IMAGE_SMALL_SIZE = 150;
	
	public static final int PROFILE_IMAGE_SIZE = 612;
	public static final int PROFILE_IMAGE_SMALL_SIZE = 75;

	public static final String SMALL_POSTFIX = "_small";


	public static Bitmap decodeInSampleSize(Context context, String imagePath, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imagePath, options);
	}


	public static Bitmap decodeInSampleSize(Resources res, int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}


	public static Bitmap decodeInSampleSize(byte[] encodeByte, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
	}


	public static Bitmap decodeInSampleSize(Bitmap bitmap, int reqWidth, int reqHeight) {
		ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		return decodeInSampleSize(b, reqWidth, reqHeight);
	}


	private static int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight
					&& (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}


	public static int getImageOrientation(String imagePath) {
		try {
			if(imagePath != null){
				ExifInterface exif = new ExifInterface(imagePath);
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
				}
			}
		} catch (IOException e) {
		}
		return 0;
	}


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(degree);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}


	public static Bitmap refineItemImageBitmap(Context context, String imagePath){
		Bitmap bitmap = decodeInSampleSize(context, imagePath, ITEM_IMAGE_SIZE, ITEM_IMAGE_SIZE);
		return rotate(bitmap, getImageOrientation(imagePath));
	}


	public static Bitmap refineProfileImageBitmap(Resources resources, int id){
		Bitmap bitmap = decodeInSampleSize(resources, id, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		return refineProfileImageBitmap(bitmap, null);
	}


	public static Bitmap refineProfileImageBitmap(Context context, String imagePath){
		Bitmap bitmap = decodeInSampleSize(context, imagePath, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
		return refineProfileImageBitmap(bitmap, imagePath);
	}


	public static Bitmap refineProfileImageBitmap(Bitmap bitmap, String imagePath){
		bitmap = BitmapUtil.decodeInSampleSize(bitmap, BitmapUtil.PROFILE_IMAGE_SIZE, BitmapUtil.PROFILE_IMAGE_SIZE);

		// Crop
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width >= height){
			bitmap = Bitmap.createBitmap(bitmap, width/2 - height/2, 0, height, height);
		} else{
			bitmap = Bitmap.createBitmap(bitmap, 0, height/2 - width/2, width, width);
		}

		// Resize and Rotate
		bitmap = Bitmap.createScaledBitmap(bitmap, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, true);
		return rotate(bitmap, getImageOrientation(imagePath));
	}
}
