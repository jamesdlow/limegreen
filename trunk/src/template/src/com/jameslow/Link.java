package com.jameslow;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;

public class Link extends JLabel {
	public Link(String text, final String url) {
		super("<html><a href=\"" + url + "\">" + text + "</a>");
		addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {
			Main.OS().openURL(url);
		}
		public void mouseEntered(MouseEvent e) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		public void mouseExited(MouseEvent e) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	});
	}
}
