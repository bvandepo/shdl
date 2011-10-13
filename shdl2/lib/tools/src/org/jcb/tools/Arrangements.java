
package org.jcb.tools;

import org.jcb.tools.*;
import java.util.*;


public class Arrangements implements Iterator {

	private int n;
	private int N;

	private int[] indices;
	private boolean more = true;
	private ArrayList next;

 
	public Arrangements(int n, int N) {
		this.n = n;
		this.N = N;
		indices = new int[n];
		// Place dans le tableau <indices> le premier arrangement de <n> elements parmi <N>
		for (int i = 0; i < n; i++) indices [i] = i;
	}

	public boolean hasNext() {
		if (!more) return false;
		next = new ArrayList();
		for (int i = 0; i < n; i++) next.add(new Integer(indices[i]));
		more = nextArrangement(n, N, indices);
		return true;
	}

	public Object next() {
		return next;
	}

	public void remove() {
	}
 
 
	// <n> est le nombre d'indices, <N> est le nombre de positions possibles,
	// <indices> est l'arrangement courant. Calcule l'arrangement suivant, et
	// renvoie false s'il n'y en a plus
 
	private boolean nextArrangement(int n, int N, int[] indices) {
		int i, j, p;
 
		i = n - 1;
		while (i >= 0) {
			p = indices [i];
			if (p < N - n + i) {
				for (j = i; j < n; j++)
					indices [j] = ++p;
				return (true);
			} else i--;
		}
		return (false);
	}



	public static void main(String[] args) {

		Arrangements ar = new Arrangements(3, 5);
		while (ar.hasNext()) {
			ArrayList l = (ArrayList) ar.next();
			System.out.println(l);
		}
/*

		for (int i = 0; i <= 3; i++) {
			Arrangements a = new Arrangements(i, 3);
			while (a.hasNext()) {
				ArrayList l = (ArrayList) a.next();
				System.out.println(l);
			}
		}
*/
	}
}

