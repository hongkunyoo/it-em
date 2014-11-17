package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ItemMyGridAdapter;
import com.pinthecloud.item.model.Item;

public class ItemMyFragment extends ItFragment {

	private GridView itemMyGrid;
	private ItemMyGridAdapter itemMyGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item_my, container, false);
		findComponent(view);
		setItemMyGrid();
		updateItemMyGrid();
		return view;
	}


	private void findComponent(View view){
		itemMyGrid = (GridView)view.findViewById(R.id.item_my_frag_grid);
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
