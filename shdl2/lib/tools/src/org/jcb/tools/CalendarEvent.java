
package org.jcb.tools;

import java.util.*;


public class CalendarEvent extends EventObject {

	private int day;
	private int month; // 0 -> 11
	private int year; // 2002, etc.

	public CalendarEvent(Object source, int type, int value) {
		super(source);
		switch (type) {
			case DAY_CHANGE: day = value; break;
			case MONTH_CHANGE: month = value; break;
			case YEAR_CHANGE: year  = value; break;
		}
	}

	public final static int DAY_CHANGE = 1;
	public final static int MONTH_CHANGE = 2;
	public final static int YEAR_CHANGE = 3;

	public int getSelectedDay() {
		return day;
	}

	public int getSelectedMonth() {
		return month;
	}

	public int getSelectedYear() {
		return year;
	}

}

