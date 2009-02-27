package com.jameslow;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GUIUtils {
	public static void addPopupListener(JComponent comp, List list) {
		final JPopupMenu popup = new JPopupMenu();
		for(int i=0; i < list.size(); i++) {
			JMenuItem menuItem = (JMenuItem) list.get(i);
			popup.add(menuItem);
		}
		comp.add(popup);
		comp.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }
		    public void mouseReleased(MouseEvent e) {
		        maybeShowPopup(e);
		    }
		    private void maybeShowPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            popup.show(e.getComponent(), e.getX(), e.getY());
		        }
		    }
		});
	}
}
