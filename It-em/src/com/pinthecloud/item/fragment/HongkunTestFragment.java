package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.databases.AimDBHelper;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.MyLog;

public class HongkunTestFragment extends ItFragment {
	AimHelper aimHelper;
	AimDBHelper aimDBHelper;
	
	Button btn;
	Button btn2;
	EditText editText;
	String id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);
		findComponent(view);
		test();
		return view;
	}
	
	private void findComponent(View view) {
		// TODO Auto-generated method stub
		btn = (Button)view.findViewById(R.id.button1);
		btn2 = (Button)view.findViewById(R.id.button2);
		editText = (EditText)view.findViewById(R.id.editText1);
		
	}

	private void test() {
		aimHelper = ItApplication.getInstance().getAimHelper();
		aimDBHelper = ItApplication.getInstance().getAimDBHelper();
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyLog.log("btn Clicked");
				Item item = new Item().rand(true);
				id = item.getId();
				int i = aimDBHelper.add(item);
				MyLog.log("result",i, item);
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Item item = new Item();
				item.setId(id);
				Item ii = aimDBHelper.get(item);
				MyLog.log(ii);
			}
		});
	}
	
	@Override
	public void handleException(ItException ex) {
		// TODO Auto-generated method stub
		MyLog.log(ex);
	}
}
