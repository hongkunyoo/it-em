package com.pinthecloud.item.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Gallery implements Parcelable {
	
	public static String INTENT_KEY = "GALLERY_INTENT_KEY";
	
	private String path;
	private boolean isSeleted = false;
	
	public Gallery() {
		super();
	}
	public Gallery(String path) {
		super();
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isSeleted() {
		return isSeleted;
	}
	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}
	public void readGallery(Gallery gallery) {
		this.setPath(gallery.getPath());
		this.setSeleted(gallery.isSeleted);
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<Gallery> CREATOR = new Creator<Gallery>(){
		public Gallery createFromParcel(Parcel in){
			return new Gallery(in);
		}
		public Gallery[] newArray(int size){
			return new Gallery[size]; 
		}
	};

	public Gallery(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readGallery(new Gson().fromJson(in.readString(), Gallery.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
