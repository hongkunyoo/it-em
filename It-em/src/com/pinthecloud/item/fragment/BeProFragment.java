package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

public class BeProFragment extends ItFragment {

	private TextView mHomepage;
	private EditText mCode;
	private Button mSubmit;
	private ItUser mMyItUser;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_be_pro, container, false);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();
		return view;
	}
	
	
	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle("");
	}
	
	
	private void findComponent(View view){
		mHomepage = (TextView)view.findViewById(R.id.be_pro_homepage);
		mCode = (EditText)view.findViewById(R.id.be_pro_code);
		mSubmit = (Button)view.findViewById(R.id.be_pro_submit);
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
				mActivity.startActivity(intent);
			}
		});
		
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				trimCode();
				bePro(mCode.getText().toString());
			}
		});
	}
	
	
	private void bePro(final String inviteKey){
		mUserHelper.bePro(mMyItUser, inviteKey, ItUser.TYPE.PRO, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				if(entity != null){
					mApp.dismissProgressDialog();
					Toast.makeText(mActivity, getResources().getString(R.string.valid_pro), Toast.LENGTH_LONG).show();
					
					mObjectPrefHelper.put(entity);
					
					Intent intent = new Intent(mActivity, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					mApp.dismissProgressDialog();
					Toast.makeText(mActivity, getResources().getString(R.string.invalid_pro), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	
	private void trimCode(){
		mCode.setText(mCode.getText().toString().trim().replace("\n", ""));
	}
}
