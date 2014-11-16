package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.AppSettingsActivity;
import com.pinthecloud.item.adapter.ItemMyGridAdapter;
import com.pinthecloud.item.model.Item;

public class ItemMyFragment extends ItFragment {

	private TextView nickNameText;
	private Button newButton;

	private GridView itemMyGrid;
	private ItemMyGridAdapter itemMyGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_my, container, false);
		setHasOptionsMenu(true);
		findComponent(view);
		setComponent();
		setButton();
		setItemMyGrid();
		updateItemMyGrid();
		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.item_my, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_my_settings:
			Intent intent = new Intent(activity, AppSettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		nickNameText = (TextView)view.findViewById(R.id.item_my_frag_nick_name);
		newButton = (Button)view.findViewById(R.id.item_my_frag_new_button);
		itemMyGrid = (GridView)view.findViewById(R.id.item_my_frag_grid);
	}


	private void setComponent(){
	}


	private void setButton(){
		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}	


	private void setItemMyGrid(){
		itemMyGridAdapter = new ItemMyGridAdapter(activity, thisFragment);
		itemMyGrid.setAdapter(itemMyGridAdapter);

		itemMyGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}


	private void updateItemMyGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			itemMyGridAdapter.add(item);
		}
	}
}
