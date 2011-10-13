
package org.jcb.tools;

import java.util.*;


public interface CalendarListener extends EventListener {

	public void monthChanged(CalendarEvent ev);
	public void yearChanged(CalendarEvent ev);
	public void dayChanged(CalendarEvent ev);
}

