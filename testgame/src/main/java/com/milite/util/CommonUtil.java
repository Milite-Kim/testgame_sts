package com.milite.util;

public class CommonUtil {
	public static int Dice(int n) {
		int r = (int) Math.random() * n + 1;
		return r;
	}
}
