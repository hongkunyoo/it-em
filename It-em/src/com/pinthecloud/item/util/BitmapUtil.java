package com.pinthecloud.item.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

public class BitmapUtil {

	public static final int SMALL_SIZE = 100;
	public static final int BIG_SIZE = 320;
	
	public static final String SMALL_POSTFIX = "_small";
	public static final String PNG = ".png";
	

	public static Bitmap decodeInSampleSize(Context context, Uri imageUri, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap bitmap = null;
		try {
			BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options);
		} catch (FileNotFoundException e) {
		}

		return bitmap;
	}


	public static Bitmap decodeInSampleSize(Resources res, int resId, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}


	public static Bitmap decodeInSampleSize(byte[] encodeByte, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
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
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}


	public static int getImageOrientation(String imagePath) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imagePath);
		} catch (IOException e) {
		}

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
		default:
			return 0;
		}
	}


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}


	public static Bitmap crop(Bitmap bitmap, int xOffset, int yOffset, int width, int height) {
		return Bitmap.createBitmap(bitmap, xOffset, yOffset, width, height);
	}
}
