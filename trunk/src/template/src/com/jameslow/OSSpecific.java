package com.jameslow;

import java.awt.Font;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import javax.swing.UIManager;

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
	
	protected OSSpecific() {
		homedir = properties.getProperty("user.home");
		fileseparator = properties.getProperty("file.separator");
		lineseparator = properties.getProperty("line.separator");
		pathseparator = properties.getProperty("path.separator");
		appname = build.getString("application.name");
		setSettingsDir();
		setLogDir();
		this.preSwing();
			shortcutkey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			setFont();
			//try {
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				//"apple.laf.AquaLookAndFeel"
				//"com.sun.java.swing.plaf.gtk.GTKLookAndFeel" //Linux should return this as the dfault, but it doesn't
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
		//TODO: Implement For Linux
	}
	public void openFolder(String folder) {
		//TODO: Implement For Linux
	}
	public ResourceBundle getMainProps() {
		return main;
	}
	public ResourceBundle getBuildProps() {
		return build;
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
	public boolean addQuit() {
		return true;
	}
	public boolean addIcon() {
		return true;
	}
}