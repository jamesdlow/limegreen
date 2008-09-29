package com.jameslow;

import java.util.*;
import java.awt.event.*;

import javax.swing.*;

public class MainWindow extends AbstractWindow {
	protected Action aboutAction, exitAction, prefAction;
	
	public MainWindow() {
		super();
		addListener();
		createActions();
		setJMenuBar(createMenu());
	}
	public String getDefaultTitle() {
		return Main.Settings().getTitle();
	}
	public WindowSettings getDefaultWindowSettings() {
		return new WindowSettings(320,160,0,0,true);
	}
	private void addListener() {
		if (Main.OS().addQuit()) {
			addWindowListener(new WindowListener() {
				public void windowClosing(WindowEvent e) {
					Main.quit();
				}
				public void windowClosed(WindowEvent e) {}
				public void windowOpened(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowGainedFocus(WindowEvent e) {}
				public void windowLostFocus(WindowEvent e) {}
				public void windowStateChanged(WindowEvent e) {}
			});
		}
	}
	public void createActions() {
		//TODO: Figure out why we have to override this
		int shortcutKeyMask = Main.OS().shortCutKey();
		if (Main.OS().addQuit()) {
			aboutAction = new aboutActionClass("About", KeyStroke.getKeyStroke(KeyEvent.VK_B, shortcutKeyMask));
			exitAction = new exitActionClass("Exit", KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutKeyMask));
			prefAction = new exitActionClass("Options", KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKeyMask));
		}
	}
	public JMenuBar createMenu() {
		JMenuBar mainMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
			if (Main.OS().addQuit()) {
				fileMenu.add(new JMenuItem(exitAction));	
			}
		if (fileMenu.getItemCount() >= 1) {
			mainMenuBar.add(fileMenu);
		}
		JMenu viewMenu = new JMenu("View");
		if (Main.OS().addQuit()) {
			viewMenu.add(new JMenuItem(prefAction));	
		}
		if (viewMenu.getItemCount() >= 1) {
			mainMenuBar.add(viewMenu);
		}
		JMenu helpMenu = new JMenu("Help");
		if (Main.OS().addQuit()) {
			helpMenu.add(new JMenuItem(aboutAction));	
		}
		if (helpMenu.getItemCount() >= 1) {
			mainMenuBar.add(helpMenu);
		}
		return mainMenuBar;
	}
	public class exitActionClass extends AbstractAction {
		public exitActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			Main.quit();
		}
	}
	public class aboutActionClass extends AbstractAction {
		public aboutActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			Main.about();
		}
	}
	public class prefActionClass extends AbstractAction {
		public prefActionClass(String text, KeyStroke shortcut) {
			super(text);
			putValue(ACCELERATOR_KEY, shortcut);
		}
		public void actionPerformed(ActionEvent e) {
			Main.preferences();
		}
	}
}