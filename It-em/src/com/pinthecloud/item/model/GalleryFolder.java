package com.pinthecloud.item.model;

import java.util.ArrayList;
import java.util.List;

public class GalleryFolder {
	
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
}
