package com.pinthecloud.item.fragment;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.HongkunTestActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ImageUtil;

public class SplashFragment extends ItFragment {

	private final int SPLASH_TIME = 1000;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);

		if(mPrefHelper.getInt(ImageUtil.MAX_TEXTURE_SIZE_KEY) == PrefHelper.DEFAULT_INT){
			FrameLayout layout = (FrameLayout) view.findViewById(R.id.splash_frag_surface_layout);
			layout.addView(new GetMaxTextureSizeSurfaceView(mActivity));
		} else {
			runItem();
		}

		return view;
	}


	private void runItem() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				goToNextActivity();
			}
		}, SPLASH_TIME);
	}


	private void goToNextActivity() {
		try {
			String className = "com.pinthecloud.item.util.HongUtil2";
			Class.forName(className);
			Intent hongTent = new Intent(mActivity, HongkunTestActivity.class);
			startActivity(hongTent);
		} catch (ClassNotFoundException e) {
			if(isAdded()){
				Intent intent = new Intent();
				if (!mObjectPrefHelper.get(ItUser.class).isLoggedIn()){
					// New User
					intent.setClass(mActivity, LoginActivity.class);
				} else{
					// Has Loggined
					intent.setClass(mActivity, MainActivity.class);			
				}
				startActivity(intent);
			}
		}
	}


	private class GetMaxTextureSizeSurfaceView extends SurfaceView implements
	SurfaceHolder.Callback {

		public GetMaxTextureSizeSurfaceView(Context context) {
			super(context);
			getHolder().addCallback(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			setMaxTextureSize();
			runItem();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// Do nothing
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// Do nothing
		}

		private void setMaxTextureSize(){
			int[] maxTextureSize = new int[1];
			GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
			mPrefHelper.put(ImageUtil.MAX_TEXTURE_SIZE_KEY, maxTextureSize[0]);
		}
	}
}
