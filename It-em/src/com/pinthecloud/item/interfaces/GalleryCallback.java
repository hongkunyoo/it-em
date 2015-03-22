package com.pinthecloud.item.interfaces;

import com.pinthecloud.item.model.Gallery;
import com.pinthecloud.item.model.GalleryFolder;

public interface GalleryCallback {
	public void clickGallery(Gallery gallery);
	public void clickFolder(GalleryFolder folder);
}
