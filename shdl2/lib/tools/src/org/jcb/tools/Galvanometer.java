
package org.jcb.tools;
 
import org.jcb.tools.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;


public class Galvanometer extends JPanel {

	private int size;
	private String label;
	private FuzzyQuantity norm;

	private double liminf;
	private double limsup;
	private FuzzyQuantity value;
	private boolean printValue = false;

	private final static Color GREEN = new Color(92, 201, 121);
	private final static Color ORANGE = new Color(242, 181, 106);
	private final static Color RED = new Color(221, 46, 46);


	public Galvanometer(int size, String label) {
		this.size = size;
		this.label = label;
	}

	public String toString() {
		return ("label=" + label + ", norm=" + norm + ", value=" + value);
	}

	public Dimension getPreferredSize() {
		return new Dimension(2 * size, 2 * size);
	}
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public FuzzyQuantity getNorm() {
		return norm;
	}

	public void setNorm(FuzzyQuantity norm, double liminf, double limsup) {
		if (norm == null) return;
		this.norm = norm;
		this.liminf = liminf;
		this.limsup = limsup;
		repaint();
	}
	

	public void setValue(FuzzyQuantity value) {
		if (value.lessThan(liminf)) value = new FuzzyQuantity(liminf, 0.);
		if (value.greaterThan(limsup)) value = new FuzzyQuantity(limsup, 0.);
		this.value = value;
		repaint();
	}

	public void setPrintValue(boolean printValue) {
		this.printValue = printValue;
	}


	public void paint(Graphics g) {
		// paint background
		super.paint(g);

		// frame
		g.setColor(Color.black);
		g.drawRect(0, 0, 2 * size - 1, 2 * size - 1);

		g.setClip(1, 1, 2 * size - 2, 2 * size - 2);

		g.setFont(new Font("SansSerif", Font.BOLD, 10));

		if (norm != null) {
			g.setColor(RED);
			myFillArc(g, size, 150 * size / 100, 140 * size / 100, 30, 120);

			g.setColor(ORANGE);
			int anglSuppInf = valueToAngle(norm.getSupportInf());
			int anglSuppSup = valueToAngle(norm.getSupportSup());
			myFillArc(g, size, 150 * size / 100, 140 * size / 100, anglSuppInf, anglSuppSup - anglSuppInf);

			g.setColor(GREEN);
			int anglCoreInf = valueToAngle(norm.getCoreInf());
			int anglCoreSup = valueToAngle(norm.getCoreSup());
			myFillArc(g, size, 150 * size / 100, 140 * size / 100, anglCoreInf, anglCoreSup - anglCoreInf);

			g.setColor(getBackground());
			myFillArc(g, size, 150 * size / 100, 100 * size / 100, 0, 180);
		
			g.setColor(Color.black);
			//angl1 = valueToAngle(limsup);
			//angl2 = valueToAngle(liminf);
			int angl1 = 30;
			int angl2 = 150;
			myDrawArc(g, size, 150 * size / 100, 140 * size / 100, angl1, angl2 - angl1);
			myDrawArc(g, size, 150 * size / 100, 100 * size / 100, angl1, angl2 - angl1);
		
			if (value == null) return;

			if ((value.getSupportInf() > norm.getSupportSup()) ||
					(value.getSupportSup() < norm.getSupportInf())) {
				g.setColor(RED);
				g.fillRect(1, 150 * size / 100, 2 * size - 1, 48 * size / 100);
			} else if ((value.getSupportInf() >= norm.getCoreInf()) &&
					(value.getSupportSup() <= norm.getCoreSup())) {
				g.setColor(GREEN);
				g.fillRect(1, 150 * size / 100, 2 * size - 1, 48 * size / 100);
			} else {
				//g.setColor(GREEN);
				//g.fillRect(1, 150 * size / 100, 2 * size - 1, 25 * size / 100);
				//g.setColor(RED);
				//g.fillRect(1, 175 * size / 100, 2 * size - 1, 23 * size / 100);
				g.setColor(ORANGE);
				g.fillRect(1, 150 * size / 100, 2 * size - 1, 48 * size / 100);
			}
			g.setColor(Color.black);
			int width = g.getFontMetrics().stringWidth(label);
			g.drawString(label.trim(), size - width / 2, 190 * size / 100);
			if (printValue)
				g.drawString(value.format(), size - width / 2, 150 * size / 100);
		
			// support
			angl1 = Math.min(150, valueToAngle(value.getSupportInf()));
			angl2 = Math.max(30, valueToAngle(value.getSupportSup()));
			//g.setColor(Color.lightGray);
			//myFillArc(g, size, 150 * size / 100, 130 * size / 100, angl1, angl2 - angl1);
			g.setColor(Color.black);
			for (int angl = angl2; angl <= angl1; angl += 2) {
				myDrawPin(g, size, 150 * size / 100, angl, 0, 130 * size / 100);
			}

			// noyau
			angl1 = valueToAngle(value.getCoreInf());
			angl2 = valueToAngle(value.getCoreSup());
			g.setColor(Color.black);
			myFillArc(g, size, 150 * size / 100, 130 * size / 100, angl1, angl2 - angl1);
			//myDrawPin(g, size, 150 * size / 100, angl1, 0, 130 * size / 100);
			//myDrawPin(g, size, 150 * size / 100, angl2, 0, 130 * size / 100);
		} else {
			g.setColor(GREEN);
			int angl1 = 30;
			int angl2 = 150;
			myFillArc(g, size, 150 * size / 100, 140 * size / 100, angl1, angl2 - angl1);
			g.setColor(NiceColors.smog);
			myFillArc(g, size, 150 * size / 100, 100 * size / 100, angl1, angl2 - angl1);
		
			g.setColor(Color.black);
			myDrawArc(g, size, 150 * size / 100, 140 * size / 100, angl1, angl2 - angl1);
			myDrawArc(g, size, 150 * size / 100, 100 * size / 100, angl1, angl2 - angl1);
		
			g.setColor(GREEN);
			g.fillRect(1, 150 * size / 100, 2 * size - 1, 45 * size / 100);
			g.setColor(Color.black);
			int width = g.getFontMetrics().stringWidth(label);
			g.drawString(label.trim(), size - width / 2, 190 * size / 100);
			if (printValue)
				g.drawString(value.format(), size - width / 2, 150 * size / 100);
		
		}
		// paint '?' in the middle if value is unknown
		if (value.equals(FuzzyQuantity.UNKNOWN)) {
			g.setColor(Color.white);
			g.setFont(new Font("SansSerif", Font.BOLD, 48));
			g.drawString("?", size - 8, size + 5);
		}
	}

