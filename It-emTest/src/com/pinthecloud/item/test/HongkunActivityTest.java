package com.pinthecloud.item.test;

import android.test.ActivityInstrumentationTestCase2;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.activity.SplashActivity;
import com.pinthecloud.item.helper.ObjectPrefHelper;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.test.model.TestObject;

public class HongkunActivityTest extends ActivityInstrumentationTestCase2<SplashActivity>{
	
	PrefHelper prefHelper;
	TestObject testObj;
	ObjectPrefHelper objPrefHelper;
	
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
		objPrefHelper = ItApplication.getInstance().getObjPrefHelper();
		
		assertNotNull(ItApplication.getInstance());
		assertNotNull(prefHelper);
		assertNotNull(objPrefHelper);
	}
	
	public void testMy() {
		testObj = new TestObject();
		
		testObj.setTestInt(123);
		testObj.setTestBoolean(true);
		testObj.setTestFloat(12345f);
		testObj.setTestString("testing String~");
		
		objPrefHelper.put(testObj);
		
		TestObject o = new TestObject();
		TestObject result = objPrefHelper.get(o);
		assertEquals(testObj, result);
	}
	
}
