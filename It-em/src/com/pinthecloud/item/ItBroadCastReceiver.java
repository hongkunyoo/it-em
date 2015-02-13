package com.pinthecloud.item;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pinthecloud.item.event.GcmRegistrationIdEvent;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.model.DeviceInfo;

import de.greenrobot.event.EventBus;

public class ItBroadCastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String registrationId = intent.getExtras().getString("registration_id");
		if(registrationId != null && !registrationId.equals("")) {
			// Get registration id
			ObjectPrefHelper objectPrefHelper = ItApplication.getInstance().getObjectPrefHelper();
			DeviceInfo deviceInfo = objectPrefHelper.get(DeviceInfo.class);
			
			if(deviceInfo.getRegistrationId().equals(PrefHelper.DEFAULT_STRING)){
				// After get id, goto SplashActivity.OnEvent()
				EventBus.getDefault().post(new GcmRegistrationIdEvent(registrationId));
			} else {
				// Got registration id in UserHelper getRegisterationId()
			}
		} else {
			// Explicitly specify that GcmIntentService will handle the intent.
			ComponentName comp = new ComponentName(context.getPackageName(), ItIntentService.class.getName());

			// Start the service, keeping the device awake while it is launching.
			startWakefulService(context, intent.setComponent(comp));
			setResultCode(Activity.RESULT_OK);
		}
	}
}
