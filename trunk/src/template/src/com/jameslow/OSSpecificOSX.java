package com.jameslow;

import java.io.*;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eio.FileManager;

public class OSSpecificOSX extends OSSpecific {
	private static final String LIBRARY = "Library";
	private static final String LOGS = "Logs";
	private static final String APPSUP = "Application Support";
	
	public OSSpecificOSX() {
		super();	
	}
	
	public void setSettingsDir() {
		String s = fileSeparator();
		settingsdir = homeDir() + s + LIBRARY + s + APPSUP + s + appName();
	}
	public void setLogDir() {
		String s = fileSeparator();
		logdir = homeDir() + s + LIBRARY + s + LOGS + s + appName();
	}	
	public void preSwing() {
		try {
			//need to do this before we make any calls to AWT or Swing functions
			//as when they initialise they stop us editing these two properties
			//"com.apple.macos.useScreenMenuBar" (old method, OSX 10.2 etc.)
			properties.setProperty("com.apple.mrj.application.apple.menu.about.name",appName());
			properties.setProperty("apple.laf.useScreenMenuBar","true");
		} catch (Exception e) {}
	}
	public void postSwing() {
		Application fApplication = Application.getApplication();
		fApplication.setEnabledPreferencesMenu(true);
		fApplication.addApplicationListener(new com.apple.eawt.ApplicationAdapter() {
			public void handleAbout(ApplicationEvent e) {
				Main.about();
				e.setHandled(true);
			}
			public void handleOpenApplication(ApplicationEvent e) {}
			public void handleOpenFile(ApplicationEvent e) {}
			public void handlePreferences(ApplicationEvent e) {
				Main.preferences();
			}
			public void handlePrintFile(ApplicationEvent e) {}
			public void handleQuit(ApplicationEvent e) {
				Main.quit();
			}
		});
	}
	public void openURL(String url) {
		try {
			FileManager.openURL(url);
		} catch (Exception e) {
			Main.Logger().warning("Could not open url: " + e.getMessage());
		}
	}
	public void openFile(String file) {
		// open -a "Finder" ~/
		//TODO: fix for ~/
		openURL((new File(file)).toURI().toString());
	}
	public void openFolder(String folder) {
		openFile(FileUtils.GetFolder(folder));
		/*
		try {
			Runtime.getRuntime().exec("open -a \"Finder\" \"" + folder + "\"").waitFor();
		} catch (Exception e) {
			Main.Logger().warning("Could not open folder: " + e.getMessage());
		}
		*/
	}
	public boolean addQuit() {
		return false;
	}
	public boolean addIcon() {
		return false;
	}
}
