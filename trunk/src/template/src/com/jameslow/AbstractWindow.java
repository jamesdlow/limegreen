package com.jameslow;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public abstract class AbstractWindow extends JFrame {
	public AbstractWindow() {
		super();
		setTitle(getDefaultTitle());
		setSettingBounds();
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
	public void setBounds(WindowSettings settings) {
		setBounds(settings.getLeft(), settings.getTop(), settings.getWidth(), settings.getHeight());
	}
	public void setDefaultBounds() {
		setBounds(getDefaultWindowSettings());
	}
	public void setSettingBounds() {
		setBounds(getWindowSettings());
	}
	public class closeActionClass extends AbstractAction {
		private JFrame window;
		public closeActionClass(String text, JFrame window) {
			super(text);
			this.window = window;
		}
		public void actionPerformed(ActionEvent e) {
			window.setVisible(false);
		}
	}
}