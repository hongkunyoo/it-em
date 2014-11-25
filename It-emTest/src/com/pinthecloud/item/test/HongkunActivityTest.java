package com.pinthecloud.item.test;

import android.test.ActivityInstrumentationTestCase2;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.SplashActivity;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.test.model.TestObject;

public class HongkunActivityTest extends ActivityInstrumentationTestCase2<SplashActivity>{

	private PrefHelper prefHelper;
	private TestObject testObj;
	private ObjectPrefHelper objPrefHelper;
	private AimHelper aimHelper;


	public HongkunActivityTest() {
		super(SplashActivity.class);
	}


	public HongkunActivityTest(Class<SplashActivity> activityClass) {
		super(activityClass);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prefHelper = ItApplication.getInstance().getPrefHelper();
		objPrefHelper = ItApplication.getInstance().getObjectPrefHelper();
		aimHelper = ItApplication.getInstance().getAimHelper();
	}
}
