package org.jcb.tools;
 
import org.jcb.tools.*;
import java.util.*;


public class MyArrays {

	public static String toString(Object[] objs) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < objs.length; i++) {
			if (i > 0) sb.append(",");
			sb.append(objs[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	public static String toString(int[] ints) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < ints.length; i++) {
			if (i > 0) sb.append(",");
			sb.append(ints[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	public static int compareTo(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] < b[i]) return -1;
			if (a[i] > b[i]) return 1;
		}
		return 0;
	}

}

