package com.pinthecloud.item.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.pinthecloud.item.R;

public class ProductTag extends AbstractItemModel<ProductTag> implements Parcelable {

	public static String INTENT_KEY = "PRODUCT_TAG_INTENT_KEY";

	private int category;
	private String shopName;
	private String webPage;
	private double price;

	public ProductTag() {
		super();
	}

	public int getCategory() {
		return category;
	}
	public String categoryString(Resources resources){
		String[] categoryArray = resources.getStringArray(R.array.category_string_array);
		return categoryArray[category];
	}
	public void setCategory(int category) {
		this.category = category;
	}
	
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getWebPage() {
		return webPage;
	}
	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public void readProductTag(ProductTag tag) {
		this.setId(tag.getId());
		this.setContent(tag.getContent());
		this.setWhoMade(tag.getWhoMade());
		this.setWhoMadeId(tag.getWhoMadeId());
		this.setRawCreateDateTime(tag.getRawCreateDateTime());
		this.setRefId(tag.getRefId());
		this.setShopName(tag.getShopName());
		this.setWebPage(tag.getWebPage());
		this.setPrice(tag.getPrice());
	}


	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<ProductTag> CREATOR = new Creator<ProductTag>(){
		public ProductTag createFromParcel(Parcel in){
			return new ProductTag(in);
		}
		public ProductTag[] newArray(int size){
			return new ProductTag[size]; 
		}
	};

	public ProductTag(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readProductTag(new Gson().fromJson(in.readString(), ProductTag.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
