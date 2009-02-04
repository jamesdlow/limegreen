package com.jameslow;

import java.awt.Color;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

/**
 * Standard way of doing settings
 * TODO: Decide if we have standard way to set settings, so on OSX they're immediate, and on windows it takes affect when you click ok.
 * TODO: Also make sure we don't save until we click ok or apply setting like OSX 
 * TODO: Also an event to say that a certain setting has changes, incase we need to apply it, store up on Windows, immediate OS X?
 * TODO: Also a flag to say a setting has changed that won't apply until you restart
 * TODO: Settings list interface
 * @author James
 */
public class Settings {
	private OSSpecific os;
	private XMLHelper xmlhelper;
	
	private String aboutimage;
	private String abouttext;
	private String abouturl;
	private String buildnumber;
	private String builddate;
	private String mainclass;
	private String title;
	private String version;
	private String settingsfile;
	private String logfile;
	
	private Level loglevel;
	private boolean logtoconsole;
	private boolean logtofile;
	
	public static final String HEIGHT = "Height";
	public static final String WIDTH = "Width";
	public static final String TOP = "Top";
	public static final String LEFT = "Left";
	public static final String VISIBLE = "Visible";
	public static final String WINDOW = "Windows.Window";
	
	public Settings() {
		os = Main.OS();
		loadProperties();
		loadFiles();
		loadCommonSettings();
		loadSettings();
	}
	public void loadCommonSettings() {
		logtofile = getSetting("Common.Log.ToFile", logtofile);
		logtoconsole = getSetting("Common.Log.ToConsole", logtoconsole);
		try {
			loglevel = Level.parse(getSetting("Common.Log.Level",loglevel.toString()));
		} catch (Exception e) {
			Main.Logger().warning("Log level in settings file not recognised.");
		}
		//TODO: Only save if we we're ok loading settings before, otherwise we loose default?
		//saveSettings();
	}
	public void loadSettings() {}
	public void loadFiles() {
		String dir = os.settingsDir();
        (new File(dir)).mkdirs();
        settingsfile = dir + os.fileSeparator()  + os.appName() + ".xml";
        dir = os.logDir();
        (new File(dir)).mkdirs();
        logfile = dir + os.fileSeparator()  + os.appName() + ".log";
        xmlhelper = new XMLHelper(settingsfile,os.appName());
	}
	public String getLogFile() {
		return logfile;
	}
	public String getSettingsFile() {
		return settingsfile;
	}
	public String getTitle() {
		return title;
	}
	public String getAboutText() {
		return abouttext;
	}
	public String getAboutImage() {
		return aboutimage;
	}
	public String getBuild() {
		return buildnumber;
	}
	public String getVersion() {
		return version;
	}
	public String getBuildDate() {
		return builddate;
	}
	public String getMainClass() {
		return mainclass;
	}
	public String getAboutURL() {
		return abouturl;
	}
	public Level getLogLevel() {
		return loglevel;
	}
	public boolean getLogToConsole() {
		return logtoconsole;
	}
	public boolean getLogToFile() {
		return logtofile;
	}
	private void loadProperties() {
		ResourceBundle build = os.getBuildProps();
			builddate = build.getString("build.date");
			buildnumber = build.getString("build.number");
			version = build.getString("build.version");
			mainclass = build.getString("main.class");
			title = build.getString("application.name");
			//mainclass = readManifest(Attributes.Name.MAIN_CLASS);
			//build = readManifest("Build-Number");
			//version = readManifest("Build-Version");
		
		ResourceBundle main = os.getMainProps();
			abouttext = getProperty("about.text",title);
			aboutimage = getProperty("about.image");
			abouturl = getProperty("about.url","");
			logtofile = getBooleanProperty("log.tofile");
			logtoconsole = getBooleanProperty("log.toconsole");
			try {
				loglevel = Level.parse(getProperty("log.level","WARNING"));
			} catch (Exception e) {
				//TODO: Something very basic has gone wrong, log everything
				Main.Logger().severe("Log level in properties file not recognised.");
				loglevel = Level.ALL;
			}
	}
	public boolean getBooleanProperty(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}
	public int getIntProperty(String key) {
		return Integer.parseInt(getProperty(key));
	}
	public float getFloatProperty(String key) {
		return Float.parseFloat(getProperty(key));
	}
	public String getProperty(String key) {
		return getProperty(key,null);
	}
	public String getProperty(String key, String def) {
		try {
			return os.getMainProps().getString(key);
		} catch (Exception e) {
			Main.Logger().warning("Property Not Found: " + key);
			return def;
		}
	}
	public String[] getPropertyList(String key) {
		try {
			return os.getMainProps().getString(key).split(",");
		} catch (Exception e) {
			Main.Logger().warning("Property Not Found: " + key);
			return new String[0];
		}
	}
	public InputStream getResourceAsStream(String resource) {
		return getClass().getClassLoader().getResourceAsStream(resource);
	}
	public String readManifest(String attribute) {
		try {
			return getManifest().getMainAttributes().getValue(attribute);
		} catch (Exception e) {
			Main.Logger().warning("Cannot get manifest attribute: " + attribute);
			return null;
		}
	}
	public String readManifest(Name name) {
		try {
			return getManifest().getMainAttributes().getValue(name);
		} catch (Exception e) {
			Main.Logger().warning("Cannot get manifest name: " + name);
			return null;
		}
	}
	public Manifest getManifest() {
		//TODO: Need to make sure this works with all forms of build (windows)
		Manifest manifest = null;
		try {
			InputStream is = Main.class.getResourceAsStream("/main/main.jar");
			//InputStream is = getResourceAsStream("/main/main.jar");
			JarInputStream jis = new JarInputStream(is);
			manifest = jis.getManifest();
			return manifest;
		} catch (Exception e) {
			try {
				String pathToThisClass = Main.class.getResource("/"+Main.class.getName().replaceAll("\\.", "/")+".class").toString();
				String manifestPath = pathToThisClass.substring(0, pathToThisClass.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
				manifest = new Manifest(new URL(manifestPath).openStream());
			} catch (Exception e2) {
				Main.Logger().warning("Cannot get manifest file: " + e2.getMessage());
				return null;
			}
		}
		return manifest;
	}
	public void saveSettings() {
		preSaveSettings();
		//TODO: Handle window position saving
		xmlhelper.save(settingsfile);
		postSaveSettings();
	}
	public void preSaveSettings() {}
	public void postSaveSettings() {}
	public String getSetting(String key, String def) {
		return xmlhelper.getValue(key, def);
	}
	public int getSetting(String key, int def) {
		return xmlhelper.getValue(key, def);
	}
	public float getSetting(String key, float def) {
		return xmlhelper.getValue(key, def);
	}
	public boolean getSetting(String key, boolean def) {
		return xmlhelper.getValue(key, def);
	}
	public Color getSetting(String key, Color def) {
		return xmlhelper.getValue(key, def);
	}
	public void setSetting(String key, String value) {
		xmlhelper.setValue(key, value);
	}
	public void setSetting(String key, int value) {
		xmlhelper.setValue(key, value);
	}
	public void setSetting(String key, float value) {
		xmlhelper.setValue(key, value);
	}
	public void setSetting(String key, boolean value) {
		xmlhelper.setValue(key, value);
	}
	public void setSetting(String key, Color value) {
		xmlhelper.setValue(key, value);
	}
	public XMLHelper getXMLHelper(String key) {
		return xmlhelper.getSubNode(key);
	}
	public XMLHelper getXMLHelperByName(String key, String name) {
		return xmlhelper.getSubNodeByName(key,name);
	}
	public XMLHelper[] getXMLHelpers(String key) {
		return xmlhelper.getSubNodeList(key);
	}
	public XMLHelper getWindowXMLHelper(String classname) {
		return getXMLHelperByName(WINDOW, classname);
	}
	public WindowSettings getWindowSettings(String classname) {
		return getWindowSettings(classname,320,160,0,0,true);
	}
	public WindowSettings getWindowSettings(String classname, int width, int height, int left,int top, boolean visible) {
		return getWindowSettings(classname,new WindowSettings(width,height,left,top,visible));
	}
	public WindowSettings getWindowSettings(String classname, WindowSettings ws) {
		XMLHelper window = getWindowXMLHelper(classname);
		if (!window.getIsNewNode()) {
			//TODO: Decide if here, if even one value is wrong, we should return all the defaults, and also save them
			ws = new WindowSettings(window.getValue(WIDTH, ws.getWidth()),window.getValue(HEIGHT, ws.getHeight()),window.getValue(LEFT, ws.getLeft()),window.getValue(TOP, ws.getTop()),window.getValue(VISIBLE, ws.getVisible()));
		}
		setWindowSettings(classname,ws);
		return ws;
	}
	public void setWindowSettings(String classname, int width, int height, int left,int top, boolean visible) {
		XMLHelper window = getWindowXMLHelper(classname);
		window.getSubNode(WIDTH).setValue(width);
		window.getSubNode(HEIGHT).setValue(height);
		window.getSubNode(LEFT).setValue(left);
		window.getSubNode(TOP).setValue(top);
		window.getSubNode(VISIBLE).setValue(visible);
	}
	public void setWindowSettings(String classname, WindowSettings ws) {
		setWindowSettings(classname,ws.getWidth(),ws.getHeight(),ws.getLeft(),ws.getTop(),ws.getVisible());
	}
	public XMLHelper getXMLHelper() {
		return xmlhelper;
	}
}
