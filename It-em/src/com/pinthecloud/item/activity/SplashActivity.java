package com.pinthecloud.item.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.SplashFragment;
import com.pinthecloud.item.interfaces.DialogCallback;

public class SplashActivity extends ItActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setFragment();
	}


	@Override
	public View getToolbarLayout() {
		return null;
	}


	private void setFragment(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		ItFragment fragment = new SplashFragment();
		transaction.replace(R.id.activity_container, fragment);
		transaction.commit();
	}
	
	
	public void onEvent(ItException exception) {
		if (exception.getType().equals(ItException.TYPE.GCM_REGISTRATION_FAIL)) {
			String message = getResources().getString(R.string.google_play_services_message);
			ItAlertDialog gcmDialog = ItAlertDialog.newInstance(message, null, null, true);

			gcmDialog.setCallback(new DialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id=" + GlobalVariable.GOOGLE_PLAY_SERVICE_APP_ID));
					startActivity(intent);
					mThisActivity.finish();
				}

				@Override
				public void doNegativeThing(Bundle bundle) {
					mThisActivity.finish();
				}
			});
			gcmDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			return;
		}

		super.onEvent(exception);
	}
}
