package com.pinthecloud.item.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.ItEntityCallback;

public class BlobStorageHelper {

	private static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;AccountName=athere;AccountKey=ldhgydlWndSIl7XfiaAQ+sibsNtVZ1Psebba1RpBKxMbyFVYUCMvvuQir0Ty7f0+8TnNLfFKc9yFlYpP6ZSuQQ==";
	public static final String USER_PROFILE = "item-user-profile";
	public static final String ITEM_IMAGE = "item-image-container";

	private ItApplication mApp;
	private CloudBlobClient blobClient;


	public BlobStorageHelper(ItApplication app) {
		this.mApp = app;

		CloudStorageAccount account = null;
		try {
			account = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			ExceptionManager.fireException(new ItException(null, "BlobStorageHelper", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new ItException(null, "BlobStorageHelper", ItException.TYPE.BLOB_STORAGE_ERROR));
		}
		this.blobClient = account.createCloudBlobClient();
	}


	public static String getHostUrl() {
		return "https://athere.blob.core.windows.net/";
	}
	public static String getHostUrl(String uri) {
		return "https://athere.blob.core.windows.net/" + uri + "/";
	}
	public static String getUserProfileHostUrl() {
		return getHostUrl(USER_PROFILE);
	}
	public static String getUserProfileImgUrl(String id) {
		return getHostUrl(USER_PROFILE) + id;
	}
	public static String getItemImgHostUrl() {
		return getHostUrl(ITEM_IMAGE);
	}
	public static String getItemImgUrl(String id) {
		return getHostUrl(ITEM_IMAGE)+id;
	}



	public String uploadBitmapSync(ItFragment frag, String containerName, String id, Bitmap bitmap) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
			blob.getProperties().setCacheControl("only-if-cached,max-age=" + Integer.MAX_VALUE);
			blob.upload(new ByteArrayInputStream(baos.toByteArray()), baos.size());
			baos.close();
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new ItException(frag, "uploadBitmapSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			ExceptionManager.fireException(new ItException(frag, "uploadBitmapSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (IOException e) {
			ExceptionManager.fireException(new ItException(frag, "uploadBitmapSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		}
		return id;
	}


	public Bitmap downloadBitmapSync(ItFragment frag, String containerName, String id) {
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
		} catch (StorageException e) {
		}
		return bm;
	}


	public String downloadToFileSync(ItFragment frag, String containerName, String id, String path) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			blob.downloadToFile(frag.getActivity().getFilesDir() + "/" + path);
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new ItException(frag, "downloadToFileSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			ExceptionManager.fireException(new ItException(frag, "downloadToFileSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (IOException e) {
			ExceptionManager.fireException(new ItException(frag, "downloadToFileSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		}
		return frag.getActivity().getFilesDir() + "/" + path;
	}


	public boolean deleteBitmapSync(ItFragment frag, String containerName, String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			blob.delete();
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new ItException(frag, "deleteBitmapSync", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (StorageException e) {
			return false;
		}
		return true;
	}


	public void uploadBitmapAsync(final ItFragment frag, final String containerName, String id, final Bitmap bitmap, final ItEntityCallback<String> callback) {
		if(!mApp.isOnline()){
			ExceptionManager.fireException(new ItException(frag, "uploadBitmapSync", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return uploadBitmapSync(frag, containerName, id, bitmap);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void downloadBitmapAsync(final ItFragment frag, final String containerName, String id, final ItEntityCallback<Bitmap> callback) {
		if(!mApp.isOnline()){
			ExceptionManager.fireException(new ItException(frag, "downloadBitmapAsync", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return downloadBitmapSync(frag, containerName, id);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void downloadToFileAsync(final ItFragment frag, final String containerName, String id, final String path, final ItEntityCallback<String> callback) {
		if(!mApp.isOnline()){
			ExceptionManager.fireException(new ItException(frag, "downloadToFileAsync", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return downloadToFileSync(frag, containerName, id, path);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}


	public void deleteBitmapAsync(final ItFragment frag, final String containerName, String id, final ItEntityCallback<Boolean> callback) {
		if(!mApp.isOnline()){
			ExceptionManager.fireException(new ItException(frag, "deleteBitmapAsync", ItException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String id = params[0];
				return deleteBitmapSync(frag, containerName, id);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				callback.onCompleted(result);
			}
		}).execute(id);
	}
}