	private void myFillArc(Graphics g, int xcenter, int ycenter, int radius, int startangle, int drawangle) {
		g.fillArc(xcenter - radius, ycenter - radius, 2 * radius, 2 * radius, startangle, drawangle);
	}
	
	private void myDrawArc(Graphics g, int xcenter, int ycenter, int radius, int startangle, int drawangle) {
		g.drawArc(xcenter - radius, ycenter - radius, 2 * radius, 2 * radius, startangle, drawangle);
	}
	
	private void myDrawPin(Graphics g, int xcenter, int ycenter, int angle, int radius, int len) {
		//double rangle = (double) Math.toRadians(angle);
		double rangle = (double) (angle * Math.PI / 180.);
		double cosangle = Math.cos(rangle);
		double sinangle = Math.sin(rangle);
		int x1 = xcenter + ((int) (cosangle * ((double) radius)));
		int y1 = ycenter - ((int) (sinangle * ((double) radius)));
		int x2 = xcenter + ((int) (cosangle * ((double) (radius + len))));
		int y2 = ycenter - ((int) (sinangle * ((double) (radius + len))));
		g.drawLine(x1, y1, x2, y2);
	}
	
	// val = liminf -> 150 degrees
	// val = limsup -> 30 degrees
	private int valueToAngle(double val) {
		if (val == Double.NEGATIVE_INFINITY) return 150;
		if (val == Double.POSITIVE_INFINITY) return 30;
		int ang = (150 - ((int) ((120 * (val - liminf)) / (limsup - liminf))));
		ang = Math.max(ang, 30);
		ang = Math.min(ang, 150);
		return ang;
	}


	public static void main(String[] args) {
		JFrame f = new JFrame("Galvanometer Demo");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		Galvanometer indic1 = new Galvanometer(60, "ENERG_KCAL");
		indic1.setNorm(new FuzzyQuantity(800, 1100., 200., 200., 0.), 0., 2000.);
		indic1.setValue(new FuzzyQuantity(950., 0.1));
		Galvanometer indic5 = new Galvanometer(60, "CARBOHYDR");
		indic5.setNorm(new FuzzyQuantity(800, 1100., 200., 200., 0.), 0., 2000.);
		indic5.setValue(new FuzzyQuantity(1200., 0.));
		Galvanometer indic2 = new Galvanometer(60, "CALCIUM");
		indic2.setNorm(new FuzzyQuantity(1000., 2000., 100., 100., 0.), 0., 2000.);
		indic2.setValue(new FuzzyQuantity(1300, 2000., 100., 200., 0.));
		Galvanometer indic3 = new Galvanometer(60, "LIPID_SAT");
		indic3.setNorm(new FuzzyQuantity(0., 200., 100., 100., 0.), 0., 1000.);
		indic3.setValue(new FuzzyQuantity(650., 0.5));
		Galvanometer indic4 = new Galvanometer(60, "PROTEIN");
		indic4.setNorm(new FuzzyQuantity(1000., 1200., 100., 100., 0.), 0., 2000.);
		indic4.setValue(FuzzyQuantity.UNKNOWN);
		panel.add(indic1);
		panel.add(indic5);
		panel.add(indic2);
		panel.add(indic3);
		panel.add(indic4);
		Container cont = f.getContentPane();
		cont.add(panel);
		f.pack();
		f.setVisible(true);
	}

}
