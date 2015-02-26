package com.pinthecloud.item.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

public class BeProActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	
	private TextView mHomepage;
	private EditText mCode;
	private Button mSubmit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_be_pro);

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
	
	
	private void findComponent(){
		mHomepage = (TextView)findViewById(R.id.be_pro_homepage);
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
		mHomepage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String homepage = "http://" + mHomepage.getText().toString();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
				startActivity(intent);
			}
		});
		
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
		ItUser myItUser = mObjectPrefHelper.get(ItUser.class);
		mUserHelper.bePro(myItUser, inviteKey, ItUser.TYPE.PRO, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				if(entity != null){
					mApp.dismissProgressDialog();
					Toast.makeText(mThisActivity, getResources().getString(R.string.valid_pro), Toast.LENGTH_LONG).show();
					
					mObjectPrefHelper.put(entity);
					
					Intent intent = new Intent(mThisActivity, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					mApp.dismissProgressDialog();
					Toast.makeText(mThisActivity, getResources().getString(R.string.invalid_pro), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	
	private void trimCode(){
		mCode.setText(mCode.getText().toString().trim().replace("\n", ""));
	}
}
