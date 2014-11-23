package com.pinthecloud.item.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.item.interfaces.ItDialogCallback;

public class ItAlertDialog extends ItDialogFragment{

	private ItDialogCallback mItDialogCallback;
	private String mTitle;
	private String mMessage;
	private String mOkMessage;
	private String mCancelMessage;
	private boolean mCancel;


	public ItAlertDialog(String title, String message, String okMessage, String cancelMessage, boolean cancel, ItDialogCallback itDialogCallback) {
		super();
		this.mItDialogCallback = itDialogCallback;
		this.mTitle = title;
		this.mMessage = message;
		this.mOkMessage = okMessage;
		this.mCancelMessage = cancelMessage;
		this.mCancel = cancel;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(mActivity);
		setTitle(altBuilder);
		setMessage();
		setDialog(altBuilder);
		AlertDialog alertDialog = altBuilder.create();
		return alertDialog;
	}


	private void setTitle(AlertDialog.Builder altBuilder){
		if(mTitle == null){
			setStyle(STYLE_NO_TITLE, 0);
		}else{
			altBuilder.setTitle(mTitle);
		}
	}


	private void setMessage(){
		if(mOkMessage == null){
			mOkMessage =  getResources().getString(android.R.string.ok);
		}
		if(mCancelMessage == null){
			mCancelMessage =  getResources().getString(android.R.string.no);	
		}
	}


	private void setDialog(AlertDialog.Builder altBuilder){
		altBuilder.setMessage(mMessage);
		altBuilder.setPositiveButton(mOkMessage, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				mItDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});
		if(mCancel){
			altBuilder.setNegativeButton(mCancelMessage, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mItDialogCallback.doNegativeThing(null);
					dismiss();
				}
			});
		}
	}
}
