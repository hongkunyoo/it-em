package com.pinthecloud.item.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtil {

	public static String getString() {
		return getString(getInt(25));
	}
	public static String getString(int num) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < num; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();
		return output;
	}
	public static int getInt() {
		return getInt(100);
	}
	public static int getInt(int seed) {
		return new Random().nextInt(seed);
	}
	public static float getFloat() {
		return new Random().nextFloat();
	}
	public static boolean getBoolean() {
		if (getInt() > 50) return true;
		return false;
	}
	public static double getDouble() {
		return new Random().nextDouble();
	}
	public static String getDate() {
		return "2014."+getInt(13)+"."+getInt(31);
	}
	public static String getTime() {
		return String.valueOf(getInt(24)) + ":" +getInt(60);
	}
	public static String getName() {
		return nameList.get(getInt(nameList.size()));
	}
	public static String getObjName() {
		return objList.get(getInt(objList.size()));
	}
	private static List<String> nameList = new ArrayList<String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("Bob");
			add("Tom");
			add("Noah");
			add("Liam");
			add("Emma");
			add("Olivia");
			add("Khaleesi");
			add("Asher");
			add("Ethan");
			add("Mia");
			add("Ava");
			add("Michael");
			add("Emily");
			add("Hazel");
			add("Violet");
			add("Seungmin");
			add("Milo");
			add("Jasper");
			add("Ezra");
			add("Jayden");
			add("Madison");
			add("Elizabeth");
			add("Charlotte");
			add("Henry");
		}
	};

	private static List<String> objList = new ArrayList<String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add("Apple");
			add("Banna");
			add("Cherry");
			add("Dog");
			add("Elephant");
			add("Flamingo");
			add("Guitar");
			add("Horse");
			add("IceCream");
			add("Jelly");
			add("Kellogg");
			add("Lemon");
			add("Monkey");
			add("North");
			add("Omega");
			add("Pig");
			add("Qeen");
			add("Rihno");
			add("Street");
			add("Tiger");
			add("Umbrella");
			add("Violet");
			add("Whale");
			add("Xylitol");
			add("Yogurt");
			add("Zoo");
		}
	};
}
