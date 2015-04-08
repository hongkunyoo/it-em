package com.pinthecloud.item.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.item.interfaces.DialogCallback;

public class ItAlertDialog extends ItDialogFragment{

	private static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final String OK_MESSAGE_KEY = "OK_MESSAGE_KEY";
	private static final String NEUTRAL_MESSAGE_KEY = "NEUTRAL_MESSAGE_KEY";
	private static final String CANCEL_MESSAGE_KEY = "CANCEL_MESSAGE_KEY";
	private static final String NEUTRAL_KEY = "NEUTRAL_KEY";
	private static final String CANCEL_KEY = "CANCEL_KEY";

	private String mMessage;
	private String mOkMessage;
	private String mNeutralMessage;
	private String mCancelMessage;
	private boolean mNeutral;
	private boolean mCancel;
	private DialogCallback mCallback;

	public void setCallback(DialogCallback mCallback) {
		this.mCallback = mCallback;
	}


	public static ItAlertDialog newInstance(String message, String okMessage, String NeutralMessage, String cancelMessage, 
			boolean neutral, boolean cancel) {
		ItAlertDialog dialog = new ItAlertDialog();
		Bundle bundle = new Bundle();
		bundle.putString(MESSAGE_KEY, message);
		bundle.putString(OK_MESSAGE_KEY, okMessage);
		bundle.putString(NEUTRAL_MESSAGE_KEY, NeutralMessage);
		bundle.putString(CANCEL_MESSAGE_KEY, cancelMessage);
		bundle.putBoolean(NEUTRAL_KEY, neutral);
		bundle.putBoolean(CANCEL_KEY, cancel);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMessage = getArguments().getString(MESSAGE_KEY);
		mOkMessage = getArguments().getString(OK_MESSAGE_KEY);
		mNeutralMessage = getArguments().getString(NEUTRAL_MESSAGE_KEY);
		mCancelMessage = getArguments().getString(CANCEL_MESSAGE_KEY);
		mNeutral = getArguments().getBoolean(NEUTRAL_KEY);
		mCancel = getArguments().getBoolean(CANCEL_KEY);
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setStyle(STYLE_NO_TITLE, 0);
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(mActivity);
		setMessage();
		setDialog(altBuilder);
		return altBuilder.create();
	}


	private void setMessage(){
		if(mOkMessage == null){
			mOkMessage =  getResources().getString(android.R.string.ok);
		}
		if(mNeutralMessage == null){
			mNeutralMessage =  getResources().getString(android.R.string.no);
		}
		if(mCancelMessage == null){
			mCancelMessage =  getResources().getString(android.R.string.no);	
		}
	}


	private void setDialog(AlertDialog.Builder altBuilder){
		altBuilder.setMessage(mMessage);
		altBuilder.setPositiveButton(mOkMessage, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				mCallback.doPositive(null);
			}
		});
		
		if(mNeutral){
			altBuilder.setNeutralButton(mNeutralMessage, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
					mCallback.doNeutral(null);
				}
			});
		}
		
		if(mCancel){
			altBuilder.setNegativeButton(mCancelMessage, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
					mCallback.doNegative(null);
				}
			});
		}
	}
}
