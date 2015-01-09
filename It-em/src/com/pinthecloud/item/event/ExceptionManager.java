//package com.pinthecloud.item.event;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.pinthecloud.item.fragment.ItFragment;
//
//public class ExceptionManager {
//
//	private static Map<String, Handler> map = new HashMap<String, ExceptionManager.Handler>();
//
//	public static void setHandler(ItFragment frag) {
//		map.put(frag.getClass().getName(), frag);
//	}
//
//	public static void fireException(ItException ex) {
//		Class<?> clazz = null;
//		if (ex.fromWho() == null) {
//			clazz = ItFragment.class;
//		} else {
//			clazz = ex.fromWho().getClass();
//		}
//
//		Handler handler = map.get(clazz.getName());
//		if (handler != null){
//			handler.handleException(ex);
//		}
//	}
//
//	public static interface Handler {
//		public void handleException(ItException ex);
//	}
//}
