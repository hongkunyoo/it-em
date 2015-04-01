package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

public class BeProFragment extends ItFragment {

	private TextView mApplyEditor;
	private EditText mCode;
	private Button mSubmit;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_be_pro, container, false);

		mGaHelper.sendScreen(mThisFragment);
		setActionBar();
		findComponent(view);
		setComponent();
		setButton();

		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.be_pro));
	}


	private void findComponent(View view){
		mApplyEditor = (TextView)view.findViewById(R.id.be_pro_apply_editor);
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
		mApplyEditor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String applyEditor = getResources().getString(R.string.apply_editor);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(applyEditor));
				startActivity(intent);
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
		ItUser user = mObjectPrefHelper.get(ItUser.class);
		mUserHelper.bePro(user, inviteKey, ItUser.TYPE.PRO, new EntityCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser entity) {
				if(entity != null){
					mApp.dismissProgressDialog();
					Toast.makeText(mActivity, getResources().getString(R.string.valid_pro), Toast.LENGTH_LONG).show();

					mObjectPrefHelper.put(entity);

					getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					ItFragment fragment = new ProSettingsFragment();
					mActivity.replaceFragment(fragment, true, R.anim.slide_in_pop_up, 0, R.anim.pop_in, R.anim.slide_out_pop_down);
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
