package com.pinthecloud.item.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class AsyncChainer {

	private static int mCurrentCount = 0;
	private static int mCount = 0;

	private static final int NUM_OF_QUEUE = 16;
	private static Map<Object, Queue<Chainable>> mMapQueue;
	static {
		mMapQueue = new HashMap<Object, Queue<Chainable>>();
	}


	public static void asyncChain(Object obj, Chainable...chains) {
		Queue<Chainable> queue = mMapQueue.get(obj);
		if (queue == null) {
			mMapQueue.put(obj, new ArrayBlockingQueue<Chainable>(NUM_OF_QUEUE));
			queue = mMapQueue.get(obj);
		}
		queue.addAll(Arrays.asList(chains));
		AsyncChainer.notifyNext(obj);
	}


	public static void notifyNext(Object obj, Object... params) {
		if (++mCurrentCount < mCount) return;
		Queue<Chainable> queue = mMapQueue.get(obj);
		if (queue != null && !queue.isEmpty()) {
			Chainable c = queue.poll();
			c.doNext(obj, params);
		}
	}


	public static void clearChain(Object obj) {
		mCount = 0;
		Queue<Chainable> queue = mMapQueue.get(obj);
		queue.clear();
	}


	public static void waitChain(int count){
		mCurrentCount = 0;
		mCount = count;
	}


	public static interface Chainable {
		public void doNext(Object obj, Object... params);
	}
}
