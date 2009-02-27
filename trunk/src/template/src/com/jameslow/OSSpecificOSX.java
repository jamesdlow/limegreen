package com.jameslow;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;

import javax.swing.JFileChooser;

import com.apple.eawt.*;
import com.apple.eio.FileManager;
import com.jameslow.FileUtils.Filefilter;

public class OSSpecificOSX extends OSSpecific implements ApplicationListener {
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
		fApplication.addApplicationListener(this);
	}
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
	public void handleReOpenApplication(ApplicationEvent event) {}
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
	protected File saveOpenFileDialog(Frame parent, boolean dirsonly, String title, String dir, Filefilter[] filters, boolean save) {
		if (filters.length > 1) {
			return super.saveOpenFileDialog(parent, dirsonly, title, dir, filters, save);
		} else {
			FileDialog dialog = new FileDialog(parent,title,(save ? FileDialog.SAVE : FileDialog.LOAD));
			if (dir == null) {
				dialog.setDirectory(homeDir());
			} else {
				dialog.setDirectory(dir);	
			}
			if (filters.length > 0) {
				dialog.setFilenameFilter(FileUtils.getExtFilenameFilter(filters[0]));
			}
			if (dirsonly) {
				System.setProperty("apple.awt.fileDialogForDirectories", "true");
			} else {
				System.setProperty("apple.awt.fileDialogForDirectories", "false"); //Make sure this is set, incase other code hasn't cleaned up
			}
			dialog.show();
			System.setProperty("apple.awt.fileDialogForDirectories", "false"); //Always make sure we clean up
			String filename = dialog.getFile();
			if (filename != null) {
				return new File(dialog.getDirectory() + fileSeparator() + filename);
			}
			return null;
		}
	}
	
	public boolean addQuit() {
		return false;
	}
	public boolean addIcon() {
		return false;
	}
}
