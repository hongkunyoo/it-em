package com.pinthecloud.item.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;

public class AsyncChainer {
	
	private static int mCurrentCount = 0;
	private static int mCount = 0;
	
	private static final int NUM_OF_QUEUE = 16;
	private static Map<String, Queue<Chainable>> mMapQueue;
	static {
		mMapQueue = new HashMap<String, Queue<Chainable>>();
	}


	public static void asyncChain(ItFragment frag, Chainable...chains) {
		Class<?> clazz = null;
		if (frag == null) {
			clazz = ItFragment.class;
		} else {
			clazz = frag.getClass();
		}
		
		Queue<Chainable> queue = mMapQueue.get(clazz.getName());
		if (queue == null) {
			mMapQueue.put(clazz.getName(), new ArrayBlockingQueue<Chainable>(NUM_OF_QUEUE));
			queue = mMapQueue.get(clazz.getName());
		}
		
		for(Chainable c : chains) {
			queue.add(c);
		}
		
		AsyncChainer.notifyNext(frag);
	}


	public static void notifyNext(ItFragment frag, Object... params) {
	    if (++mCurrentCount < mCount) return;
		
		Class<?> clazz = null;
		if (frag == null) {
			clazz = ItFragment.class;
		} else {
			clazz = frag.getClass();
		}
		
		Queue<Chainable> queue = mMapQueue.get(clazz.getName());
		if (queue != null && !queue.isEmpty()) {
			Chainable c = queue.poll();
			if (c == null) throw new ItException("chain == null");
			c.doNext(frag, params);
		}
	}


	public static void clearChain(ItFragment frag) {
		Class<?> clazz = null;
		if (frag == null) {
			clazz = ItFragment.class;
		} else {
			clazz = frag.getClass();
		}
		
		Queue<Chainable> queue = mMapQueue.get(clazz.getName());
		queue.clear();
	}


	public static void waitChain(int count){
		mCurrentCount = 0;
		mCount = count;
	}
	
	
	public static interface Chainable {
		public void doNext(ItFragment frag, Object... params);
	}
}
