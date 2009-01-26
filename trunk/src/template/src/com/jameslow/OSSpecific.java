package com.jameslow;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import com.jameslow.FileUtils.Filefilter;

//Other:
//ALLUSERPROFILE (XP) /Users/Shared on OSX
//TMP/TEMP

public class OSSpecific {
	private static final String MAIN_BUNDLE = "main";
	private static final String BUILD_BUNDLE = "build";
	//Locale("fr", "CH"): result MyResources_fr_CH.class, parent MyResources_fr.properties, parent MyResources.class 
	//Locale("fr", "FR"): result MyResources_fr.properties, parent MyResources.class 
	//Locale("de", "DE"): result MyResources_en.properties, parent MyResources.class 
	//Locale("en", "US"): result MyResources_en.properties, parent MyResources.class
	//Locale("en", "GB"): result MyResources_en.properties, parent MyResources.class
	//Locale("es", "ES"): result MyResources_es_ES.class, parent MyResources.class
	protected static final ResourceBundle build = ResourceBundle.getBundle(BUILD_BUNDLE, Locale.getDefault());
	protected static final ResourceBundle main = ResourceBundle.getBundle(MAIN_BUNDLE, Locale.getDefault());
	protected static final Properties properties = System.getProperties();
	
	//Constants
	protected String homedir;
	protected int shortcutkey;
	protected String fileseparator;
	protected String lineseparator;
	protected String pathseparator;
	protected String appname;
	protected String settingsdir;
	protected String logdir;
	protected String tempdir;
	
	protected OSSpecific() {
		homedir = properties.getProperty("user.home");
		fileseparator = properties.getProperty("file.separator");
		lineseparator = properties.getProperty("line.separator");
		pathseparator = properties.getProperty("path.separator");
		appname = build.getString("application.name");
		setSettingsDir();
		setLogDir();
		setTempDir();
		this.preSwing();
			shortcutkey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			setFont();
			//try {
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				//"apple.laf.AquaLookAndFeel"
				//"com.sun.java.swing.plaf.gtk.GTKLookAndFeel" //Linux should return this as the default, but it doesn't
				//"com.sun.java.swing.plaf.motif.MotifLookAndFeel"
				//"com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
				//"com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"
				//UIManager.getCrossPlatformLookAndFeelClassName();
				//properties.setProperty("swing.defaultlaf","com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//} catch (Exception e) { }
		this.postSwing();
		Main.os = this;
	}
	//Statics
	public static OSSpecific getInstance() {
		String osname = properties.getProperty("os.name").toLowerCase();
		if (osname.startsWith("mac os x")) {
			return new OSSpecificOSX();
		} else if (osname.startsWith("windows")) {
			return new OSSpecificWindows();
		} else {
			return new OSSpecific();
		}
	}
	
	//Callbacks
	public void preSwing() {}
	public void postSwing() {}
	public void setFont() {
		Font font = new Font("Arial", Font.PLAIN, 11);
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key,font);
		}
	}
	public void setSettingsDir() {
		settingsdir = homeDir() + fileSeparator() + "." + appName();
	}
	public void setLogDir() {
		logdir = settingsdir;
	}
	public void setTempDir() {
		tempdir = System.getProperty("java.io.tmpdir");
	}
	
	//Helper Functions
	public void openURL(String url) {
		String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
		String browser = null;
		for (int count = 0; count < browsers.length && browser == null; count++) {
			try {
				if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0) {
					browser = browsers[count];
				}
			} catch (Exception e) {} 
		}
		if (browser != null) {
			try {
				Runtime.getRuntime().exec(new String[] {browser, url});
			} catch (Exception e) {}
		}
	}
	public void openFile(String file) {
		//TODO: Implement For Unix based OS
	}
	public void openFolder(String folder) {
		//TODO: Implement For Unix based OS
	}
	public ResourceBundle getMainProps() {
		return main;
	}
	public ResourceBundle getBuildProps() {
		return build;
	}
	
	//Save / Open dialogs
	public File openFileDialog(Frame parent) {
		return openFileDialog(parent,false);
	}
	public File openFileDialog(Frame parent, boolean dirsonly) {
		return openFileDialog(parent,dirsonly,null);
	}
	public File openFileDialog(Frame parent, boolean dirsonly, String title) {
		return openFileDialog(parent,dirsonly,title,null);
	}
	public File openFileDialog(Frame parent, boolean dirsonly, String title, String dir) {
		return openFileDialog(parent,dirsonly,title,dir,new Filefilter[0]);
	}
	public File openFileDialog(Frame parent, boolean dirsonly, String title, String dir, Filefilter[] filters) {
		return saveOpenFileDialog(parent,dirsonly,title,dir,filters,false);
	}
	protected File saveOpenFileDialog(Frame parent, boolean dirsonly, String title, String dir, Filefilter[] filters, boolean save) {
		JFileChooser fc = new JFileChooser(dir);
		if (dirsonly) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		for (int i=0; i<filters.length; i++) {
			fc.addChoosableFileFilter(FileUtils.getExtFileFilter(filters[i]));
		}
		if (title != null) {
			fc.setDialogTitle(title);
		}
		if (save) {
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
	}
	public File saveFileDialog(Frame parent) {
		return saveFileDialog(parent,null);
	}
	public File saveFileDialog(Frame parent, String title) {
		return saveFileDialog(parent,title,null);
	}
	public File saveFileDialog(Frame parent, String title, String dir) {
		return saveFileDialog(parent,title,dir,new Filefilter[0]);
	}
	public File saveFileDialog(Frame parent, String title, String dir, Filefilter[] filters) {
		return saveOpenFileDialog(parent,false,title,dir,filters,true);
	}
	
	//Constants
	public String appName() {
		return appname;
	}
	public int shortCutKey() {
		return shortcutkey;
	}
	public String fileSeparator() {
		return fileseparator;
	}
	public String lineSeparator() {
		return lineseparator;
	}
	public String pathSeparator() {
		return pathseparator;
	}
	public String homeDir() {
		return homedir;
	}
	public String settingsDir() {
		return settingsdir;
	}
	public String logDir() {
		return logdir;
	}
	public String tempDir() {
		return tempdir;
	}
	public boolean addQuit() {
		return true;
	}
	public boolean addIcon() {
		return true;
	}
}