package com.pinthecloud.item;

import java.util.List;

import com.pinthecloud.item.util.ItLog;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class ItIntentService extends IntentService {

	private Context mThis;
	private ItApplication mApp;


	public ItIntentService() {
		this("ItIntentService");
	}


	public ItIntentService(String name) {
		super(name);
		mThis = this;
		mApp = ItApplication.getInstance();
	}


	public void onHandleIntent(Intent intent) {
		/*
		 * Parsing the data from server
		 */
		String unRegisterd = intent.getStringExtra("unregistered");
		if (unRegisterd != null && unRegisterd.equals(GlobalVariable.GOOGLE_PLAY_APP_ID)) return;
		
		ItLog.log(intent.getExtras(), intent);
//		String messageStr = intent.getExtras().getString("message");
	}


	/**
	 *  Method For alerting notification
	 */
	//	private void alertNotification(){
	//		/*
	//		 * Creates an explicit intent for an Activity in your app
	//		 */
	//		Intent resultIntent = new Intent();
	//		String title = "";
	//		String content = "";
	//		Resources resources = _this.getResources();
	//
	//		// The stack builder object will contain an artificial back stack for the
	//		// started Activity.
	//		// This ensures that navigating backward from the Activity leads out of
	//		// your application to the Home screen.
	//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(_this);
	//
	//		// Adds the back stack for the Intent (but not the Intent itself)
	//		if (AhMessage.TYPE.CHUPA.equals(type)){
	//			stackBuilder.addParentStack(ChupaChatActivity.class);
	//		}
	//
	//		// Adds the Intent that starts the Activity to the top of the stack
	//		stackBuilder.addNextIntent(resultIntent);
	//
	//		// Set intent and bitmap
	//		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
	//		Bitmap bitmap = null;
	//		if(user != null){
	//			bitmap = FileUtil.getBitmapFromInternalStorage(app, user.getId()+AhGlobalVariable.SMALL);
	//		}else{
	//			bitmap = BitmapUtil.decodeInSampleSize(getResources(), R.drawable.launcher, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);
	//		}
	//
	//
	//		/*
	//		 * Set Notification
	//		 */
	//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_this)
	//		.setSmallIcon(R.drawable.launcher)
	//		.setLargeIcon(bitmap)
	//		.setContentTitle(title)
	//		.setContentText(content)
	//		.setAutoCancel(true);
	//		mBuilder.setContentIntent(resultPendingIntent);
	//
	//		// Notify!
	//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	//		mNotificationManager.notify(1, mBuilder.build());
	//
	//		// For Vibration
	//		AudioManager audioManager = (AudioManager) _this.getSystemService(Context.AUDIO_SERVICE);
	//		if(AudioManager.RINGER_MODE_SILENT != audioManager.getRingerMode()){
	//			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
	//		}
	//	}


	/**
	 * 
	 * @param Application context
	 * @return true if the app is Running foreground
	 * 		   false if the app is turned OFF
	 */
	private boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo task : tasks) {
			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) 
				return true;                                  
		}
		return false;
	}


	private boolean isActivityRunning(Context context, Class<?> clazz) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo task : tasks) {
			if (task.topActivity.getClassName().equals(clazz.getName())) {
				return true;
			}
		}
		return false;
	}


	private String getCurrentRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (context.getPackageName().equalsIgnoreCase(task.topActivity.getPackageName())) {
				return task.topActivity.getClassName();
			}
		}
		return ItIntentService.class.getName();
	}
}
