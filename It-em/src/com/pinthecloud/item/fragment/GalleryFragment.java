package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.GalleryAdapter;
import com.pinthecloud.item.interfaces.GalleryCallback;
import com.pinthecloud.item.model.Gallery;
import com.pinthecloud.item.model.GalleryFolder;

public class GalleryFragment extends ItFragment implements GalleryCallback{

	public static final String GALLERY_PATHS_KEY = "GALLERY_PATHS_KEY";
	
	private RecyclerView mGridView;
	private GalleryAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Gallery> mGalleryList;

	
	public static ItFragment newInstance(ArrayList<Gallery> galleryList) {
		ItFragment fragment = new GalleryFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(Gallery.INTENT_KEY, galleryList);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGalleryList = getArguments().getParcelableArrayList(Gallery.INTENT_KEY);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_gallery, container, false);

		mGaHelper.sendScreen(mThisFragment);
		setHasOptionsMenu(true);
		
		findComponent(view);
		setGrid();

		return view;
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.gallery, menu);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.gallery_menu_submit);
		menuItem.setEnabled(mGridAdapter.getSelected().size() > 0);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.gallery_menu_submit:
			List<Gallery> selected = mGridAdapter.getSelected();

			String[] paths = new String[selected.size()];
			for (int i = 0; i < paths.length; i++) {
				paths[i] = selected.get(i).getPath();
			}

			Intent data = new Intent().putExtra(GALLERY_PATHS_KEY, paths);
			mActivity.setResult(Activity.RESULT_OK, data);
			mActivity.finish();
			break;
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	
	@Override
	public void clickGallery(Gallery gallery) {
		mActivity.invalidateOptionsMenu();
		setTitle();
	}
	
	
	@Override
	public void clickFolder(GalleryFolder folder) {
	}
	
	
	private void findComponent(View view){
		mGridView = (RecyclerView)view.findViewById(R.id.gallery_frag_grid);
	}


	private void setGrid(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.gallery_grid_column_num);
		mGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mGridAdapter = new GalleryAdapter(mGalleryList);
		mGridAdapter.setGalleryCallback(this);
		mGridView.setAdapter(mGridAdapter);
	}


	private void setTitle(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		String selectPicture = getResources().getString(R.string.select_picture);
		int selected = mGridAdapter.getSelected().size();
		if(selected > 0){
			int maxGallery = getResources().getInteger(R.integer.max_gallery_num);	
			actionBar.setTitle(selectPicture + "  " + selected + "/" + maxGallery);
		} else {
			actionBar.setTitle(selectPicture);
		}
	}
}
