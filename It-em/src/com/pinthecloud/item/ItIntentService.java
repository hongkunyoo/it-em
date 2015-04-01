package com.pinthecloud.item;

import java.io.IOException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.event.AppVersion;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.event.NotificationEvent;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;

import de.greenrobot.event.EventBus;

public class ItIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;

	private ItApplication mApp;
	private Context mThis;

	public ItIntentService() {
		this("ItIntentService");
	}
	public ItIntentService(String name) {
		super(name);
		this.mApp = ItApplication.getInstance();
		this.mThis = this;
	}


	public void onHandleIntent(Intent intent) {
		String unRegisterd = intent.getStringExtra("unregistered");
		if (unRegisterd != null && unRegisterd.equals(ItConstant.GOOGLE_PLAY_APP_ID)){
			return;	
		}

		String message = intent.getExtras().getString("message");

		ItNotification itNoti = new Gson().fromJson(message, ItNotification.class);
		if(itNoti.getType() != null){
			alertItNotification(itNoti);
			return;
		}

		AppVersion version = new Gson().fromJson(message, AppVersion.class);
		if(version.getVersion() != 0){
			updateApp(version);
			return;
		}
	}


	private void alertItNotification(final ItNotification itNoti){
		final PendingIntent pendingIntent = getItNotificationIntent(itNoti);

		AsyncChainer.asyncChain(mThis, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				if(!itNoti.getType().equals(ItNotification.TYPE.ProductTag.toString())){
					getLargeIcon(obj, BlobStorageHelper.getUserProfileImgUrl(itNoti.getWhoMadeId()));
				} else {
					Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.noti_label_img);
					AsyncChainer.notifyNext(obj, largeIcon);
				}
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				Bitmap largeIcon = (Bitmap)params[0];
				Notification notification = getNotification(itNoti.getWhoMade(), itNoti.notiContent(), pendingIntent, largeIcon);

				// Notify
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(NOTIFICATION_ID, notification);

				// For Vibration
				AudioManager audioManager = (AudioManager) mThis.getSystemService(Context.AUDIO_SERVICE);
				if(AudioManager.RINGER_MODE_SILENT != audioManager.getRingerMode()){
					((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
				}

				// Add Noti Number preference
				int notiNumber = mApp.getPrefHelper().getInt(ItUser.NOTIFICATION_NUMBER_KEY);
				mApp.getPrefHelper().put(ItUser.NOTIFICATION_NUMBER_KEY, ++notiNumber);
				EventBus.getDefault().post(new NotificationEvent(itNoti));
			}
		});
	}


	private PendingIntent getItNotificationIntent(ItNotification itNoti){
		// The stack builder object will contain an artificial back stack for the started Activity.
		// This ensures that navigating backward from the Activity leads out of your application to the Home screen.
		// Adds the Intent that starts the Activity to the top of the stack
		Intent resultIntent = new Intent(mThis, ItemActivity.class);
		resultIntent.putExtra(Item.INTENT_KEY, itNoti.makeItem());

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mThis);
		stackBuilder.addParentStack(ItemActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}


	private void getLargeIcon(final Object obj, final String url){
		(new AsyncTask<Void,Void,Bitmap>(){

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				try {
					bitmap = mApp.getPicasso().load(url).get();
				} catch (IOException e) {
					EventBus.getDefault().post(new ItException("getLargeIcon", ItException.TYPE.INTERNAL_ERROR));
				}
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				AsyncChainer.notifyNext(obj, result);
			};
		}).execute();
	}


	private Notification getNotification(String title, String text, PendingIntent resultPendingIntent, Bitmap largeIcon){
		// Set Notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mThis)
		.setSmallIcon(R.drawable.ic_stat_notify)
		.setLargeIcon(largeIcon)
		.setContentTitle(title)
		.setContentText(text)
		.setAutoCancel(true)
		.setContentIntent(resultPendingIntent);
		return mBuilder.build();
	}


	private void updateApp(AppVersion version){
		mApp.getPrefHelper().put(ItConstant.APP_VERSION_KEY, version.getVersion());
		mApp.getPrefHelper().clear();
	}
}
