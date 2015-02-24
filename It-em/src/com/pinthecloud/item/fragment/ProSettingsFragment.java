package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pinthecloud.item.R;
import com.pinthecloud.item.model.ItUser;

public class ProSettingsFragment extends ItFragment {

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
		View view = inflater.inflate(R.layout.fragment_pro_settings, container, false);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		return view;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.pro_settings));
	}
	
	
	private void findComponent(View view){
		
	}
}
