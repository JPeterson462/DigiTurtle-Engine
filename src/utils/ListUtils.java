package utils;

import java.util.ArrayList;

public class ListUtils {
	
	public static float[] toIntegerArray(ArrayList<Float> list) {
		float[] array = new float[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static boolean[] toBooleanArray(ArrayList<Boolean> list) {
		boolean[] array = new boolean[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}
