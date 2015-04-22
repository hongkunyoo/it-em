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
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.event.NotificationEvent;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.AppVersion;
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

		AppVersion mandatoryAppVersion = new Gson().fromJson(message, AppVersion.class);
		if(mandatoryAppVersion.getVersion() > 0){
			updateApp(mandatoryAppVersion);
			return;
		}
	}


	private void alertItNotification(final ItNotification itNoti){
		final PendingIntent pendingIntent = getItNotificationIntent(itNoti);

		AsyncChainer.asyncChain(mThis, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				if(itNoti.getType().equals(ItNotification.TYPE.ProductTag.toString())){
					Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.noti_label_img);
					AsyncChainer.notifyNext(obj, largeIcon);
				} else {
					getLargeIcon(obj, BlobStorageHelper.getUserProfileImgUrl(itNoti.getWhoMadeId()));
				}
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				// Add noti Number preference
				int notiNumber = mApp.getPrefHelper().getInt(ItUser.NOTIFICATION_NUMBER_KEY);
				mApp.getPrefHelper().put(ItUser.NOTIFICATION_NUMBER_KEY, ++notiNumber);

				// Alert noti
				Bitmap largeIcon = (Bitmap)params[0];
				Notification notification = getNotification(itNoti.getWhoMade(), itNoti.makeMessage(), pendingIntent, largeIcon);
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(NOTIFICATION_ID, notification);
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
				try {
					return mApp.getPicasso().load(url).get();
				} catch (IOException e) {
					return BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
				}
			}
			@Override
			protected void onPostExecute(Bitmap icon) {
				AsyncChainer.notifyNext(obj, icon);
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

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if(ringerMode == AudioManager.RINGER_MODE_NORMAL){
			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);
		} else if(ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
			mBuilder.setVibrate(new long[] {225, 225, 225, 225});
		}
		mBuilder.setLights(Color.YELLOW, 500, 1500);

		return mBuilder.build();
	}


	private void updateApp(AppVersion mandatoryAppVersion){
		mApp.getPrefHelper().put(ItConstant.MANDATORY_APP_VERSION_KEY, mandatoryAppVersion.getVersion());
	}
}
