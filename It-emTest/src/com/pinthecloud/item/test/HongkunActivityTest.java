package com.pinthecloud.item.test;

import android.test.ActivityInstrumentationTestCase2;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.SplashActivity;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.test.model.TestObject;
import com.pinthecloud.item.test.util.MyLog;

public class HongkunActivityTest extends ActivityInstrumentationTestCase2<SplashActivity>{
	
	PrefHelper prefHelper;
	TestObject testObj;
	ObjectPrefHelper objPrefHelper;
	AimHelper aimHelper;
	
	public HongkunActivityTest() {
		super(SplashActivity.class);
	}
	
	public HongkunActivityTest(Class<SplashActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		prefHelper = ItApplication.getInstance().getPrefHelper();
		objPrefHelper = ItApplication.getInstance().getObjPrefHelper();
		aimHelper = ItApplication.getInstance().getAimHelper();
		
	}
	
}
