package org.jcb.tools;
 
import org.jcb.tools.*;
import java.util.*;


public class Tools {

	public static void printArray(Object[] objs) {
		System.out.print("(");
		for (int i = 0; i < objs.length; i++) {
			if (i > 0) System.out.print(",");
			System.out.print(objs[i]);
		}
		System.out.print(")");
	}

	public static void printIntArray(int[] ints) {
		System.out.print("(");
		for (int i = 0; i < ints.length; i++) {
			if (i > 0) System.out.print(",");
			System.out.print(ints[i]);
		}
		System.out.print(")");
	}

	public static void printIntArray(double[] ints) {
		System.out.print("(");
		for (int i = 0; i < ints.length; i++) {
			if (i > 0) System.out.print(",");
			System.out.print(ints[i]);
		}
		System.out.print(")");
	}

}

