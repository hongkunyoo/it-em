package com.pinthecloud.item.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.GalleryAdapter;
import com.pinthecloud.item.model.Gallery;
import com.pinthecloud.item.model.GalleryFolder;

public class GalleryFragment extends ItFragment {

	private RecyclerView mGridView;
	private GalleryAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private GalleryFolder mFolder;
	private List<String> mImagePathList;


	public static ItFragment newInstance(GalleryFolder folder) {
		ItFragment fragment = new GalleryFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(GalleryFolder.INTENT_KEY, folder);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFolder = getArguments().getParcelable(GalleryFolder.INTENT_KEY);
		mImagePathList = ((UploadActivity)mActivity).getImagePathList();
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
		setActionBar();
		
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
			List<Gallery> galleryList = mGridAdapter.getSelected();
			for (Gallery gallery : galleryList) {
				mImagePathList.add(gallery.getPath());
			}
			getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		}
		return super.onOptionsItemSelected(menuItem);
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

		mGridAdapter = new GalleryAdapter(mActivity, mThisFragment, mFolder.getGalleryList(), mImagePathList);
		mGridView.setAdapter(mGridAdapter);
	}
	
	
	public void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		int selected = mImagePathList.size() + (mGridAdapter == null ? 0 : mGridAdapter.getSelected().size());
		if(selected > 0){
			int maxGallery = mActivity.getResources().getInteger(R.integer.gallery_max_num);
			actionBar.setTitle(mFolder.getName() + "  (" + selected + "/" + maxGallery + ")");
		} else {
			actionBar.setTitle(mFolder.getName());
		}
	}
}
