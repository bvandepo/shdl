
package org.jcb.tools;

import org.jcb.tools.*;
import java.util.*;


public class Enumerations implements Iterator {

	private int n;
	private int[] nbs;

	private int[] indices;
	private boolean more = true;
	private ArrayList next;

 
	public Enumerations(int n, int nbs[]) {
		this.n = n;
		this.nbs = nbs;
		indices = new int[n];
		// Place dans le tableau <indices> la premiere enumeration
		for (int i = 0; i < n; i++) {
			if (nbs[i] == 0) more = false;
			indices [i] = 0;
		}
	}

	public boolean hasNext() {
		if (!more) return false;
		next = new ArrayList();
		for (int i = 0; i < n; i++) next.add(new Integer(indices[i]));
		more = nextEnumeration(n, nbs, indices);
		return true;
	}

	public Object next() {
		return next;
	}

	public void remove() {
	}
 
 
	// <n> est le nombre d'indices, <nbs> donne le nombre de chiffres par position
	// <indices> est l'enumeration courante. Calcule l'enumeration suivante, et
	// renvoie false s'il n'y en a plus
 
	private boolean nextEnumeration(int n, int[] nbs, int[] indices) {
		for (int i = n - 1; i >= 0; i--) {
			if (indices[i] < nbs[i] - 1) {
				indices[i] += 1;
				break;
			}
			if (i == 0) return false;
			indices[i] = 0;
		}
		return true;
	}



	public static void main(String[] args) {

		Enumerations en = new Enumerations(3, new int[] {3, 1, 4});
		while (en.hasNext()) {
			ArrayList l = (ArrayList) en.next();
			System.out.println(l);
		}
	}
}

