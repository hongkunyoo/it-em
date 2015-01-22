package com.pinthecloud.item.fragment;

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

import com.pinthecloud.item.R;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.ItLog;
import com.pinthecloud.item.util.AsyncChainer.Chainable;

import de.greenrobot.event.EventBus;

public class BeProFragment extends ItFragment {

	private EditText mCode;
	private Button mSubmit;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_be_pro, container, false);
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
		mCode = (EditText)view.findViewById(R.id.be_pro_frag_code);
		mSubmit = (Button)view.findViewById(R.id.be_pro_frag_submit);
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
				
				final String inviteKey = mCode.getText().toString();
				
				if (inviteKey == null || "".equals(inviteKey)) {
					// TODO : Seung Min --> Do SOMETHING
					// I make Error on purpose to let you Know!!
					_ItLog.log("Seung Min Change Here");
					
					return;
				}
				AsyncChainer.asyncChain(mThisFragment, new Chainable(){

					@Override
					public void doNext(final ItFragment frag, Object... params) {
						// TODO Auto-generated method stub
						mAimHelper.isValid(inviteKey, ItUser.TYPE.PRO, new EntityCallback<Boolean>() {

							@Override
							public void onCompleted(Boolean entity) {
								// TODO Auto-generated method stub
								if (entity) AsyncChainer.notifyNext(frag, entity);
								else {
									EventBus.getDefault().post(new ItException("isValid", ItException.TYPE.INVALID_KEY));
									return;
								}
							}
						});
					}
					
				}, new Chainable(){

					@Override
					public void doNext(ItFragment frag, Object... params) {
						// TODO Auto-generated method stub
						ItUser user = mObjectPrefHelper.get(ItUser.class);
						user.setType(ItUser.TYPE.PRO);
						mUserHelper.update(user, new EntityCallback<ItUser>() {
							
							@Override
							public void onCompleted(ItUser entity) {
								// TODO : Seung Min --> Do SOMETHING
								// I make Error on purpose to let you Know!!
								_ItLog.log("Seung Min Change Here");
								mObjectPrefHelper.put(entity);
							}
						});
					}
				});
			}
		});
	}
}
