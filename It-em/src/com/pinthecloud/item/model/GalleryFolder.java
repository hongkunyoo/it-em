package com.pinthecloud.item.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class GalleryFolder implements Parcelable {
	
	public static String INTENT_KEY = "GALLERY_FOLDER_INTENT_KEY";
	
	private List<Gallery> galleryList;
	private String name;
	
	public GalleryFolder() {
		super();
	}
	public GalleryFolder(String name) {
		super();
		this.galleryList = new ArrayList<Gallery>();
		this.name = name;
	}
	
	public List<Gallery> getGalleryList() {
		return galleryList;
	}
	public void setGalleryList(List<Gallery> galleryList) {
		this.galleryList = galleryList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void readGalleryFolder(GalleryFolder galleryFolder) {
		this.setGalleryList(galleryFolder.getGalleryList());
		this.setName(galleryFolder.getName());
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
	/*
	 * Parcelable
	 */
	public static final Parcelable.Creator<GalleryFolder> CREATOR = new Creator<GalleryFolder>(){
		public GalleryFolder createFromParcel(Parcel in){
			return new GalleryFolder(in);
		}
		public GalleryFolder[] newArray(int size){
			return new GalleryFolder[size]; 
		}
	};

	public GalleryFolder(Parcel in){
		this();
		readToParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.toString());
	}

	public void readToParcel(Parcel in){
		this.readGalleryFolder(new Gson().fromJson(in.readString(), GalleryFolder.class));
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
