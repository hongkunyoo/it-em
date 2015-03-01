package com.pinthecloud.item.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pinthecloud.item.R;

public class ProSettingsActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;
	
	private TextView mMileage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_pro_settings);

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
		mMileage = (TextView)findViewById(R.id.pro_settings_mileage);
	}
	
	
	private void setComponent(){
		
	}
	
	
	private void setButton(){
		
	}
}
