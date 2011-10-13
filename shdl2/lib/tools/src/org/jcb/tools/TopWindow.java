
package org.jcb.tools;
 
import org.jcb.tools.*;

import java.awt.*;


public class TopWindow {

	public static Window getRootWindow(Component component) {
		Component last;
		do {
			last = component;
			component = component.getParent();
		} while (component != null);
		if (last instanceof Window) return (Window) last;
		return null;
	}

	public static Window getParentWindow(Component component) {
		do {
			component = component.getParent();
		} while (! (component instanceof Window));
		return ((Window) component);
	}
}
