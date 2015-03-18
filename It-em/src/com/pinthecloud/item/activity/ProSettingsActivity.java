package com.pinthecloud.item.activity;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ProSettingsActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;

	private TextView mMileage;
	private EditText mEmail;
	private ImageButton mEmailSubmit;
	private TextView mEmptyBankAccount;
	private TextView mBankAccount;
	private Button mEditBankAccount;

	private ItUser mMyItUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_pro_settings);

		mMyItUser = mObjectPrefHelper.get(ItUser.class);
		setToolbar();
		findComponent();
		setComponent();
		setButton();
		setMileage();
		setBankAccount();
	}


	@Override
	public void onStart() {
		super.onStart();
		mUserHabitHelper.activityStart(mThisActivity);
		mGaHelper.reportActivityStart(mThisActivity);
	}


	@Override
	public void onStop() {
		super.onStop();
		mUserHabitHelper.activityStop(mThisActivity);
		mGaHelper.reportActivityStop(mThisActivity);
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
	}


	@Override
	public View getToolbarLayout() {
		return mToolbarLayout;
	}


	private void setToolbar(){
		mToolbarLayout = findViewById(R.id.toolbar_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}


	private void findComponent() {
		mMileage = (TextView)findViewById(R.id.pro_settings_mileage);
		mEmail = (EditText)findViewById(R.id.pro_settings_email);
		mEmailSubmit = (ImageButton)findViewById(R.id.pro_settings_email_submit);
		mEmptyBankAccount = (TextView)findViewById(R.id.pro_settings_empty_bank_account);
		mBankAccount = (TextView)findViewById(R.id.pro_settings_bank_account);
		mEditBankAccount = (Button)findViewById(R.id.pro_settings_bank_account_edit);
	}


	private void setComponent(){
		mEmail.setText(mMyItUser.getEmail());
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
						Toast.makeText(mThisActivity, message, Toast.LENGTH_LONG).show();	
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
						mMyItUser = bundle.getParcelable(ItUser.INTENT_KEY);
						mObjectPrefHelper.put(mMyItUser);
						setBankAccount();
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				});
				bankAccountEditDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setMileage(){
		mUserHelper.get(mMyItUser.getId(), new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mMyItUser = entity;
				mObjectPrefHelper.put(mMyItUser);
				
				String mileage = String.format(Locale.US, "%,d", mMyItUser.getMileage());
				mMileage.setText(mileage);
			}
		});
	}
	
	
	private void setBankAccount(){
		String bankName = mMyItUser.bankNameString(mThisActivity);
		String bankAccountNumber = mMyItUser.getBankAccountNumber();
		String bankAccountName = mMyItUser.getBankAccountName();

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
		return !mMyItUser.getEmail().equals(mEmail.getText().toString());
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
		mApp.showProgressDialog(mThisActivity);

		mMyItUser.setEmail(mEmail.getText().toString());
		mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				mApp.dismissProgressDialog();
				Toast.makeText(mThisActivity, getResources().getString(R.string.email_edited), Toast.LENGTH_LONG).show();

				mMyItUser = entity;
				mObjectPrefHelper.put(mMyItUser);
			}
		});
	}
}
