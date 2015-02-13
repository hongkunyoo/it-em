package com.pinthecloud.item.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;

public class BeProActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	
	private EditText mCode;
	private Button mSubmit;
	private ItUser mMyItUser;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_be_pro);
		
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
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
	}


	private void findComponent(){
		mCode = (EditText)findViewById(R.id.be_pro_code);
		mSubmit = (Button)findViewById(R.id.be_pro_submit);
	}


	private void setComponent(){
		mCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String code = s.toString().trim();
				mSubmit.setEnabled(code.length() > 0);
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
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mThisActivity);
				trimCode();
				bePro(mCode.getText().toString());
			}
		});
	}
	
	
	private void bePro(final String inviteKey){
		AsyncChainer.asyncChain(mThisActivity, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				mAimHelper.isValid(inviteKey, ItUser.TYPE.PRO, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						if(entity){
							AsyncChainer.notifyNext(obj);
						} else {
							mApp.dismissProgressDialog();
							Toast.makeText(mThisActivity, getResources().getString(R.string.invalid_pro), Toast.LENGTH_LONG).show();
							AsyncChainer.clearChain(obj);
						}
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(final Object obj, Object... params) {
				mMyItUser.fixType(ItUser.TYPE.PRO);
				mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

					@Override
					public void onCompleted(ItUser entity) {
						mObjectPrefHelper.put(entity);
						AsyncChainer.notifyNext(obj);
					}
				});
			}
		}, new Chainable() {

			@Override
			public void doNext(Object obj, Object... params) {
				mAimHelper.invalidateInviteKey(inviteKey, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						mApp.dismissProgressDialog();
						Toast.makeText(mThisActivity, getResources().getString(R.string.valid_pro), Toast.LENGTH_LONG).show();
						
						Intent intent = new Intent(mThisActivity, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
			}
		});
	}

	
	private void trimCode(){
		mCode.setText(mCode.getText().toString().trim().replace("\n", ""));
	}
}
