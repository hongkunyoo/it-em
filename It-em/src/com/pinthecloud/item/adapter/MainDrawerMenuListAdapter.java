package com.pinthecloud.item.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.fragment.MainDrawerFragment;
import com.pinthecloud.item.fragment.MainDrawerFragment.MainDrawerMenu;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ImageUtil;
import com.pinthecloud.item.view.CircleImageView;

public class MainDrawerMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		PROFILE,
		NORMAL
	}

	private ItApplication mApp;
	private ItFragment mfrag; 
	private List<MainDrawerMenu> mMenuList;


	public MainDrawerMenuListAdapter(ItFragment frag, List<MainDrawerMenu> menuList) {
		this.mApp = ItApplication.getInstance();
		this.mfrag = frag;
		this.mMenuList = menuList;
	}


	private static class ProfileViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public CircleImageView profileImage;
		public TextView nickName;

		public ProfileViewHolder(View view) {
			super(view);
			this.view = view;
			this.profileImage = (CircleImageView)view.findViewById(R.id.row_home_drawer_menu_list_profile_image);
			this.nickName = (TextView)view.findViewById(R.id.row_home_drawer_menu_list_profile_nick_name);
		}
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView menuImage;
		public TextView menuName;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.menuImage = (ImageView)view.findViewById(R.id.row_home_drawer_menu_list_menu_image);
			this.menuName = (TextView)view.findViewById(R.id.row_home_drawer_menu_list_menu_name);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.PROFILE.ordinal()){
			view = inflater.inflate(R.layout.row_main_drawer_menu_list_profile, parent, false);
			viewHolder = new ProfileViewHolder(view);
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_main_drawer_menu_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		MainDrawerMenu menu = mMenuList.get(position);
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.PROFILE.ordinal()){
			ProfileViewHolder profileViewHolder = (ProfileViewHolder)holder;
			setProfileViewHolder(profileViewHolder, position);
		} else if (viewType == VIEW_TYPE.NORMAL.ordinal()){
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalViewHolder(normalViewHolder, menu);
		}
	}


	@Override
	public int getItemCount() {
		return mMenuList.size();
	}


	@Override
	public int getItemViewType(int position) {
		if(position == 0){
			return VIEW_TYPE.PROFILE.ordinal();
		} else {
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setProfileViewHolder(ProfileViewHolder holder, final int position) {
		ItUser myItUser = mApp.getObjectPrefHelper().get(ItUser.class);

		mApp.getPicasso()
		.load(BlobStorageHelper.getUserProfileImgUrl(myItUser.getId()+ImageUtil.PROFILE_THUMBNAIL_IMAGE_POSTFIX))
		.fit()
		.into(holder.profileImage);

		holder.nickName.setText(myItUser.getNickName());
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainDrawerFragment)mfrag).selectMenu(position);
			}
		});
	}


	private void setNormalViewHolder(NormalViewHolder holder, final MainDrawerMenu menu) {
		holder.view.setActivated(menu.isActivated());
		holder.menuImage.setImageResource(menu.getMenuImage());
		holder.menuName.setText(menu.getMenuName());

		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainDrawerFragment)mfrag).selectMenu(mMenuList.indexOf(menu));
			}
		});
	}
}
