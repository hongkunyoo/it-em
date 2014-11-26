package com.pinthecloud.item.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.item.interfaces.ItDialogCallback;


public class ItAlertListDialog extends ItDialogFragment{

	protected ItDialogCallback[] mCallbacks;
	private String mTitle;
	private String[] mItemList;


	public ItAlertListDialog(String title, String[] list, ItDialogCallback[] itCallbacks) {
		super();
		this.mCallbacks = itCallbacks;
		this.mTitle = title;
		this.mItemList = list;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(mActivity);
		setTitle(altBuilder);
		setItemList(altBuilder);
		return altBuilder.create();
	}


	private void setTitle(AlertDialog.Builder altBuilder){
		if(mTitle == null){
			setStyle(STYLE_NO_TITLE, 0);
		}else{
			altBuilder.setTitle(mTitle);
		}
	}


	private void setItemList(AlertDialog.Builder altBuilder){
		altBuilder.setItems(mItemList, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mCallbacks[which] != null){
					mCallbacks[which].doPositiveThing(null);	
				}
				dismiss();
			}
		});
	}
}
