package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.CollectItemGridAdapter;
import com.pinthecloud.item.model.Item;

public class CollectItemFragment extends ItFragment {

	private GridView collectItemGrid;
	private CollectItemGridAdapter collectItemGridAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_collect_item, container, false);
		findComponent(view);
		setCollectItemGrid();
		updateCollectItemGrid();
		return view;
	}


	private void findComponent(View view){
		collectItemGrid = (GridView)view.findViewById(R.id.collect_item_frag_grid);
	}


	private void setCollectItemGrid(){
		collectItemGridAdapter = new CollectItemGridAdapter(activity, thisFragment);
		collectItemGrid.setAdapter(collectItemGridAdapter);

		collectItemGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}


	private void updateCollectItemGrid() {
		for(int i=0 ; i<5 ; i++){
			Item item = new Item();
			item.setContent(""+i);
			collectItemGridAdapter.add(item);
		}
	}
}
