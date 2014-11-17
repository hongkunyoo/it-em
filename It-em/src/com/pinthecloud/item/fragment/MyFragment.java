package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.SettingsActivity;
import com.pinthecloud.item.activity.UploadActivity;

public class MyFragment extends ItFragment {

	private TextView nickNameText;
	private Button uploadButton;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my, container, false);
		setHasOptionsMenu(true);
		findComponent(view);
		setComponent();
		setButton();
		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.my, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_my_settings:
			Intent intent = new Intent(activity, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		nickNameText = (TextView)view.findViewById(R.id.my_frag_nick_name);
		uploadButton = (Button)view.findViewById(R.id.my_frag_upload_button);
	}


	private void setComponent(){
	}


	private void setButton(){
		uploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, UploadActivity.class);
				startActivity(intent);
			}
		});
	}	
}
