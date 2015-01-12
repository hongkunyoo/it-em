package com.pinthecloud.item.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;

public class CustomDialog extends ItDialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
