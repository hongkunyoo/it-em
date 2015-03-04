package com.pinthecloud.item.dialog;

import java.util.Locale;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
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
		setSpinner();
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
				mSubmit.setEnabled(isSubmitEnable());
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
				mSubmit.setEnabled(isSubmitEnable());
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


	private void setSpinner(){
		String[] bankNames = getResources().getStringArray(R.array.bank_name_array);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, bankNames);
		mBankName.setAdapter(adapter);

		mBankName.setSelection(mMyItUser.getBankName()+1);
		mBankName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mSubmit.setEnabled(isSubmitEnable());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
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

		mSubmit.setEnabled(isSubmitEnable());
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trimContent();
				if(isBankAccountChanged()){
					int bankName = mBankName.getSelectedItemPosition()-1;
					int bankAccountNumber = Integer.parseInt(mBankAccountNumber.getText().toString());
					String bankAccountName = mBankAccountName.getText().toString();

					String message = checkBankAccountName(bankAccountName);
					if(message.equals("")){
						updateBankAccount(bankName, bankAccountNumber, bankAccountName);
					} else {
						Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();	
					}
				} else {
					dismiss();	
				}
			}
		});
	}


	private boolean isSubmitEnable(){
		return mBankName.getSelectedItemPosition() != 0
				&& mBankAccountNumber.getText().toString().trim().length() > 0 
				&& mBankAccountName.getText().toString().trim().length() > 0;
	}


	private void trimContent(){
		mBankAccountNumber.setText(mBankAccountNumber.getText().toString().trim().replace(" ", "").replace("\n", ""));
		mBankAccountName.setText(mBankAccountName.getText().toString().trim().replace("\n", ""));
	}


	private boolean isBankAccountChanged(){
		return mMyItUser.getBankAccountNumber() != Integer.parseInt(mBankAccountNumber.getText().toString())
				&& !mMyItUser.getBankAccountName().equals(mBankAccountName.getText().toString());
	}


	private String checkBankAccountName(String name){
		String nameRegx = "^[a-zA-Z0-9가-힣\\s]+";
		if(!name.matches(nameRegx)){
			return getResources().getString(R.string.bad_bank_account_name_message);
		} else {
			return "";
		}
	}


	private void updateBankAccount(int bankName, final int bankAccountNumber, final String bankAccountName){
		mApp.showProgressDialog(mActivity);

		mMyItUser.setBankName(bankName);
		mMyItUser.setBankAccountNumber(bankAccountNumber);
		mMyItUser.setBankAccountName(bankAccountName);
		mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.bank_account_edited), Toast.LENGTH_LONG).show();

				Bundle bundle = new Bundle();
				bundle.putParcelable(ItUser.INTENT_KEY, entity);
				mCallback.doPositiveThing(bundle);
				dismiss();
			}
		});
	}
}
