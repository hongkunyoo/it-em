package com.pinthecloud.item.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.exception.ItException;

public class FileUtil {


	public static Uri getOutputMediaFileUri(){
		return Uri.fromFile(getOutputMediaFile());
	}


	public static File getOutputMediaFile(){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), GlobalVariable.APP_NAME);

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (!mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		Time time = new Time();
		time.setToNow();
		String timeStamp = time.format("%Y%m%d_%H%M%S");
		return new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	}


	public static File saveBitmapToFile(Context context, Uri uri, Bitmap bitmap){
		File file = null;
		try {
			file = new File(uri.getPath());
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return file;
	}


	public static Uri getLastCaptureBitmapUri(Context context){
		Uri uri =null;
		String[] IMAGE_PROJECTION = {
				MediaStore.Images.ImageColumns.DATA, 
				MediaStore.Images.ImageColumns._ID,
		};
		Cursor cursorImages = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_PROJECTION, null, null,null);
		if (cursorImages != null && cursorImages.moveToLast()) {
			uri = Uri.parse(cursorImages.getString(0)); //경로
			cursorImages.close();
		}
		return uri;  
	}
}
