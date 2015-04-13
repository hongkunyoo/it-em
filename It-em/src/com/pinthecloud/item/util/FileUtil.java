package com.pinthecloud.item.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.squareup.picasso.PicassoTool;

import de.greenrobot.event.EventBus;

public class FileUtil {

	public static final int GALLERY = 10;
	public static final int CAMERA = 11;


	public static Uri getOutputMediaFileUri(Context context){
		return Uri.fromFile(getOutputMediaFile(context));
	}


	public static File getOutputMediaFile(Context context){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.app_name));

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (!mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
		String timeStamp = format.format(calendar.getTime());
		return new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	}


	public static File saveBitmapToFile(Context context, Uri uri, Bitmap bitmap){
		File file = null;
		try {
			file = new File(uri.getPath());
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			EventBus.getDefault().post(new ItException("saveBitmapToFile", ItException.TYPE.INTERNAL_ERROR));
		} catch (IOException e) {
			EventBus.getDefault().post(new ItException("saveBitmapToFile", ItException.TYPE.INTERNAL_ERROR));
		}
		return file;
	}


	public static void getMediaFromGallery(ItFragment frag){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");

		Intent chooser = Intent.createChooser(intent, frag.getResources().getString(R.string.select_source));
		frag.startActivityForResult(chooser, GALLERY);
	}


	public static String getMediaPathFromGalleryUri(Context context, Uri mediaUri){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			try{
				return getMediaPathFromGalleryUri_API19(context, mediaUri);
			} catch(IllegalArgumentException e) {
				// Do nothing
			}
		}
		return getMediaPathFromGalleryUri_API11to18(context, mediaUri);
	}


	public static String getMediaPathFromGalleryUri_API11to18(Context context, Uri mediaUri){
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(mediaUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		return imagePath;
	}


	@SuppressLint("NewApi")
	public static String getMediaPathFromGalleryUri_API19(Context context, Uri mediaUri){
		// Will return "image:x*"
		String wholeID = DocumentsContract.getDocumentId(mediaUri);

		// Split at colon, use second item in the array
		String id = wholeID.split(":")[1];
		String[] column = { MediaStore.Images.Media.DATA };     

		// where id is equal to             
		String sel = MediaStore.Images.Media._ID + "=?";
		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
				column, sel, new String[]{ id }, null);

		String filePath = "";
		int columnIndex = cursor.getColumnIndex(column[0]);
		if (cursor.moveToFirst()) {
			filePath = cursor.getString(columnIndex);
		}
		cursor.close();

		return filePath;
	}


	public static Uri getMediaFromCamera(ItFragment frag){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri mediaUri = getOutputMediaFileUri(frag.getActivity());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
		frag.startActivityForResult(intent, CAMERA);
		return mediaUri;
	}


	public static Uri getMediaUriFromCamera(Context context, Intent data, Uri mediaUri){
		if(mediaUri == null){
			if(data == null){
				mediaUri = getLastCaptureBitmapUri(context);
			} else {
				mediaUri = data.getData();
				if(mediaUri == null){
					// Intent pass data as Bitmap
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					mediaUri = getOutputMediaFileUri(context);
					saveBitmapToFile(context, mediaUri, bitmap);
				}
			}
		}
		return mediaUri;
	}


	public static Uri getLastCaptureBitmapUri(Context context){
		String[] IMAGE_PROJECTION = {
				MediaStore.Images.ImageColumns.DATA, 
				MediaStore.Images.ImageColumns._ID,
		};
		Cursor cursorImages = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_PROJECTION, null, null,null);
		Uri uri =null;
		if (cursorImages != null && cursorImages.moveToLast()) {
			uri = Uri.parse(cursorImages.getString(0));
			cursorImages.close();
		}
		return uri;  
	}


	public static void deleteDirectoryTree(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteDirectoryTree(child);
			}
		}
		fileOrDirectory.delete();
	}


	public static void clearCache(){
		ItApplication app = ItApplication.getInstance();
		PicassoTool.clearCache(app.getPicasso());
		deleteDirectoryTree(app.getCacheDir());
	}
}
