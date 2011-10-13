
package org.jcb.tools;

import org.jcb.tools.*;

import java.util.*;


public class Attributs extends HashMap {

	// ex: new Attributs("size=10 height=20 title=its_me")
	public Attributs(String str) {
		super();
		StringTokenizer st = new StringTokenizer(str);
		while (st.hasMoreTokens()) {
			String key_value = st.nextToken();
			StringTokenizer st1 = new StringTokenizer(key_value, "=");
			String key = st1.nextToken();
			String value = st1.nextToken();
			put(key, value);
		}
	}

	public String toString() {
		StringBuffer res = new StringBuffer();
		Iterator it = keySet().iterator();
		int nb = 0;
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) get(key);
			if (nb++ > 0) res.append(" ");
			res.append(key + "=" + value);
		}
		return new String(res);
	}

	public String get(String key) {
		return ((String) super.get(key));
	}

	public void put(String key, String value) {
		super.put(key, value);
	}

}

