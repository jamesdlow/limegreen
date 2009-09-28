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
		setSettingBounds();
		if (Main.OS().addIcon()) {
			ImageIcon image = Main.AboutImage();
			if (image != null) {
				setIconImage(image.getImage());
			}
		}
		Main.addWindow(this);
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
	public boolean getJustHide() {
		//Can override this if your window contains a document that you want to close on OSX 
		//Or if you want to minimize an application to the system tray on Windows/Unix
		return !Main.OS().addQuit();
	}
	public boolean onClose() {
		return true;
	}
}