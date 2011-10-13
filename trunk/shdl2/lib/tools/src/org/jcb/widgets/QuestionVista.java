package org.jcb.widgets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;



public class QuestionVista extends JPanel {
	
	private QuestionItem[] items;
	private JTextArea ta;
	private JPanel itemsPanel;
	private ImageIcon arrowEnabled = new ImageIcon("resources/icons/greenArrow.png");
	private ImageIcon arrowDisabled = new ImageIcon("resources/icons/greenArrowDisabled.png");
	private Vector listeners = new Vector();
	protected int selected = -1;
	
	protected final Font TEXT_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
	protected final Font TITLE_FONT = TEXT_FONT.deriveFont(17.f);
	
	protected final Color MOUSE_OVER_COLOR = new Color(245, 245, 255);
	protected final Color SELECTED_COLOR = new Color(220, 220, 255);
	
	
	public QuestionVista(int nbCol, String title, String[] itemTitles, String[] itemTexts) {
		boolean[] enableds = new boolean[itemTitles.length];
		Arrays.fill(enableds, true);
		setup(nbCol, title, itemTitles, itemTexts, enableds);
	}
	
	public QuestionVista(int nbCol, String title, String[] itemTitles, String[] itemTexts, boolean[] enableds) {
		setup(nbCol, title, itemTitles, itemTexts, enableds);
	}
	
