package com.pinthecloud.item.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.format.Time;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;

public class FileUtil {

	public static final int GALLERY = 0;
	public static final int CAMERA = 1;


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
		try {
			File file = new File(uri.getPath());
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			return file;
		} catch (FileNotFoundException e) {
			throw new ItException(ItException.TYPE.INTERNAL_ERROR);
		} catch (IOException e) {
			throw new ItException(ItException.TYPE.INTERNAL_ERROR);
		}
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


	public static Uri getMediaUri(ItFragment frag, int mediaType){
		Intent intent = null;
		Uri mediaUri = null;
		switch(mediaType){
		case GALLERY:
			intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			frag.startActivityForResult(intent, GALLERY);
			break;
		case CAMERA:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mediaUri = getOutputMediaFileUri();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
			frag.startActivityForResult(intent, CAMERA);
			break;
		}
		return mediaUri;
	}


	public static String getMediaPath(Context context, Intent data, Uri mediaUri, int mediaType){
		String imagePath = null;
		switch(mediaType){
		case GALLERY:
			mediaUri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = context.getContentResolver().query(mediaUri, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			imagePath = cursor.getString(columnIndex);
			cursor.close();
			break;
		case CAMERA:
			if(mediaUri == null){
				if(data == null){
					mediaUri = getLastCaptureBitmapUri(context);
				} else{
					mediaUri = data.getData();
					if(mediaUri == null){
						// Intent pass data as Bitmap
						Bitmap bitmap = (Bitmap) data.getExtras().get("data");
						mediaUri = getOutputMediaFileUri();
						saveBitmapToFile(context, mediaUri, bitmap);
					}
				}
			}
			imagePath = mediaUri.getPath();
			break;
		}
		return imagePath;
	}
}
