
package org.jcb.tools;

import org.jcb.tools.*;
import java.util.*;


// implements equivalence classes using chains of elements

public class EquivalenceClass {

	private HashMap map;

	public EquivalenceClass() {
		map = new HashMap();
	}


	public void setEquivalent(Object obj1, Object obj2) {
		Object o1 = map.get(obj1);
		Object o2 = map.get(obj2);

		if ((o1 == null) && (o2 == null)) {
			// create a new chain
			map.put(obj1, obj2);
			map.put(obj2, obj1);

		} else if ((o1 != null) && (o2 != null)) {
			// o1 != null && o2 != null
			// melt two previously unrelated chains
			map.put(obj1, o2);
			map.put(obj2, o1);
 
		} else if (o1 == null) {
			// o1 == null && o2 != null
			// add <obj1> to the chain <obj2> belongs to
			map.put(obj2, obj1);
			map.put(obj1, o2);

		} else {
			// o1 != null && o2 == null
			// add <obj2> to the chain <obj1> belongs to
			map.put(obj1, obj2);
			map.put(obj2, o1);
		}
	}

	public Iterator getEquivalentsIterator(Object obj) {
		return new EquivalentIterator(obj);
	}



	private class EquivalentIterator implements Iterator {

		private Object obj;
		private Object curr;

		public EquivalentIterator(Object obj) {
			this.obj = obj;
			curr = obj;
		}

		public boolean hasNext() {
			curr = map.get(curr);
			if ((curr == null) || (curr == obj)) return false;
			return true;
		}

		public Object next() {
			return curr;
		}

		public void remove() {
		}
	}


	public static void main(String[] args) {

		EquivalenceClass ec = new EquivalenceClass();
		String s1 = "111";
		String s2 = "222";
		String s3 = "333";
		String s4 = "444";
		String s5 = "555";

		System.out.println(s1 + " declared equivalent to " + s2);
		ec.setEquivalent(s1, s2);
		System.out.println(s1 + " declared equivalent to " + s3);
		ec.setEquivalent(s1, s3);

		System.out.print("Equivalents to " + s2 + ": ");
		Iterator it = ec.getEquivalentsIterator(s2);
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println("");

		System.out.print("Equivalents to " + s3 + ": ");
		it = ec.getEquivalentsIterator(s3);
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println("");

		System.out.print("Equivalents to " + s4 + ": ");
		it = ec.getEquivalentsIterator(s4);
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println("");

		System.out.println(s4 + " declared equivalent to " + s5);
		ec.setEquivalent(s4, s5);

		System.out.print("Equivalents to " + s4 + ": ");
		it = ec.getEquivalentsIterator(s4);
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println("");

		System.out.println(s2 + " declared equivalent to " + s4);
		ec.setEquivalent(s2, s4);

		System.out.print("Equivalents to " + s4 + ": ");
		it = ec.getEquivalentsIterator(s4);
		while (it.hasNext())
			System.out.print(it.next() + " ");
		System.out.println("");
	}
}

