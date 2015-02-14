package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ProductTagListAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.ProductTag;
import com.pinthecloud.item.util.ViewUtil;

public class ProductTagDialog extends ItDialogFragment {

	private TextView mTitle;

	private ProgressBar mProgressBar;
	private RecyclerView mListView;
	private ProductTagListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<ProductTag> mOriginTagList;
	private List<ProductTag> mTagList;
	private Item mItem;


	public static ProductTagDialog newInstance(Item item, ArrayList<ProductTag> tagList) {
		ProductTagDialog dialog = new ProductTagDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		bundle.putParcelableArrayList(ProductTag.INTENT_KEY, tagList);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItem = getArguments().getParcelable(Item.INTENT_KEY);
		mOriginTagList = getArguments().getParcelableArrayList(ProductTag.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_product_tag, container, false);
		findComponent(view);
		setList();

		if(mTagList.size() > 0){
			setTitle();
		} else {
			updateList();
		}

		return view;
	}


	private void findComponent(View view){
		mTitle = (TextView)view.findViewById(R.id.product_tag_title);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListView = (RecyclerView)view.findViewById(R.id.product_tag_frag_list);
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		if(mOriginTagList != null){
			mTagList = getProductTagList(mOriginTagList);
		} else {
			mTagList = new ArrayList<ProductTag>();
		}
		
		mListAdapter = new ProductTagListAdapter(mActivity, mThisFragment, mTagList);
		mListView.setAdapter(mListAdapter);

		mListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					mListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					mListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				
				ViewUtil.setListHeightBasedOnChildren(mListView, mTagList.size());
			}
		});
	}


	private void updateList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.INVISIBLE);

		mAimHelper.list(ProductTag.class, mItem.getId(), new ListCallback<ProductTag>() {

			@Override
			public void onCompleted(List<ProductTag> list, int count) {
				if(isAdded()){
					mProgressBar.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);

					mTagList.clear();
					mListAdapter.addAll(getProductTagList(list));

					ViewUtil.setListHeightBasedOnChildren(mListView, mTagList.size());
					setTitle();
				}
			}
		});
	}


	private List<ProductTag> getProductTagList(List<ProductTag> originTagList){
		List<ProductTag> tagList = new ArrayList<ProductTag>();
		tagList.addAll(originTagList);
		
		Collections.sort(tagList, new Comparator<ProductTag>(){

			@Override
			public int compare(ProductTag lhs, ProductTag rhs) {
				return lhs.getCategory() > rhs.getCategory() ? 1 : 
					lhs.getCategory() == rhs.getCategory() ? 0 : -1;
			}
		});
		
		tagList.add(0, new ProductTag(tagList.get(0).getCategory()));
		for(int i=1 ; i<tagList.size()-1 ; i++){
			if(tagList.get(i).getCategory() != tagList.get(i+1).getCategory()){
				tagList.add(i+1, new ProductTag(tagList.get(i+1).getCategory()));
				i++;
			}
		}
		return tagList;
	}


	private void setTitle(){
		mTitle.setText(getResources().getString(R.string.product_tag));
	}
}
