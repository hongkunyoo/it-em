package com.pinthecloud.item.dialog;

import java.util.Locale;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.model.ItUser;

public class BankAccountEditDialog extends ItDialogFragment {

	private Spinner mBankName;
	private EditText mBankAccountNumber;
	private EditText mBankAccountName;
	private Button mCancel;
	private Button mSubmit;
	
	private ItUser mMyItUser;
	private DialogCallback mCallback;

	public void setCallback(DialogCallback mCallback) {
		this.mCallback = mCallback;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_bank_account_edit, container, false);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
		findComponent(view);
		setComponent();
		setButton();
		return view;
	}


	private void findComponent(View view){
		mBankName = (Spinner)view.findViewById(R.id.bank_account_edit_frag_bank_name);
		mBankAccountNumber = (EditText)view.findViewById(R.id.bank_account_edit_frag_account_number);
		mBankAccountName = (EditText)view.findViewById(R.id.bank_account_edit_frag_account_name);
		mCancel = (Button)view.findViewById(R.id.bank_account_edit_frag_cancel);
		mSubmit = (Button)view.findViewById(R.id.bank_account_edit_frag_submit);
	}


	private void setComponent(){
		mBankAccountNumber.setText(mMyItUser.getBankAccountNumber() == 0 ? "" : ""+mMyItUser.getBankAccountNumber());
		mBankAccountNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mBankAccountNumber.setText(mMyItUser.getBankAccountName());
		mBankAccountName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	private void setButton(){
		mCancel.setText(getResources().getString(android.R.string.cancel).toUpperCase(Locale.US));
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.doPositiveThing(null);
				dismiss();
			}
		});
	}
}
