package com.pinthecloud.item.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;

import de.greenrobot.event.EventBus;

public class BlobStorageHelper {

	private static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;AccountName=athere;AccountKey=ldhgydlWndSIl7XfiaAQ+sibsNtVZ1Psebba1RpBKxMbyFVYUCMvvuQir0Ty7f0+8TnNLfFKc9yFlYpP6ZSuQQ==";
	public static final String CONTAINER_USER_PROFILE = "item-user-profile";
	public static final String CONTAINER_ITEM_IMAGE = "item-image-container";

	private ItApplication mApp;
	private CloudBlobClient blobClient;


	public BlobStorageHelper(ItApplication app) {
		this.mApp = app;

		CloudStorageAccount account = null;
		try {
			account = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			EventBus.getDefault().post(new ItException("BlobStorageHelper", ItException.TYPE.INTERNAL_ERROR));
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("BlobStorageHelper", ItException.TYPE.INTERNAL_ERROR));
		}
		this.blobClient = account.createCloudBlobClient();
	}


	public static String getHostUrl() {
		return "https://athere.blob.core.windows.net/";
	}
	public static String getHostUrl(String uri) {
		return getHostUrl() + uri + "/";
	}
	public static String getUserProfileHostUrl() {
		return getHostUrl(CONTAINER_USER_PROFILE);
	}
	public static String getUserProfileImgUrl(String id) {
		return getUserProfileHostUrl() + id;
	}
	public static String getItemImgHostUrl() {
		return getHostUrl(CONTAINER_ITEM_IMAGE);
	}
	public static String getItemImgUrl(String id) {
		return getItemImgHostUrl() + id;
	}


	private boolean isExistSync(String containerName, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		boolean result = true;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			result = blob.exists();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("isExistSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("isExistSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return result;
	}


	private String uploadBitmapSync(String containerName, String id, Bitmap bitmap) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);

			// Compress Bitmap
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

			// Add header for picasso
			blob.getProperties().setCacheControl("only-if-cached, max-age=" + Integer.MAX_VALUE);

			blob.upload(new ByteArrayInputStream(baos.toByteArray()), baos.size());
			baos.close();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (IOException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return id;
	}


	private Bitmap downloadBitmapSync(String containerName, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		Bitmap bm = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			blob.download(baos);
			bm = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("downloadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("downloadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return bm;
	}


	private String downloadToFileSync(Context context, String containerName, String id, String path) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			blob.downloadToFile(context.getFilesDir() + "/" + path);
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("downloadToFileSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("downloadToFileSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (IOException e) {
			EventBus.getDefault().post(new ItException("downloadToFileSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return context.getFilesDir() + "/" + path;
	}


	private boolean deleteBitmapSync(String containerName, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			blob.delete();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("deleteBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("deleteBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return true;
	}


	public void isExistAsync(final String containerName, String id, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("isExistAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String id = params[0];
				return isExistSync(containerName, id);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}
	
	
	public void uploadBitmapAsync(final String containerName, String id, final Bitmap bitmap, final EntityCallback<String> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return uploadBitmapSync(containerName, id, bitmap);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void downloadBitmapAsync(final String containerName, String id, final EntityCallback<Bitmap> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("downloadBitmapAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return downloadBitmapSync(containerName, id);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void downloadToFileAsync(final Context context, final String containerName, String id, final String path, final EntityCallback<String> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("downloadToFileAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return downloadToFileSync(context, containerName, id, path);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void deleteBitmapAsync(final String containerName, String id, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("deleteBitmapAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String id = params[0];
				return deleteBitmapSync(containerName, id);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}
}
