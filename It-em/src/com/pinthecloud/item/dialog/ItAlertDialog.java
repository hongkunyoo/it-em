package com.pinthecloud.item.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.item.interfaces.ItDialogCallback;

public class ItAlertDialog extends ItDialogFragment{

	private ItDialogCallback itDialogCallback;
	private String title;
	private String message;
	private String okMessage;
	private String cancelMessage;
	private boolean cancel;


	public ItAlertDialog(String title, String message, String okMessage, String cancelMessage, boolean cancel, ItDialogCallback itDialogCallback) {
		super();
		this.itDialogCallback = itDialogCallback;
		this.title = title;
		this.message = message;
		this.okMessage = okMessage;
		this.cancelMessage = cancelMessage;
		this.cancel = cancel;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(activity);
		setTitle(altBuilder);
		setMessage();
		setDialog(altBuilder);
		AlertDialog alertDialog = altBuilder.create();
		return alertDialog;
	}


	private void setTitle(AlertDialog.Builder altBuilder){
		if(title == null){
			setStyle(STYLE_NO_TITLE, 0);
		}else{
			altBuilder.setTitle(title);
		}
	}


	private void setMessage(){
		if(okMessage == null){
			okMessage =  getResources().getString(android.R.string.ok);
		}
		if(cancelMessage == null){
			cancelMessage =  getResources().getString(android.R.string.no);	
		}
	}


	private void setDialog(AlertDialog.Builder altBuilder){
		altBuilder.setMessage(message);
		altBuilder.setPositiveButton(okMessage, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				itDialogCallback.doPositiveThing(null);
				dismiss();
			}
		});
		if(cancel){
			altBuilder.setNegativeButton(cancelMessage, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					itDialogCallback.doNegativeThing(null);
					dismiss();
				}
			});
		}
	}
}
