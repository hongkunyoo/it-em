package com.pinthecloud.item.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.MyLog;

public class HongkunTestFragment extends ItFragment {
	AimHelper aimHelper;
	
	Button btn;
	EditText editText;
	
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
		editText = (EditText)view.findViewById(R.id.editText1);
		
	}

	private void test() {
		aimHelper = ItApplication.getInstance().getAimHelper();
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyLog.log("btn Clicked");
				int page = Integer.parseInt(editText.getText().toString());
				aimHelper.listItem(page, new ItListCallback<Item>(){

					@Override
					public void onCompleted(List<Item> list, int count) {
						// TODO Auto-generated method stub
					}

				});
//				LikeIt like = new LikeIt().rand();
//				String refId = "559952DA-4FE0-444B-9ADB-1DD4B2D373E1";
//				like.setRefId(refId);
//				aimHelper.add(thisFragment, like, new ItEntityCallback<String>() {
//					
//					@Override
//					public void onCompleted(String entity) {
//						// TODO Auto-generated method stub
//						MyLog.log(entity);
//					}
//				});
			}
		});
	}
	
	@Override
	public void handleException(ItException ex) {
		// TODO Auto-generated method stub
		MyLog.log(ex);
	}
}
