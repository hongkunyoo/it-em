package com.pinthecloud.item.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;

public class ViewUtil {


	@SuppressWarnings("unchecked")
	public static void setListHeightBasedOnChildren(final RecyclerView recyclerView, final int rowCount) {
		@SuppressWarnings("rawtypes")
		RecyclerView.Adapter adapter = recyclerView.getAdapter();
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), MeasureSpec.AT_MOST);

		for (int i=0 ; i<rowCount ; i++) {
			if(layoutManager.findViewByPosition(i) != null){
				totalHeight += layoutManager.findViewByPosition(i).getHeight();	
			} else {
				RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i));
				adapter.onBindViewHolder(holder, i);

				holder.itemView.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
				totalHeight += holder.itemView.getMeasuredHeight();
			}
		}

		recyclerView.getLayoutParams().height = totalHeight;
		recyclerView.requestLayout();
	}

	public static int getStatusBarHeight(Context context) {
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		return context.getResources().getDimensionPixelSize(resourceId);
	}
	
	public static int getActionBarHeight(Context context) {
		TypedValue tv = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
		return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
	}

	public static int getDeviceWidth(ItActivity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}


	public static int getDeviceHeight(ItActivity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}
	

	public static void recycleImageView(ImageView imageView){
		Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		if(bitmap != null){
			bitmap.recycle();
			imageView.setImageBitmap(null);
		}
	}

	public static void hideKeyboard(ItActivity activity) {   
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
