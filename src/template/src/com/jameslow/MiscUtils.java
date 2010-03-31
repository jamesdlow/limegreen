package com.jameslow;

public class MiscUtils {
	public static void wait (int n){
		long t0, t1;
		t0 =  System.currentTimeMillis();
		do{
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < n);
	}
	public static boolean match(String needle, String haystack) {
		String regex = " |,|\\.|;|:|\t";
		String[] needles = needle.toUpperCase().split(regex);
		haystack = haystack.toUpperCase();
		
		for (int i = 0; i < needles.length; i++ ) {
			if (!(haystack.indexOf(needles[i]) >= 0)) {
				return false;
			}
		}
		return true;
	}
}
