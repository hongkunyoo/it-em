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
import com.pinthecloud.item.databases.AimDBHelper;
import com.pinthecloud.item.helper.AimHelper;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItDateTime;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ItLog;

public class HongkunTestFragment extends ItFragment {

	AimHelper aimHelper;
	AimDBHelper aimDBHelper;
	ItDateTime today;
	Button btn;
	Button btn2;
	EditText editText;
	String id;
	Item item;
	ItDateTime datetime;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);
		findComponent(view);
		test();
		return view;
	}


	private void findComponent(View view) {
		btn = (Button)view.findViewById(R.id.button1);
		btn2 = (Button)view.findViewById(R.id.button2);
		editText = (EditText)view.findViewById(R.id.editText1);
	}


	private void test() {
		aimHelper = ItApplication.getInstance().getAimHelper();
		aimDBHelper = ItApplication.getInstance().getAimDBHelper();
		today = ItDateTime.getToday();
		final ItUser user = mObjectPrefHelper.get(ItUser.class);
		ItLog.log(user.getId());
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aimHelper.listMyItem(user.getId(), new ListCallback<Item>() {

					@Override
					public void onCompleted(List<Item> list, int count) {
						ItLog.log(list);
					}
				});
			}
		});

		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Item item = new Item();
				item.setId(id);
				Item ii = aimDBHelper.get(item);
				ItLog.log(ii);
			}
		});
	}
}
