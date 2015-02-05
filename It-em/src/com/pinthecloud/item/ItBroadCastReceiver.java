package com.pinthecloud.item;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.model.ItUser;

public class ItBroadCastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String regId = intent.getExtras().getString("registration_id");
		if(regId != null && !regId.equals("")) {
			// Save registration id
			ObjectPrefHelper objPrefHelper = ItApplication.getInstance().getObjectPrefHelper();
			ItUser user = objPrefHelper.get(ItUser.class);
			user.setRegistrationId(regId);
			objPrefHelper.put(user);
		} else {
			// Explicitly specify that GcmIntentService will handle the intent.
			ComponentName comp = new ComponentName(context.getPackageName(), ItIntentService.class.getName());

			// Start the service, keeping the device awake while it is launching.
			startWakefulService(context, intent.setComponent(comp));
			setResultCode(Activity.RESULT_OK);
		}
	}
}