	private void setup(int nbCol, String title, String[] itemTitles, String[] itemTexts, boolean[] enableds) {
		setBackground(Color.white);
		setLayout(new BorderLayout(10, 20));
		ta = new JTextArea(title);
		ta.setBackground(getBackground());
		ta.setFont(TITLE_FONT);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		add("North", ta);
		itemsPanel = new JPanel(new GridLayout(0, nbCol, 20, 10));
		itemsPanel.setBackground(Color.white);
		items = new QuestionItem[itemTitles.length];
		for (int i = 0; i < itemTitles.length; i++) {
			items[i] = new QuestionItem(i, itemTitles[i], itemTexts[i], enableds[i]);
			CurvedBorder border = new CurvedBorder();
			border.setColor(Color.lightGray);
			items[i].setBorder(border);
			items[i].addMouseListener(new MyMouseListener(items[i]));
			itemsPanel.add(items[i]);
		}
		add("Center", itemsPanel);
		setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 20));
	}
	
	private int width = -1, height = -1;
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.width = width;
		this.height = height;
	}
	public Dimension getPreferredSize() {
		if (width < 0) return super.getPreferredSize();
		return new Dimension(width, height);
	}
	
	public void setBackground(Color color) {
		super.setBackground(color);
		if (ta != null) ta.setBackground(color);
		if (itemsPanel != null) itemsPanel.setBackground(color);
	}
	
	public void unselectAll() {
		for (int i = 0; i < items.length; i++) {
			QuestionItem qi = items[i];
			qi.setBackground(Color.white);
			CurvedBorder border = (CurvedBorder) qi.getBorder();
			border.setColor(Color.lightGray);
		}
		selected = -1;
	}
	
	public void select(int index) {
		if ((selected != -1) && (selected != index)) {
			QuestionItem prev = items[selected];
			prev.setBackground(Color.white);
			CurvedBorder border = (CurvedBorder) prev.getBorder();
			border.setColor(Color.lightGray);
		}
		selected = index;
		CurvedBorder border = (CurvedBorder) items[index].getBorder();
		border.setColor(Color.DARK_GRAY);
		items[index].setBackground(SELECTED_COLOR);
	}
	
	public void setItemEnabled(int index, boolean enable) {
		QuestionItem item = items[index];
		item.setItemEnabled(enable);
	}

	class QuestionItem extends JPanel {
		
		protected int index;
		protected String itemTitle;
		protected JLabel itemTitleLabel;
		protected JLabel itemTextLabel;
		protected boolean enabled;
		private JPanel westPanel, centerPanel;
		private JTextArea ta;
								
		public QuestionItem(int index, String itemTitle, String itemText, boolean enabled) {
			this.index = index;
			this.itemTitle = itemTitle;
			this.enabled = enabled;
			setLayout(new BorderLayout());
			setBackground(Color.white);
			westPanel = new JPanel();
			westPanel.setBackground(Color.white);
			westPanel.add((enabled) ? new JLabel(arrowEnabled) : new JLabel(arrowDisabled));
			add("West", westPanel);
			centerPanel = new JPanel(new BorderLayout());
			centerPanel.setBackground(Color.white);
			itemTitleLabel = new JLabel("<html>" + itemTitle + "</html>");
			itemTitleLabel.setFont(TITLE_FONT);
			if (!enabled) itemTitleLabel.setForeground(Color.lightGray);
			centerPanel.add("North", itemTitleLabel);
			itemTextLabel = new JLabel("<html>" + itemText + "</html>");
			itemTextLabel.setFont(TEXT_FONT);
			if (enabled)
				itemTextLabel.setForeground(Color.blue);
			else
				itemTextLabel.setForeground(Color.lightGray);
			centerPanel.add("Center", itemTextLabel);
			add("Center", centerPanel);
		}
		
		public String toString() {
			return itemTitle;
		}
		
		public void setItemEnabled(boolean enabled) {
			this.enabled = enabled;
			westPanel.removeAll();
			westPanel.add((enabled) ? new JLabel(arrowEnabled) : new JLabel(arrowDisabled));
			if (enabled) {
				itemTitleLabel.setForeground(Color.black);
				itemTextLabel.setForeground(Color.blue);
			} else {
				itemTitleLabel.setForeground(Color.lightGray);
				itemTextLabel.setForeground(Color.lightGray);
			}
		}
		
		public void setBackground(Color color) {
			super.setBackground(color);
			if (westPanel != null) westPanel.setBackground(color);
			if (centerPanel != null) centerPanel.setBackground(color);
			if (ta != null) ta.setBackground(color);
		}
	}
	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		listeners.remove(listener);
	}
	
	class MyMouseListener extends MouseAdapter {
		
		private QuestionItem qi;
		
		public MyMouseListener(QuestionItem qi) {
			this.qi = qi;
		}

		public void mouseClicked(MouseEvent ev) {
		}
		public void mousePressed(MouseEvent ev) {
		}
		
		public void mouseReleased(MouseEvent ev) {
			if (!qi.enabled) return;
			select(qi.index);
			ActionEvent ae = new ActionEvent(qi, qi.index, "");
			for (int i = 0; i < listeners.size(); i++) {
				ActionListener listener = (ActionListener) listeners.get(i);
				listener.actionPerformed(ae);
			}
		}

		public void mouseEntered(MouseEvent ev) {
			if ((qi.index != selected) && qi.enabled) {
				CurvedBorder border = (CurvedBorder) qi.getBorder();
				border.setColor(Color.DARK_GRAY);
				qi.setBackground(MOUSE_OVER_COLOR);
			}
			repaint();
		}

		public void mouseExited(MouseEvent ev) {
			if (qi.index == selected) {
				qi.setBackground(SELECTED_COLOR);
			} else {
				qi.setBackground(Color.white);
				CurvedBorder border = (CurvedBorder) qi.getBorder();
				border.setColor(Color.lightGray);
			}
			repaint();
		}
	}
	
	
	public static void main(String[] args) {
		QuestionVista q = new QuestionVista(1, "Choisir une imprimante locale ou réseau",
				new String[] {"Ajouter une imprimante locale", "Ajouter une imprimante réseau, sans fil ou Bluetooth"},
				new String[] {"Utilisez cette option uniquement si vous n'avez pas d'imprimante USB. (Windows installe automatiquement les imprimantes USB)", "Vérifiez que votre ordinateur est connecté au réseau, ou que votre imprimante Bluetooth ou sans fil est allumée"}
				);
		q.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("clicked on id=" + ev.getID());
			}
		});
		q.setItemEnabled(1, false);
		JFrame frame = new JFrame("test question");
		frame.setContentPane(q);
		frame.setSize(580, 350);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
