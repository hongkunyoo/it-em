package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ProfileSettingsActivity;
import com.pinthecloud.item.activity.UploadActivity;
import com.pinthecloud.item.adapter.MyPagePagerAdapter;
import com.pinthecloud.item.dialog.ItAlertListDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItDialogCallback;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ScrollTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.util.FileUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class MyPageFragment extends ItFragment {

	public static int mTabHeight;

	private ProgressBar mProgressBar;
	private LinearLayout mHeader;
	private CircleImageView mProfileImage;
	private TextView mNickName;
	private TextView mDescription;
	private TextView mWebsite;
	private Button mProfileSettings;

	private ViewPager mViewPager;
	private MyPagePagerAdapter mViewPagerAdapter;
	private LinearLayout mTab;
	private LinearLayout mMyItemTab;
	private LinearLayout mItItemTab;
	private TextView mMyItemNumber;
	private TextView mItItemNumber;

	private String mItUserId;
	private ItUser mItUser;

	private boolean[] updateMyPageItem = {true, true};


	public static MyPageFragment newInstance(String itUserId) {
		MyPageFragment fragment = new MyPageFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ItUser.INTENT_KEY, itUserId);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItUserId = getArguments().getString(ItUser.INTENT_KEY);
		mItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_page, container, false);
		setHasOptionsMenu(true);
		findComponent(view);

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				setItUser();
			}
		}, new Chainable(){

			@Override
			public void doNext(final ItFragment frag, Object... params) {
				setComponent();
				setButton();
				setImageView();
				setViewPager();
				setTab();
			}
		});

		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.my_page, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.my_page_upload:
			String[] itemList = getResources().getStringArray(R.array.image_select_string_array);
			ItDialogCallback[] callbacks = getDialogCallbacks(itemList);
			ItAlertListDialog listDialog = new ItAlertListDialog(null, itemList, callbacks);
			listDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.my_page_frag_progress_bar);
		mHeader = (LinearLayout)view.findViewById(R.id.my_page_frag_header_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.my_page_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		mDescription = (TextView)view.findViewById(R.id.my_page_frag_description);
		mWebsite = (TextView)view.findViewById(R.id.my_page_frag_website);
		mProfileSettings = (Button)view.findViewById(R.id.my_page_frag_profile_settings);
		mViewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
		mTab = (LinearLayout)view.findViewById(R.id.my_page_frag_tab);
		mMyItemTab = (LinearLayout)view.findViewById(R.id.my_page_frag_my_item_tab);
		mItItemTab = (LinearLayout)view.findViewById(R.id.my_page_frag_it_item_tab);
		mMyItemNumber = (TextView)view.findViewById(R.id.my_page_frag_my_item_number);
		mItItemNumber = (TextView)view.findViewById(R.id.my_page_frag_it_item_number);
	}


	private void setItUser(){
		if(mItUserId.equals(mItUser.getId())){
			mProgressBar.setVisibility(View.GONE);
			AsyncChainer.notifyNext(mThisFragment);
		} else {
			mUserHelper.get(mThisFragment, mItUserId, new ItEntityCallback<ItUser>() {

				@Override
				public void onCompleted(ItUser entity) {
					mProgressBar.setVisibility(View.GONE);
					mItUser = entity;
					AsyncChainer.notifyNext(mThisFragment);
				}
			});
		}
	}


	private void setComponent(){
		mTabHeight = mTab.getHeight();
		mNickName.setText(mItUser.getNickName());
		mDescription.setText(mItUser.getSelfIntro());
		mWebsite.setText(mItUser.getWebPage());
	}


	private void setButton(){
		mProfileSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ProfileSettingsActivity.class);
				startActivity(intent);
			}
		});
	}


	private void setImageView(){
		Picasso.with(mActivity)
		.load(BlobStorageHelper.getUserProfileImgUrl(mItUser.getId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.ic_launcher)
		.error(R.drawable.ic_launcher)
		.fit()
		.into(mProfileImage);
	}


	private void setViewPager(){
		mViewPagerAdapter = new MyPagePagerAdapter(getFragmentManager(), mActivity, mItUser);
		mViewPagerAdapter.setTabHolderScrollingContent(new ScrollTabHolder() {

			@Override
			public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
				if (mViewPager.getCurrentItem() == pagePosition) {
					int scrollY = getGridScrollY(view, (GridLayoutManager)layoutManager);
					mHeader.scrollTo(0, Math.min(scrollY, mHeader.getHeight() - mTab.getHeight()));
				}
			}

			@Override
			public void updateTabNumber(int position, int number) {
				if(position == MyPagePagerAdapter.MY_PAGE_ITEM.MY_ITEM.ordinal()){
					mMyItemNumber.setText(""+number);
				}else if(position == MyPagePagerAdapter.MY_PAGE_ITEM.IT_ITEM.ordinal()){
					mItItemNumber.setText(""+number);
				}
			}

			@Override
			public void adjustScroll(int scrollHeight) {
			}
		});

		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mViewPagerAdapter.getScrollTabHolders();
				ScrollTabHolder fragmentContent = scrollTabHolders.valueAt(position);
				fragmentContent.adjustScroll((int) (mHeader.getHeight() - mHeader.getScrollY()));
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}


	private void setTab(){
		mMyItemTab.setSelected(true);
		mMyItemTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyItemTab.setSelected(true);
				mItItemTab.setSelected(false);
				mViewPager.setCurrentItem(MyPagePagerAdapter.MY_PAGE_ITEM.MY_ITEM.ordinal(), false);
			}
		});

		mItItemTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyItemTab.setSelected(false);
				mItItemTab.setSelected(true);
				mViewPager.setCurrentItem(MyPagePagerAdapter.MY_PAGE_ITEM.IT_ITEM.ordinal(), false);
			}
		});
	}


	private int getGridScrollY(RecyclerView view, GridLayoutManager layoutManager) {
		View c = view.getChildAt(0);
		if(c == null){
			return 0;
		}

		int findFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
		int spanCount = layoutManager.getSpanCount();
		int headerHeight = 0;
		if (findFirstVisibleItemPosition >= spanCount) {
			headerHeight = mHeader.getHeight();
		}

		return -c.getTop() + (findFirstVisibleItemPosition / spanCount) * c.getHeight() + headerHeight;
	}


	private ItDialogCallback[] getDialogCallbacks(String[] itemList){
		final Intent intent = new Intent(mActivity, UploadActivity.class);
		ItDialogCallback[] callbacks = new ItDialogCallback[itemList.length];
		callbacks[0] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Get image from gallery
				intent.putExtra(UploadFragment.MEDIA_KEY, FileUtil.GALLERY);
				startActivity(intent);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[1] = new ItDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Get image from camera
				intent.putExtra(UploadFragment.MEDIA_KEY, FileUtil.CAMERA);
				startActivity(intent);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
			}
		};

		callbacks[itemList.length-1] = null;
		return callbacks;
	}
}
