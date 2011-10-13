package org.jcb.widgets;

import java.awt.*;
import javax.swing.border.*;


public class CurvedBorder extends AbstractBorder {
	
	private int x = 10;
	private Color color = Color.lightGray;
	
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		g.setColor(color);
		g.drawRoundRect(x, y, w-1, h-1, 8, 8);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Insets getBorderInsets(Component c) {
		return new Insets(x, x, x, x);
	}
	
	public Insets getBorderInsets(Component c, Insets i) {
		i.left = i.right = i.bottom = i.top = x;
		return i;
	}
	
	public boolean isBorderOpaque() {
		return true;
	}
}
