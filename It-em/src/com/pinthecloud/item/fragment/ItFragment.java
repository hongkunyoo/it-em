package com.pinthecloud.item.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.pinthecloud.item.GlobalVariable;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;

public class ItFragment extends Fragment implements ExceptionManager.Handler {

	protected ItApplication app;
	protected ItActivity activity;
	protected ItFragment thisFragment;
	protected PrefHelper prefHelper;


	public ItFragment(){
		app = ItApplication.getInstance();
		thisFragment = this;
		prefHelper = app.getPrefHelper();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (ItActivity) getActivity();
		ExceptionManager.setHandler(thisFragment);
	}


	@Override
	public void handleException(final ItException ex) {
		String title = null;
		String message = null;
		if(ex.getType().equals(ItException.TYPE.INTERNET_NOT_CONNECTED)){
			title = null;
			message = getResources().getString(R.string.internet_not_connected_message);
		} else{
			title = ex.getType().toString();
			message = ex.toString();
		}

		ItAlertDialog exceptionDialog = new ItAlertDialog(title, message, null, null, false, new ItDialogCallback() {
			@Override
			public void doPositiveThing(Bundle bundle) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		}); 
		exceptionDialog.show(getFragmentManager(), GlobalVariable.DIALOG_KEY);
	}
}
