package com.pinthecloud.item;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class ItIntentService extends IntentService {

	private Context mThis;


	public ItIntentService() {
		this("ItIntentService");
	}


	public ItIntentService(String name) {
		super(name);
		mThis = this;
	}


	public void onHandleIntent(Intent intent) {
		String unRegisterd = intent.getStringExtra("unregistered");
		if (unRegisterd != null && unRegisterd.equals(GlobalVariable.GOOGLE_PLAY_APP_ID)){
			return;	
		}

		String message = intent.getExtras().getString("message");
		alertNotification(message);
	}


	private void alertNotification(String message){
		Intent resultIntent = new Intent();
		String title = "";
		String content = message;

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mThis);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		// Set intent and bitmap
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		// Set Notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mThis)
		.setSmallIcon(R.drawable.launcher)
		.setContentTitle(title)
		.setContentText(content)
		.setAutoCancel(true);
		mBuilder.setContentIntent(resultPendingIntent);

		// Notify
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(GlobalVariable.NOTIFICATION_ID, mBuilder.build());

		// For Vibration
		AudioManager audioManager = (AudioManager) mThis.getSystemService(Context.AUDIO_SERVICE);
		if(AudioManager.RINGER_MODE_SILENT != audioManager.getRingerMode()){
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
		}
	}


	/*
	 * Deprecated by security issue in api 21
	 */
	//	private boolean isRunning(Context context) {
	//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	//		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
	//		for (RunningTaskInfo task : tasks) {
	//			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())){
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	//
	//
	//	private boolean isActivityRunning(Context context, Class<?> clazz) {
	//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	//		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
	//		for (RunningTaskInfo task : tasks) {
	//			if (task.topActivity.getClassName().equals(clazz.getName())) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	//
	//
	//	private String getCurrentRunningActivityName(Context context) {
	//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	//		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
	//		for (RunningTaskInfo task : tasks) {
	//			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) {
	//				return task.topActivity.getClassName();
	//			}
	//		}
	//		return ItIntentService.class.getName();
	//	}
}