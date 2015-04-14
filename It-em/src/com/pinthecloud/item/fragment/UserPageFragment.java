package com.pinthecloud.item.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.SettingsActivity;
import com.pinthecloud.item.activity.UserPageActivity;
import com.pinthecloud.item.adapter.UserPagePagerAdapter;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.dialog.ProfileImageDialog;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.UserPageScrollTabHolder;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.AsyncChainer;
import com.pinthecloud.item.util.AsyncChainer.Chainable;
import com.pinthecloud.item.view.PagerSlidingTabStrip;

public class UserPageFragment extends MainTabFragment {

	private final int SETTINGS = 0;

	private ActionBar mActionBar;
	private ProgressBar mProgressBar;
	private RelativeLayout mContainer;

	private View mHeader;
	private ImageView mProfileImage;
	private TextView mNickName;
	private TextView mDescription;
	private TextView mWebsite;
	private ImageButton mSettings;

	private PagerSlidingTabStrip mTab;
	private ViewPager mViewPager;
	private UserPagePagerAdapter mViewPagerAdapter;

	private String mUserId;
	private ItUser mUser;


	public static MainTabFragment newInstance(String userId) {
		MainTabFragment fragment = new UserPageFragment();
		Bundle args = new Bundle();
		args.putString(ItUser.INTENT_KEY, userId);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserId = getArguments().getString(ItUser.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_user_page, container, false);
		findComponent(view);
		setButton();

		if(mActivity instanceof UserPageActivity){
			updateFragment();
		}

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case SETTINGS:
			if (resultCode == Activity.RESULT_OK){
				mUser = data.getParcelableExtra(ItUser.INTENT_KEY);
				updateProfile();

				mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						} else {
							mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}

						SparseArrayCompat<UserPageScrollTabHolder> scrollTabHolderList = mViewPagerAdapter.getScrollTabHolderList();
						for(int i=0 ; i<scrollTabHolderList.size() ; i++){
							scrollTabHolderList.valueAt(i).updateHeader(mHeader.getHeight());	
						}
					}
				});
			}
			break;
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		if(mUser != null){
			setProfileImage();
		}
	}


	@Override
	public void onStop() {
		super.onStop();
		mProfileImage.setImageBitmap(null);
	}


	@Override
	public void updateFragment() {
		mGaHelper.sendScreen(mThisFragment);

		AsyncChainer.asyncChain(mThisFragment, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				mProgressBar.setVisibility(View.VISIBLE);
				mContainer.setVisibility(View.GONE);
				setUser(obj);
			}
		}, new Chainable(){

			@Override
			public void doNext(Object obj, Object... params) {
				mProgressBar.setVisibility(View.GONE);
				mContainer.setVisibility(View.VISIBLE);
				updateProfile();
				setProfileImage();

				mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						} else {
							mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}

						setViewPager();
						setTab();
						setTabName();
					}
				});
			}
		});
	}


	private void findComponent(View view){
		mActionBar = mActivity.getSupportActionBar();
		mContainer = (RelativeLayout)view.findViewById(R.id.user_page_frag_container_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.user_page_frag_progress_bar);
		mHeader = (LinearLayout)view.findViewById(R.id.user_page_frag_header_layout);
		mProfileImage = (ImageView)view.findViewById(R.id.user_page_frag_profile_image);
		mNickName = (TextView)view.findViewById(R.id.user_page_frag_nick_name);
		mDescription = (TextView)view.findViewById(R.id.user_page_frag_description);
		mWebsite = (TextView)view.findViewById(R.id.user_page_frag_website);
		mSettings = (ImageButton)view.findViewById(R.id.user_page_frag_settings);
		mViewPager = (ViewPager)view.findViewById(R.id.user_page_frag_pager);
		mTab = (PagerSlidingTabStrip)view.findViewById(R.id.user_page_frag_tab);
	}


	private void setButton(){
		mWebsite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String webSite = mWebsite.getText().toString();
				String webSiteRegx = "(http|https)://.*";
				if(!webSite.matches(webSiteRegx)){
					webSite = "http://" + webSite;
				}

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
				mActivity.startActivity(intent);
			}
		});

		mProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ItDialogFragment replyDialog = ProfileImageDialog.newInstance(mUser);
				replyDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});

		mSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, SettingsActivity.class);
				startActivityForResult(intent, SETTINGS);
			}
		});
	}


	private void setUser(final Object obj){
		mUser = mObjectPrefHelper.get(ItUser.class);
		if(mUserId.equals(mUser.getId())){
			AsyncChainer.notifyNext(obj);
		} else {
			mUserHelper.get(mUserId, new EntityCallback<ItUser>() {

				@Override
				public void onCompleted(ItUser user) {
					if(!isAdded()){
						return;
					}

					if(user != null){
						mUser = user;
						AsyncChainer.notifyNext(obj);
					} else {
						String message = getResources().getString(R.string.not_exist_user);
						ItAlertDialog notExistUserDialog = ItAlertDialog.newInstance(message, null, null, null, false, false);
						notExistUserDialog.setCallback(new DialogCallback() {

							@Override
							public void doPositive(Bundle bundle) {
								mActivity.finish();
							}
							@Override
							public void doNeutral(Bundle bundle) {
								// Do nothing
							}
							@Override
							public void doNegative(Bundle bundle) {
								// Do nothing
							}
						});
						notExistUserDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
						AsyncChainer.clearChain(obj);
					}
				}
			});
		}
	}


	public void updateProfile() {
		if(mActionBar != null) mActionBar.setTitle(mUser.getNickName());
		mNickName.setText(mUser.getNickName());
		mDescription.setText(mUser.getSelfIntro());
		mWebsite.setText(mUser.getWebPage());

		mDescription.setVisibility(!mUser.getSelfIntro().equals("") ? View.VISIBLE : View.GONE);
		mWebsite.setVisibility(!mUser.getWebPage().equals("") ? View.VISIBLE : View.GONE);
		mSettings.setVisibility(mUser.checkMe() ? View.VISIBLE : View.GONE);
	}


	private void setProfileImage(){
		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(mUser.getId()))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(mProfileImage);
	}


	private void setViewPager(){
		mViewPagerAdapter = new UserPagePagerAdapter(getChildFragmentManager(), mActivity, mUser, 
				mHeader.getHeight(), mTab.getHeight());
		mViewPagerAdapter.setScrollTabHolder(new UserPageScrollTabHolder() {

			@Override
			public void onScroll(RecyclerView view, RecyclerView.LayoutManager layoutManager, int pagePosition) {
				if (mViewPager.getCurrentItem() == pagePosition) {
					// Scroll Header by current grid scroll y
					int scrollY = getGridScrollY(view, (GridLayoutManager)layoutManager);
					mHeader.scrollTo(0, Math.min(scrollY, mHeader.getHeight() - mTab.getHeight()));
				}
			}

			@Override
			public void updateTabNumber(int position, int number) {
				View tab = mTab.getTab(position);
				TextView numberText = (TextView)tab.findViewById(R.id.tab_user_page_number);
				numberText.setText(""+number);
			}

			@Override
			public void adjustScroll(int scrollHeight) {
			}
			@Override
			public void updateHeader(int headerHeight) {
			}
		});
		mViewPager.setAdapter(mViewPagerAdapter);
	}


	private void setTab(){
		mTab.setViewPager(mViewPager);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// Get scroll tab holder interface
				SparseArrayCompat<UserPageScrollTabHolder> scrollTabHolderList = mViewPagerAdapter.getScrollTabHolderList();
				UserPageScrollTabHolder fragmentContent = null;

				// Scroll grid view items of tab fragment
				int currentItem = mViewPager.getCurrentItem();
				if (positionOffsetPixels > 0) {
					if (position < currentItem) {
						// Revealed the previous page
						fragmentContent = scrollTabHolderList.valueAt(position);
					} else {
						// Revealed the next page
						fragmentContent = scrollTabHolderList.valueAt(position + 1);
					}

					fragmentContent.adjustScroll((int) (mHeader.getHeight() - mHeader.getScrollY()));
				}
			}
			@Override
			public void onPageSelected(int position) {
				// Scroll header by grid view items scroll y
				SparseArrayCompat<UserPageScrollTabHolder> scrollTabHolderList = mViewPagerAdapter.getScrollTabHolderList();
				UserPageScrollTabHolder fragmentContent = scrollTabHolderList.valueAt(position);
				fragmentContent.adjustScroll((int) (mHeader.getHeight() - mHeader.getScrollY()));
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}


	private void setTabName(){
		for(int i=0 ; i<mViewPagerAdapter.getCount() ; i++){
			View tab = mTab.getTab(i);
			TextView name = (TextView)tab.findViewById(R.id.tab_user_page_name);
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

		return -c.getTop() + (findFirstVisibleItemPosition/spanCount)*c.getHeight() + headerHeight;
	}
}
