package com.jameslow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public abstract class AbstractWindow extends JFrame {
	public AbstractWindow() {
		super();
		setTitle(getDefaultTitle());
		WindowSettings ws = getWindowSettings(); //This will be the classname of any inheriting classes
		setSize(ws.getWidth(),ws.getHeight());
		setLocation(ws.getLeft(),ws.getTop());
		if (Main.OS().addIcon()) {
			ImageIcon image = Main.AboutImage();
			if (image != null) {
				setIconImage(image.getImage());
			}
		}
	}
	public abstract String getDefaultTitle();
	public abstract WindowSettings getDefaultWindowSettings();
	public WindowSettings getWindowSettings() {
		return Main.Settings().getWindowSettings(getClass().getName(),getDefaultWindowSettings());
	}
	public class closeActionClass extends AbstractAction {
		private JFrame window;
		public closeActionClass(String text, JFrame window) {
			super(text);
			this.window = window;
			//, KeyStroke shortcut
			//putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			window.setVisible(false);
		}
	}
}
