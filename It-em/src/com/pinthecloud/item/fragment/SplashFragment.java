package com.pinthecloud.item.fragment;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.HongkunTestActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ViewUtil;

public class SplashFragment extends ItFragment {

	private final int SPLASH_TIME = 1000;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_splash, container, false);
		if(mPrefHelper.getInt(ViewUtil.MAX_TEXTURE_SIZE_KEY) == PrefHelper.DEFAULT_INT){
			FrameLayout layout = (FrameLayout) view.findViewById(R.id.splash_frag_surface_layout);
			layout.addView(new GetMaxTextureSizeSurfaceView(mActivity));
		} else {
			runItem();
		}
		return view;
	}


	private void runItem() {
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

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
				} else {
					// Has Loggined
					intent.setClass(mActivity, MainActivity.class);
				}
				startActivity(intent);
			}
		}
	}


	private class GetMaxTextureSizeSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

		public GetMaxTextureSizeSurfaceView(Context context) {
			super(context);
			setRenderer(this);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			setMaxTextureSize();
			runItem();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
		}

		@Override
		public void onDrawFrame(GL10 gl) {
		}

		private void setMaxTextureSize(){
			int[] maxTextureSize = new int[1];
			GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
			mPrefHelper.put(ViewUtil.MAX_TEXTURE_SIZE_KEY, maxTextureSize[0]);
		}
	}
}
