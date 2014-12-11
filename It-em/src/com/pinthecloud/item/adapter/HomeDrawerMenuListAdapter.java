package com.pinthecloud.item.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import com.pinthecloud.item.activity.ItUserPageActivity;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.BitmapUtil;
import com.pinthecloud.item.view.CircleImageView;
import com.squareup.picasso.Picasso;

public class HomeDrawerMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		PROFILE,
		Header,
		NORMAL
	}

	private Context mContext;
	private List<HomeDrawerMenu> mMenuList;


	public HomeDrawerMenuListAdapter(Context context) {
		this.mContext = context;

		String[] menuList = context.getResources().getStringArray(R.array.home_drawer_menu_string_array);
		this.mMenuList = new ArrayList<HomeDrawerMenu>();
		mMenuList.add(new HomeDrawerMenu(0, ""));
		mMenuList.add(new HomeDrawerMenu(R.drawable.launcher, menuList[0]));
		mMenuList.add(new HomeDrawerMenu(0, menuList[1]));
		mMenuList.add(new HomeDrawerMenu(R.drawable.launcher, menuList[2]));
		mMenuList.add(new HomeDrawerMenu(0, menuList[3]));
		mMenuList.add(new HomeDrawerMenu(R.drawable.launcher, menuList[4]));
		mMenuList.add(new HomeDrawerMenu(R.drawable.launcher, menuList[5]));
	}


	public static class ProfileViewHolder extends RecyclerView.ViewHolder {
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


	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView headerText;

		public HeaderViewHolder(View view) {
			super(view);
			this.view = view;
			this.headerText = (TextView)view.findViewById(R.id.row_home_drawer_menu_list_header_text);
		}
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
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
			view = inflater.inflate(R.layout.row_home_drawer_menu_list_profile, parent, false);
			viewHolder = new ProfileViewHolder(view);
		} else if(viewType == VIEW_TYPE.Header.ordinal()){
			view = inflater.inflate(R.layout.row_home_drawer_menu_list_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_home_drawer_menu_list, parent, false);
			viewHolder = new NormalViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		HomeDrawerMenu menu = mMenuList.get(position);
		int viewType = getItemViewType(position);

		if(viewType == VIEW_TYPE.PROFILE.ordinal()){

			ProfileViewHolder profileViewHolder = (ProfileViewHolder)holder;
			ItUser myItUser = ItApplication.getInstance().getObjectPrefHelper().get(ItUser.class);
			setProfileViewHolder(profileViewHolder, myItUser);

		} else if (viewType == VIEW_TYPE.Header.ordinal()){

			HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
			setHeaderViewHolder(headerViewHolder, menu);

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
		HomeDrawerMenu menu = mMenuList.get(position);
		if(position == 0){
			return VIEW_TYPE.PROFILE.ordinal();
		} else if (menu.getMenuImage() == 0){
			return VIEW_TYPE.Header.ordinal();
		} else {
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setProfileViewHolder(ProfileViewHolder holder, final ItUser myItUser) {
		holder.nickName.setText(myItUser.getNickName());

		Picasso.with(holder.profileImage.getContext())
		.load(BlobStorageHelper.getUserProfileImgUrl(myItUser.getId()+BitmapUtil.SMALL_POSTFIX))
		.placeholder(R.drawable.launcher)
		.fit()
		.into(holder.profileImage);

		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ItUserPageActivity.class);
				intent.putExtra(ItUser.INTENT_KEY, myItUser.getId());
				mContext.startActivity(intent);
			}
		});
	}


	private void setHeaderViewHolder(HeaderViewHolder holder, HomeDrawerMenu menu) {
		holder.headerText.setText(menu.getMenuName());
	}


	private void setNormalViewHolder(final NormalViewHolder holder, HomeDrawerMenu menu) {
		holder.menuImage.setImageResource(menu.getMenuImage());
		holder.menuName.setText(menu.getMenuName());
		
		holder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
	}


	private class HomeDrawerMenu {
		private int menuImage;
		private String menuName;

		public int getMenuImage() {
			return menuImage;
		}
		public String getMenuName() {
			return menuName;
		}

		public HomeDrawerMenu(int menuImage, String menuName) {
			super();
			this.menuImage = menuImage;
			this.menuName = menuName;
		}
	}
}
