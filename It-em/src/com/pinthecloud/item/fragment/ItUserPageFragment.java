package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ProfileSettingsActivity;
import com.pinthecloud.item.adapter.ItUserPagePagerAdapter;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.interfaces.ItUserPageTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.pinthecloud.item.view.PagerSlidingTabStrip;
import com.squareup.picasso.Picasso;

public class ItUserPageFragment extends ItFragment {

	public static int mTabHeight;

	private ProgressBar mProgressBar;
	private RelativeLayout mContainer;

	private LinearLayout mHeader;
	private CircleImageView mProfileImage;
	private TextView mNickName;
	private TextView mDescription;
	private TextView mWebsite;
	private ImageButton mProfileSettings;

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private ItUserPagePagerAdapter mViewPagerAdapter;

	private String mItUserId;
	private ItUser mItUser;

	private boolean[] mIsUpdatedTabs;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mItUserId = mActivity.getIntent().getStringExtra(ItUser.INTENT_KEY);
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
				mProgressBar.setVisibility(View.GONE);
				mContainer.setVisibility(View.VISIBLE);

				setActionBar();
				setComponent();
				setButton();
				setImageView();
				setViewPager();
				setTab();
				setCustomTabName();
			}
		});

		return view;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mItUser.getNickName());
	}


	private void findComponent(View view){
		mContainer = (RelativeLayout)view.findViewById(R.id.my_page_frag_container_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.my_page_frag_progress_bar);
		mHeader = (LinearLayout)view.findViewById(R.id.my_page_frag_header_layout);
		mProfileImage = (CircleImageView)view.findViewById(R.id.my_page_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.my_page_frag_nick_name);
		mDescription = (TextView)view.findViewById(R.id.my_page_frag_description);
		mWebsite = (TextView)view.findViewById(R.id.my_page_frag_website);
		mProfileSettings = (ImageButton)view.findViewById(R.id.my_page_frag_profile_settings);
		mViewPager = (ViewPager)view.findViewById(R.id.my_page_frag_pager);
		mTab = (PagerSlidingTabStrip)view.findViewById(R.id.my_page_frag_tab);
	}


	private void setItUser(){
		if(mItUserId.equals(mItUser.getId())){
			AsyncChainer.notifyNext(mThisFragment);
		} else {
			mUserHelper.get(mThisFragment, mItUserId, new ItEntityCallback<ItUser>() {

				@Override
				public void onCompleted(ItUser entity) {
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
		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(mItUser.getId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(mProfileImage);
	}


	private void setViewPager(){
		mViewPagerAdapter = new ItUserPagePagerAdapter(getFragmentManager(), mActivity, mItUser);
		mViewPagerAdapter.setMyPageTabHolder(new ItUserPageTabHolder() {

			@Override
			public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
				if (mViewPager.getCurrentItem() == pagePosition) {
					int scrollY = getGridScrollY(view, (GridLayoutManager)layoutManager);
					mHeader.scrollTo(0, Math.min(scrollY, mHeader.getHeight() - mTabHeight));
				}
			}

			@Override
			public void updateTabNumber(int position, int number) {
				View tab = mTab.getTab(position);
				TextView numberText = (TextView)tab.findViewById(R.id.tab_my_page_number);
				numberText.setText(""+number);
			}

			@Override
			public void adjustScroll(int scrollHeight) {
			}
			@Override
			public void updateTab() {
			}
		});

		mViewPager.setAdapter(mViewPagerAdapter);
		mIsUpdatedTabs = new boolean[mViewPagerAdapter.getCount()];
	}


	private void setTab(){
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				SparseArrayCompat<ItUserPageTabHolder> myPageTabHolders = mViewPagerAdapter.getMyPageTabHolders();
				ItUserPageTabHolder fragmentContent = myPageTabHolders.valueAt(position);
				fragmentContent.adjustScroll((int) (mHeader.getHeight() - mHeader.getScrollY()));
				if(!mIsUpdatedTabs[position]){
					fragmentContent.updateTab();
					mIsUpdatedTabs[position] = true;
				}
			}
			@Override
			public void onPageSelected(int position) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}


	private void setCustomTabName(){
		for(int i=0 ; i<mViewPagerAdapter.getCount() ; i++){
			View tab = mTab.getTab(i);
			TextView name = (TextView)tab.findViewById(R.id.tab_my_page_name);
			name.setText(mViewPagerAdapter.getPageTitle(i));
		}
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
}
