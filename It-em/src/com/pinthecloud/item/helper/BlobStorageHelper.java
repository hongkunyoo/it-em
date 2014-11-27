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
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.util.AsyncChainer;

public class BlobStorageHelper {

	private static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;AccountName=athere;AccountKey=ldhgydlWndSIl7XfiaAQ+sibsNtVZ1Psebba1RpBKxMbyFVYUCMvvuQir0Ty7f0+8TnNLfFKc9yFlYpP6ZSuQQ==";
	public static final String USER_PROFILE = "item-user-profile";
	public static final String ITEM_IMAGE = "item-image-container";
	protected CloudBlobClient blobClient;


	public BlobStorageHelper() {
		CloudStorageAccount account = null;
		try {
			account = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			ExceptionManager.fireException(new ItException(null, "BlobStorageHelper", ItException.TYPE.BLOB_STORAGE_ERROR));
		} catch (URISyntaxException e) {
			ExceptionManager.fireException(new ItException(null, "BlobStorageHelper", ItException.TYPE.BLOB_STORAGE_ERROR));
		}

		// Create a blob service client
		blobClient = account.createCloudBlobClient();
	}
	
	public String getHostUrl() {
		return "https://athere.blob.core.windows.net/";
	}
	public String getHostUrl(String uri) {
		return "https://athere.blob.core.windows.net/" + uri + "/";
	}
	


	public String uploadBitmapSync(final ItFragment frag, String containerName, String id, Bitmap bitmap) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference(containerName);
			blob = container.getBlockBlobReference(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
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


	public Bitmap downloadBitmapSync(final ItFragment frag, String containerName, String id) {
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
			// Do nothing
		} catch (StorageException e) {
			// Do noghing
		}
		if(bm == null){
//			bm = BitmapFactory.decodeResource(frag.getResources(), R.drawable.profile_dialog_chupa_ico);
		}
		return bm;
	}


	public String downloadToFileSync(final ItFragment frag, String containerName, String id, String path) {
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


	public boolean deleteBitmapSync(final ItFragment frag, String containerName, String id) {
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
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return uploadBitmapSync(frag, containerName, id, bitmap);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (callback != null){
					callback.onCompleted(result);
				}
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}


	public void downloadBitmapAsync(final ItFragment frag, final String containerName, String id, final ItEntityCallback<Bitmap> callback) {
		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				String id = params[0];
				return downloadBitmapSync(frag, containerName, id);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (callback != null){
					callback.onCompleted(result);
				}
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}


	public void downloadToFileAsync(final ItFragment frag, final String containerName, String id, final String path, final ItEntityCallback<String> callback) {
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String id = params[0];
				return downloadToFileSync(frag, containerName, id, path);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (callback != null){
					callback.onCompleted(result);
				}
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}


	public void deleteBitmapAsync(final ItFragment frag, final String containerName, String id, final ItEntityCallback<Boolean> callback) {
		(new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				String id = params[0];
				return deleteBitmapSync(frag, containerName, id);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (callback != null){
					callback.onCompleted(result);
				}
				AsyncChainer.notifyNext(frag);
			}
		}).execute(id);
	}
}
