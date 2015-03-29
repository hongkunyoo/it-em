package com.pinthecloud.item.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.pinthecloud.item.R;

public class WebViewActivity extends ItActivity {

	public static final String WEB_VIEW_INTENT_KEY = "WEB_VIEW_INTENT_KEY";

	private View mToolbarLayout;
	private Toolbar mToolbar;
	
	private ProgressBar mProgressBar;
	private WebView mWebView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_web_view);

		setToolbar();
		findComponent();
		setComponent();
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
	}


	@Override
	public void onBackPressed() {
		if(mWebView.canGoBack()) {
			mWebView.goBack();
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
		mToolbar.setNavigationIcon(R.drawable.appbar_close_ic);

		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private void findComponent(){
		mProgressBar = (ProgressBar)findViewById(R.id.web_view_progress_bar);
		mWebView = (WebView)findViewById(R.id.web_view);
	}


	@SuppressLint("SetJavaScriptEnabled")
	private void setComponent(){
		mProgressBar.bringToFront();
		
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);

		String uri = getIntent().getStringExtra(WEB_VIEW_INTENT_KEY);
		mWebView.loadUrl(uri);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setWebChromeClient(new MyWebChromeClient());
	}


	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
            mToolbar.setTitle(view.getTitle());
        }
	}


	private class MyWebChromeClient extends WebChromeClient {
		
		@Override
		public void onProgressChanged(WebView view, int progress){
			if(progress < 100) {
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressBar.setProgress(progress);
			} else {
				mProgressBar.setVisibility(View.GONE);
			}
        }
	}
}
