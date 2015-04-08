package com.pinthecloud.item.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitmapUtil {

	public static Bitmap decodeInSampleSize(String imagePath, int reqWidth, int reqHeight) {
		try{
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imagePath, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(imagePath, options);
		}catch(OutOfMemoryError e){
			return null;
		}
	}


	public static Bitmap decodeInSampleSize(Resources res, int resId, int reqWidth, int reqHeight) {
		try{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);
			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		}catch(OutOfMemoryError e){
			return null;
		}
	}


	public static Bitmap decodeInSampleSize(byte[] encodeByte, int reqWidth, int reqHeight) {
		try{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);
		}catch(OutOfMemoryError e){
			return null;
		}
	}


	public static Bitmap decodeInSampleSize(Bitmap bitmap, int reqWidth, int reqHeight, boolean recycle) {
		if(bitmap.getWidth() >= reqWidth*2 && bitmap.getHeight() >= reqHeight*2) {
			try{
				ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
				byte[] byteArray = byteArrayBitmapStream.toByteArray();
				Bitmap output = decodeInSampleSize(byteArray, reqWidth, reqHeight);
				if(recycle && bitmap != output){
					bitmap.recycle();
				}
				return output;
			}catch(OutOfMemoryError e){
				return null;
			}
		} else {
			return bitmap;
		}
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
		if(imagePath != null){
			try {
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
				default:
					break;
				}
			} catch (IOException e) {
				// Do nothing
			}
		}
		return 0;
	}


	public static Bitmap rotate(Bitmap bitmap, int degree, boolean recycle) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		try{
			Bitmap output = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
			if(recycle && bitmap != output){
				bitmap.recycle();
			}
			return output;
		}catch(OutOfMemoryError e){
			return null;
		}
	}


	public static Bitmap scale(Bitmap bitmap, int width, int height, boolean recycle) {
		try{
			Bitmap output = Bitmap.createScaledBitmap(bitmap, width, height, true);
			if(recycle && bitmap != output){
				bitmap.recycle();
			}
			return output;
		}catch(OutOfMemoryError e){
			return null;	
		}
	}


	public static Bitmap crop(Bitmap bitmap, int x, int y, int width, int height, boolean recycle) {
		try{
			Bitmap output = Bitmap.createBitmap(bitmap, x, y, width, height);
			if(recycle && bitmap != output){
				bitmap.recycle();
			}
			return output;
		}catch(OutOfMemoryError e){
			return null;	
		}
	}

	public static Bitmap cropSquare(Bitmap bitmap, boolean recycle) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if(width > height){
			bitmap = BitmapUtil.crop(bitmap, (int)((float)width/2 - (float)height/2), 0, height, height, recycle);
		} else if(width < height) {
			bitmap = BitmapUtil.crop(bitmap, 0, (int)((float)height/2 - (float)width/2), width, width, recycle);
		}
		return bitmap;
	}
}
