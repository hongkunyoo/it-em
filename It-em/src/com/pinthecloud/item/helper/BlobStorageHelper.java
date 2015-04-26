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
			"DefaultEndpointsProtocol=http;AccountName=item;AccountKey=vMwuKvYYKUsKSM1MTSzf2qU5MOAHbblHqmxQwFDb3I0maFcYEOZ3F14XzmGN76kpDFJKAu1iu+UT0KTtyAeuNw==";
	public static final String CONTAINER_REAL_ITEM_IMAGE = "item-image";
	public static final String CONTAINER_REAL_USER_PROFILE = "item-user-profile";
	public static final String CONTAINER_TEST_ITEM_IMAGE = "item-test-image";
	public static final String CONTAINER_TEST_USER_PROFILE = "item-test-user-profile";

	private ItApplication mApp;
	private CloudBlobClient mBlobClient;


	public BlobStorageHelper(ItApplication app) {
		this.mApp = app;

		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
			this.mBlobClient = account.createCloudBlobClient();
		} catch (InvalidKeyException e) {
			// Do nothing
		} catch (URISyntaxException e) {
			// Do nothing
		}
	}


	public static String getHostUrl() {
		return "https://item.blob.core.windows.net/";
	}
	public static String getHostUrl(String uri) {
		return getHostUrl() + uri + "/";
	}
	public static String getItemImageContainer() {
		return ItApplication.isDebugging() ? CONTAINER_TEST_ITEM_IMAGE : CONTAINER_REAL_ITEM_IMAGE;
	}
	public static String getUserProfileContainer() {
		return ItApplication.isDebugging() ? CONTAINER_TEST_USER_PROFILE : CONTAINER_REAL_USER_PROFILE;
	}
	public static String getItemImageHostUrl() {
		return getHostUrl(getItemImageContainer());
	}
	public static String getItemImageUrl(String id) {
		return getItemImageHostUrl() + id;
	}
	public static String getUserProfileHostUrl() {
		return getHostUrl(getUserProfileContainer());
	}
	public static String getUserProfileUrl(String id) {
		return getUserProfileHostUrl() + id;
	}


	private boolean isExistSync(String containerName, String imageId) {
		boolean result = false;
		try {
			CloudBlobContainer container = mBlobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(imageId);
			result = blob.exists();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("isExistSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("isExistSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return result;
	}


	private String uploadBitmapSync(String containerName, String imageId, Bitmap bitmap) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

			CloudBlobContainer container = mBlobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(imageId);
			blob.getProperties().setContentType("image/jpeg");
			blob.upload(new ByteArrayInputStream(baos.toByteArray()), baos.size());

			baos.close();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (IOException e) {
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return imageId;
	}


	private Bitmap downloadBitmapSync(String containerName, String imageId) {
		Bitmap bitmap = null;
		try {
			CloudBlobContainer container = mBlobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(imageId);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			blob.download(baos);
			bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("downloadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		} catch (StorageException e) {
			EventBus.getDefault().post(new ItException("downloadBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return bitmap;
	}


	private String downloadToFileSync(Context context, String containerName, String imageId, String path) {
		try {
			CloudBlobContainer container = mBlobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(imageId);
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


	private boolean deleteBitmapSync(String containerName, String imageId) throws StorageException {
		try {
			CloudBlobContainer container = mBlobClient.getContainerReference(containerName);
			CloudBlockBlob blob = container.getBlockBlobReference(imageId);
			blob.delete();
		} catch (URISyntaxException e) {
			EventBus.getDefault().post(new ItException("deleteBitmapSync", ItException.TYPE.INTERNAL_ERROR));
		}
		return true;
	}


	public void isExistAsync(final String containerName, String imageId, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("isExistAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String imageId = params[0];
				return isExistSync(containerName, imageId);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(imageId);
	}


	public void uploadBitmapAsync(final String containerName, String imageId, final Bitmap bitmap, final EntityCallback<String> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("uploadBitmapSync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String imageId = params[0];
				return uploadBitmapSync(containerName, imageId, bitmap);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(imageId);
	}


	public void downloadBitmapAsync(final String containerName, String imageId, final EntityCallback<Bitmap> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("downloadBitmapAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String imageId = params[0];
				return downloadBitmapSync(containerName, imageId);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(imageId);
	}


	public void downloadToFileAsync(final Context context, final String containerName, String imageId, final String path,
			final EntityCallback<String> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("downloadToFileAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String imageId = params[0];
				return downloadToFileSync(context, containerName, imageId, path);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(imageId);
	}


	public void deleteBitmapAsync(final String containerName, String imageId, final EntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("deleteBitmapAsync", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				try {
					String imageId = params[0];
					return deleteBitmapSync(containerName, imageId);
				} catch (StorageException e) {
					return true;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(imageId);
	}
}
