package com.pinthecloud.item.fragment;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.BankAccountEditDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

public class ProSettingsFragment extends ItFragment {
	
	private TextView mMileage;
	private EditText mEmail;
	private ImageButton mEmailSubmit;
	private TextView mEmptyBankAccount;
	private TextView mBankAccount;
	private Button mEditBankAccount;

	private ItUser mUser;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_pro_settings, container, false);

		mGaHelper.sendScreen(mThisFragment);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		setMileage();
		setBankAccount();
		
		return view;
	}
	
	
	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.pro_settings));
	}


	private void findComponent(View view) {
		mMileage = (TextView)view.findViewById(R.id.pro_settings_mileage);
		mEmail = (EditText)view.findViewById(R.id.pro_settings_email);
		mEmailSubmit = (ImageButton)view.findViewById(R.id.pro_settings_email_submit);
		mEmptyBankAccount = (TextView)view.findViewById(R.id.pro_settings_empty_bank_account);
		mBankAccount = (TextView)view.findViewById(R.id.pro_settings_bank_account);
		mEditBankAccount = (Button)view.findViewById(R.id.pro_settings_bank_account_edit);
	}


	private void setComponent(){
		mEmail.setText(mUser.getEmail());
		mEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEmailSubmit.setEnabled(s.toString().trim().length() > 0);
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
		mEmailSubmit.setEnabled(mEmail.getText().toString().trim().length() > 0);
		mEmailSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trimEmail();
				if(isEmailChanged()){
					String message = checkEmail(mEmail.getText().toString());
					if(message.equals("")){
						updateEmail();
					} else {
						Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();	
					}
				}
			}
		});

		mEditBankAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BankAccountEditDialog bankAccountEditDialog = new BankAccountEditDialog();
				bankAccountEditDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						mUser = bundle.getParcelable(ItUser.INTENT_KEY);
						mObjectPrefHelper.put(mUser);
						setBankAccount();
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				});
				bankAccountEditDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setMileage(){
		mUserHelper.get(mUser.getId(), new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mUser = entity;
				mObjectPrefHelper.put(mUser);
				
				String mileage = String.format(Locale.US, "%,d", mUser.getMileage());
				mMileage.setText(mileage);
			}
		});
	}
	
	
	private void setBankAccount(){
		String bankName = mUser.bankNameString(mActivity);
		String bankAccountNumber = mUser.getBankAccountNumber();
		String bankAccountName = mUser.getBankAccountName();

		if(bankAccountNumber.equals("") || bankAccountName.equals("")){
			mEmptyBankAccount.setVisibility(View.VISIBLE);
			mBankAccount.setVisibility(View.GONE);
		} else {
			mEmptyBankAccount.setVisibility(View.GONE);
			mBankAccount.setVisibility(View.VISIBLE);

			bankAccountNumber = bankAccountNumber.substring(0, bankAccountNumber.length()-4) + "****";
			mBankAccount.setText(bankName + " " + bankAccountNumber + " " + bankAccountName);
		}
	}


	private void trimEmail(){
		mEmail.setText(mEmail.getText().toString().trim().replace(" ", "").replace("\n", ""));
	}


	private boolean isEmailChanged(){
		return !mUser.getEmail().equals(mEmail.getText().toString());
	}


	private String checkEmail(String email){
		String emailRegx = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$";
		if(!email.matches(emailRegx)){
			return getResources().getString(R.string.bad_email_message);
		} else {
			return "";
		}
	}


	private void updateEmail(){
		mApp.showProgressDialog(mActivity);

		mUser.setEmail(mEmail.getText().toString());
		mUserHelper.update(mUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mApp.dismissProgressDialog();
				Toast.makeText(mActivity, getResources().getString(R.string.email_edited), Toast.LENGTH_LONG).show();

				mUser = entity;
				mObjectPrefHelper.put(mUser);
			}
		});
	}
}
