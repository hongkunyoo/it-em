package com.pinthecloud.item.interfaces;

import java.util.List;

public interface ItListCallback<E> {
	public void onCompleted(List<E> list, int count);
}
