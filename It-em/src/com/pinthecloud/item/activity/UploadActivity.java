package com.pinthecloud.item.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.fragment.UploadFragment;
import com.pinthecloud.item.interfaces.DialogCallback;

public class UploadActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	private List<String> mImagePathList;

	public List<String> getImagePathList() {
		return mImagePathList;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_up, R.anim.pop_out);
		setContentView(R.layout.activity_toolbar_frame);

		mImagePathList = new ArrayList<String>();
		setToolbar();
		setFragment(new UploadFragment());
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.pop_in, R.anim.slide_out_down);
	}


	@Override
	public void onBackPressed() {
		if(getSupportFragmentManager().getBackStackEntryCount() == 0 && mImagePathList.size() > 0){
			String message = getResources().getString(R.string.delete_item);
			String delete = getResources().getString(R.string.delete);
			ItAlertDialog dialog = ItAlertDialog.newInstance(message, delete, null, null, false, true);

			dialog.setCallback(new DialogCallback() {

				@Override
				public void doPositive(Bundle bundle) {
					mThisActivity.finish();
				}
				@Override
				public void doNeutral(Bundle bundle) {
					// Do nothing
				}
				@Override
				public void doNegative(Bundle bundle) {
					// Do nothing
				}
			});
			dialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
		} else {
			super.onBackPressed();	
		}
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

		mToolbarLayout.bringToFront();
	}
}
