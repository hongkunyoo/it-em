package com.pinthecloud.item.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.pinthecloud.item.interfaces.DialogCallback;


public class ItAlertListDialog extends ItDialogFragment{

	private static final String ITEM_LIST_KEY = "ITEM_LIST_KEY";

	private String[] mItemList;
	private DialogCallback[] mCallbacks;

	public void setCallbacks(DialogCallback[] mCallbacks) {
		this.mCallbacks = mCallbacks;
	}


	public static ItAlertListDialog newInstance(String[] itemList) {
		ItAlertListDialog dialog = new ItAlertListDialog();
		Bundle bundle = new Bundle();
		bundle.putStringArray(ITEM_LIST_KEY, itemList);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItemList = getArguments().getStringArray(ITEM_LIST_KEY);
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setStyle(STYLE_NO_TITLE, 0);
		AlertDialog.Builder altBuilder = new AlertDialog.Builder(mActivity);
		setItemList(altBuilder);
		AlertDialog alertDialog = altBuilder.create();
		return alertDialog;
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
