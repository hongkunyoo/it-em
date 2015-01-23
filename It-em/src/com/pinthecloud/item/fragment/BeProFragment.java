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
import android.widget.Toast;

import com.pinthecloud.item.R;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;

public class BeProFragment extends ItFragment {

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
				mApp.showProgressDialog(mActivity);
				trimCode();
				bePro(mCode.getText().toString());
			}
		});
	}


	private void bePro(final String inviteKey){
		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				mAimHelper.isValid(inviteKey, ItUser.TYPE.PRO, new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						AsyncChainer.notifyNext(frag, entity);
					}
				});
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				boolean result = (Boolean)params[0];
				if(result) {
					mMyItUser.setType(ItUser.TYPE.PRO);
					mUserHelper.update(mMyItUser, new EntityCallback<ItUser>() {

						@Override
						public void onCompleted(ItUser entity) {
							mObjectPrefHelper.put(entity);
							AsyncChainer.notifyNext(frag, getResources().getString(R.string.valid_pro));
						}
					});
				} else {
					AsyncChainer.notifyNext(frag, getResources().getString(R.string.invalid_pro));
				}
			}
		}, new Chainable(){

			@Override
			public void doNext(ItFragment frag, Object... params) {
				mApp.dismissProgressDialog();

				String message = params[0].toString();
				Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();

				//				Intent intent = new Intent(mActivity, MainActivity.class);
				//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				//				startActivity(intent);
			}
		});
	}


	private void trimCode(){
		mCode.setText(mCode.getText().toString().trim().replace("\n", ""));
	}
}
