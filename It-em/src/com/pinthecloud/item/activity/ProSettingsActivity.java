package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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
		String bankName = mMyItUser.bankNameString(mThisActivity);
		int bankAccountNumber = mMyItUser.getBankAccountNumber();
		String bankAccountName = mMyItUser.getBankAccountName();
		mBankAccount.setText(bankName + " " + bankAccountNumber + " " + bankAccountName);
		mEmptyBankAccount.setVisibility(bankAccountNumber == 0 ? View.VISIBLE : View.GONE);
		mBankAccount.setVisibility(bankAccountNumber == 0 ? View.GONE : View.VISIBLE);
	}
	
	
	private void setButton(){
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
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
					}
				});
				bankAccountEditDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
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
