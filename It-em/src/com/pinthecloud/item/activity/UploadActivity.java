package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.GalleryFragment;
import com.pinthecloud.item.fragment.UploadFragment;

public class UploadActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);
		
		String[] mPaths = getIntent().getStringArrayExtra(GalleryFragment.GALLERY_PATHS_KEY);
		setToolbar();
		setFragment(UploadFragment.newInstance(mPaths));
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
		actionBar.setDisplayShowHomeEnabled(true);
		
		mToolbar.setNavigationIcon(R.drawable.appbar_close_ic);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
		    @Override
		    public void onClick(View v) {
		        onBackPressed();
		    }
		});
		
		mToolbarLayout.bringToFront();
	}
}
