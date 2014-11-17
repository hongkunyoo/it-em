package com.pinthecloud.item.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.pinthecloud.item.activity.SplashActivity;

// Test case for Activity
// Naming convention : OOOOTest, e.g. HongkunTest
public class ActivitySampleTest extends ActivityInstrumentationTestCase2<SplashActivity> {
	
	Activity activity;
	
	
	public ActivitySampleTest() {
		super(SplashActivity.class);
	}
	public ActivitySampleTest(Class<SplashActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}
	
	// Do the setup things here.
	// the setUp() method will be invoked for every test cases.
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		// you can get Activity from getActivity method, in this case it will be SplashActivity
		activity = getActivity();
	}
	
	
	// Test anything what you want here.
	// You can add what ever test case method like, testMyPref, testMyActivity
	public void testSomething() {
		boolean expectedValue = true;
		boolean actualValue = true;
		
		assertEquals(expectedValue, actualValue);
	}
}
