
package org.jcb.tools;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class JCalendar extends JPanel implements ActionListener, MouseListener {

	private Locale locale;
	private FontMetrics fontMetrics;
	private ResourceBundle labels;
	private JComboBox monthComboBox;
	private JComboBox yearComboBox;
	private DayNumbersPanel dayNumbersPanel;
	private ArrayList listeners = new ArrayList();

	GregorianCalendar cal;
	private int day; // 1 -> length of month
	private int month; // 0 -> 11
	private int year; // 2002, etc.

	private int firstIndex; // index in dayNumbersPanel of the 1st of current month;
	private int firstDayOfWeek; // 0 in US, 1 in France
	private int rowCount; // 5 or 6

	private int w = 200;
	private int h = 200;


	// create calendar centered on current date

	public JCalendar(Locale locale, FontMetrics fontMetrics) {
		this.locale = locale;
		this.fontMetrics = fontMetrics;
		labels = ResourceBundle.getBundle("org.jcb.tools.CalendarResourceBundle", locale);
		cal = new GregorianCalendar(locale);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		updateFirstIndex();
		day = cal.get(Calendar.DAY_OF_MONTH);
		init(labels);
	}

	// create calendar centered on <date>

	public JCalendar(Locale locale, FontMetrics fontMetrics, int year, int month, int day) {
		this.locale = locale;
		this.fontMetrics = fontMetrics;
		labels = ResourceBundle.getBundle("org.jcb.tools.CalendarResourceBundle", locale);
		cal = new GregorianCalendar(locale);
		cal.set(year, month, day);
		this.year = year;
		this.month = month;
		this.day = day;
		init(labels);
	}


	// registers a new calendar event listener

	public void addCalendarListener(CalendarListener listener) {
		listeners.add(listener);
	}

	// get current year (2002, etc.)
	public int getYear() {
		return year;
	}

	// get current month (0 -> 11)
	public int getMonth() {
		return month;
	}

	// get current day
	public int getDay() {
		return day;
	}

	// return "ddmmyyyy"
	public String getDate() {
		String dd = "" + day;
		if (day <= 9) dd = "0" + dd;
		String mm = "" + (month + 1);
		if (month + 1 <= 9) mm = "0" + mm;
		String yyyy = "" + year;
		return (dd + mm + yyyy);
	}

	public Dimension getMinimumSize() {
		return new Dimension(150, 150);
	}

	public Dimension getMaximumSize() {
		return new Dimension(400, 400);
	}

	public Dimension getPreferredSize() {
		return new Dimension(w, h);
	}

	public void setNewSize(int w, int h) {
		this.w = w;
		this.h = h;
	}


	void init(ResourceBundle labels) {
		updateFirstIndex();

		setLayout(new BorderLayout());

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 0));
		monthComboBox = new JComboBox();
		monthComboBox.setFont(fontMetrics.getFont());
		monthComboBox.setMaximumRowCount(12);
		for (int i = 0; i < 12; i++) {
			monthComboBox.addItem(labels.getString("month" + (i + 1)));
		}
		monthComboBox.setSelectedIndex(month);
		monthComboBox.addActionListener(this);
		yearComboBox = new JComboBox();
		yearComboBox.setFont(fontMetrics.getFont());
		for (int i = 0; i < 8; i++) {
			yearComboBox.addItem("" + (year + i - 4));
		}
		yearComboBox.setSelectedIndex(4);
		yearComboBox.addActionListener(this);
		buttonsPanel.add(monthComboBox);
		buttonsPanel.add(yearComboBox);

		JPanel daysPanel = new JPanel(new BorderLayout());
		JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
		for (int i = 0; i < 7; i++) {
			JLabel label = new JLabel(labels.getString("weekDay" + i));
			label.setFont(fontMetrics.getFont());
			weekDaysPanel.add(label);
		}

		dayNumbersPanel = new DayNumbersPanel();
		dayNumbersPanel.addMouseListener(this);
		weekDaysPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		daysPanel.add("North", weekDaysPanel);
		daysPanel.add("Center", dayNumbersPanel);

		add("North", buttonsPanel);
		add("Center", daysPanel);
	}

	class DayNumbersPanel extends JPanel {

		public void paint(Graphics g) {
			g.setFont(fontMetrics.getFont());
			Color fg = getForeground();
			int dx = dayNumbersPanel.getWidth() / 7;
			int dy = dayNumbersPanel.getHeight() / rowCount;
			int index = 0;
			for (int i = 0; i < firstIndex - 1; i++) {
				index += 1;
			}
			for (int i = 0; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				int iy = index / 7;
				int ix = index - iy * 7;
				// current day
				if (i + 1 == day) g.drawRect(ix * dx, iy * dy, dx, dy);
				if ((index % 7 == (6 - firstDayOfWeek) % 7) || (index % 7 == (7 - firstDayOfWeek) % 7)) g.setColor(Color.red);
				String s = "" + (i + 1);
				int ws = fontMetrics.stringWidth(s);
				int hs = fontMetrics.getHeight();
				int wgap = (dx - ws) / 2;
				int hgap = (dy - hs) / 2;
				g.drawString(s, ix * dx + wgap, iy * dy + dy - hgap);
				g.setColor(fg);
				index += 1;
			}
		}
	}

	void updateFirstIndex() {
		GregorianCalendar gcal = new GregorianCalendar(year, month, 1);
		firstIndex = gcal.get(Calendar.DAY_OF_WEEK);
		firstDayOfWeek = Integer.parseInt(labels.getString("first-day-of-week"));
		firstIndex -= firstDayOfWeek;
		if (firstIndex <= 0) firstIndex += 7;
		rowCount = 5;
		if (firstIndex >= 6) rowCount = 6;
	}


	public void actionPerformed(ActionEvent ev) {
		Object source = ev.getSource();

		if (source == monthComboBox) {
			month = monthComboBox.getSelectedIndex();
			day = 1;
			cal.set(year, month, day);
			for (int i = 0; i < listeners.size(); i++) {
				CalendarListener listener = (CalendarListener) listeners.get(i);
				listener.monthChanged(new CalendarEvent(this, CalendarEvent.MONTH_CHANGE, month));
				listener.dayChanged(new CalendarEvent(this, CalendarEvent.DAY_CHANGE, day));
			}
			updateFirstIndex();
		} else if (source == yearComboBox) {
			year = yearComboBox.getSelectedIndex() + year - 4;
			month = 0;
			day = 1;
			cal.set(year, month, day);
			for (int i = 0; i < listeners.size(); i++) {
				CalendarListener listener = (CalendarListener) listeners.get(i);
				listener.yearChanged(new CalendarEvent(this, CalendarEvent.YEAR_CHANGE, year));
				listener.monthChanged(new CalendarEvent(this, CalendarEvent.MONTH_CHANGE, month));
				listener.dayChanged(new CalendarEvent(this, CalendarEvent.DAY_CHANGE, day));
			}
			yearComboBox.removeAllItems();
			for (int i = 0; i < 8; i++) {
				yearComboBox.addItem("" + (year + i - 4));
			}
			yearComboBox.setSelectedIndex(4);
			monthComboBox.setSelectedIndex(0);
		}
		repaint();
	}

	public void mouseClicked(MouseEvent ev) {
	}
	public void mouseEntered(MouseEvent ev) {
	}
	public void mouseExited(MouseEvent ev) {
	}
	public void mousePressed(MouseEvent ev) {
	}
	public void mouseReleased(MouseEvent ev) {
		int width = dayNumbersPanel.getWidth();
		int height = dayNumbersPanel.getHeight();
		int co = (7 * ev.getX()) / width;
		int li = (rowCount * ev.getY()) / height;
		int index = li * 7 + co;
		// set <day>
		day = Math.max(1, index - firstIndex + 2);
		cal.set(year, month, 1);
		day = Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(year, month, day);
		for (int i = 0; i < listeners.size(); i++) {
			CalendarListener listener = (CalendarListener) listeners.get(i);
			listener.dayChanged(new CalendarEvent(this, CalendarEvent.DAY_CHANGE, day));
		}
		repaint();
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("JCalendar");
		Locale locale = Locale.FRANCE;
		//Locale locale = Locale.US;
		Font defaultFont = new Font("SansSerif", Font.BOLD, 18);
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(defaultFont);
		JCalendar cal = null;
		if (args.length > 0) {
			int day = Integer.parseInt(args[0]);
			int month = Integer.parseInt(args[1]);
			int year = Integer.parseInt(args[2]);
			cal = new JCalendar(locale, fm, year, month, day);
		} else {
			cal = new JCalendar(locale, fm);
		}
		cal.addCalendarListener(new MyCalendarListener());
		frame.setContentPane(cal);
		frame.pack();
		frame.setVisible(true);
	}
}


class MyCalendarListener implements CalendarListener {
	public void dayChanged(CalendarEvent ev) {
		System.out.println("day changed: " + ev.getSelectedDay());
		JCalendar cal = (JCalendar) ev.getSource();
		System.out.println("year=" + cal.getYear() + ", month=" + cal.getMonth() + ", day=" + cal.getDay());
	}
	public void monthChanged(CalendarEvent ev) {
		System.out.println("month changed: " + ev.getSelectedMonth());
		JCalendar cal = (JCalendar) ev.getSource();
		System.out.println("year=" + cal.getYear() + ", month=" + cal.getMonth() + ", day=" + cal.getDay());
	}
	public void yearChanged(CalendarEvent ev) {
		System.out.println("year changed: " + ev.getSelectedYear());
		JCalendar cal = (JCalendar) ev.getSource();
		System.out.println("year=" + cal.getYear() + ", month=" + cal.getMonth() + ", day=" + cal.getDay());
	}
}

