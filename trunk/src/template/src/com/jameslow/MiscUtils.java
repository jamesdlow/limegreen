package com.jameslow;

import java.util.Calendar;

public class MiscUtils {
	public static void wait (int n){
		long t0, t1;
		t0 =  System.currentTimeMillis();
		do{
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < n);
	}
	public static boolean isblank(String s) {
		return (s == null || "".compareTo(s) == 0);
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
	public static double excelDate(Calendar now) {
	    Calendar cal1900 = Calendar.getInstance();
	    cal1900.set(1899, 12, 31);
	    long timenow = now.getTimeInMillis();
	    long time1900 = cal1900.getTimeInMillis();
	    float diff = timenow - time1900;
	    //TODO: 33 magic number to make dates work correctly, means this function is only accurate for current dates
	    return 33 + (diff / (float) (24 * 60 * 60 * 1000));
	}
}