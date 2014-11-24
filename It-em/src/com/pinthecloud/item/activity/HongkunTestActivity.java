package com.pinthecloud.item.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.HongkunTestFragment;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.ItemFragment;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.MyLog;

public class HongkunTestActivity extends ItActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_frame);
		setFragment();
	}
	
	
	
	private void setFragment(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ItFragment fragment = new HongkunTestFragment();
		fragmentTransaction.add(R.id.activity_container, fragment);
		fragmentTransaction.commit();
	}
}
