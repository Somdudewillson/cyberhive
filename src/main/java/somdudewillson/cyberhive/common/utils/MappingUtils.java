package somdudewillson.cyberhive.common.utils;

import java.util.HashMap;

public class MappingUtils {
	public static <T> HashMap<T,Integer> arrayToInverseMap(T[] array) {
		HashMap<T,Integer> result = new HashMap<T,Integer>();
		for (int i=0;i<array.length;i++) {result.put(array[i], i);}
		return result;
	}
}
