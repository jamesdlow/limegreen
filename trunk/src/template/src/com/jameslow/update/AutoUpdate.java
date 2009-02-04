package com.jameslow.update;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.*;

//java com.jameslow.update.AutoUpdate Limelight 'http://jameslow.com/content/software/Limelight-mac-0.3.zip' '/Users/James/Documents/Programs/James/Eclipse/Limelight/build/dist/Limelight.app' Limelight.app
//TODO: Create java ant task to create/edit rss file
//TODO: Could integrate downloading into main part of class, then the update only has install and relaunch

//This is a big hack because I'm trying to get this into a single java class
//For some reason I thought that would be easier to copy from the jar to a temp location to launch, and it might be, we'll see
public class AutoUpdate implements ActionListener, ItemListener {
	//Stuff associated with checking for new versions
	private static JFrame releasewindow;
	private static JCheckBox checkforupdatesbutton;
	private static JCheckBox includeexperimentalbutton;
	private static JButton updatebutton;
	private static JButton cancelbutton;
	private static String download;
	private static String appname;
	private static boolean checkforupdates;
	private static boolean includeexperimental;
	private static ActionListener updatelistener;
	private static ActionListener cancellistener;
	
	//Stuff associated with downloading and extracting update
	private static final String AUTOUPDATE = "AutoUpdate";
	private static final int ARG_COUNT = 4;
	private static final String USAGE = "usage: " + AutoUpdate.class.getName() + " name url copy_target launch_app [delete_first]";
	private static JFrame installwindow;
	private static JProgressBar progressBar;
	private static JButton installbutton;
	private static File downloadfile;
	private static String copylocation;
	private static String launchapp;
	private static boolean deletefirst;
	private static final String s = System.getProperty("file.separator");
	private static final String tempdir = System.getProperty("java.io.tmpdir");
	private static final String fullname = AutoUpdate.class.getName();
	private static final int lastdot = fullname.lastIndexOf(".");
	private static final String pack = fullname.substring(0, lastdot);
	private static final String name = fullname.substring(fullname.lastIndexOf("."));
	private static final String CLASS = "class";
	
	//We're at the checking for updates stage
	//Consuming classes should implement an ActionListener for when the auto update proceeds and cancelled
	//Consuming classes can check if the user has checked check for updates in the future by calling getCheckForUpdates(), this can then be saved as a setting
	public static void checkUpdate(String name, String url, String version, int build, boolean includeminorbuilds, boolean experimental, boolean autoupdate, ActionListener update, ActionListener cancel) {
		checkforupdates = autoupdate;
		includeexperimental = experimental;
		cancellistener = cancel;
		updatelistener = update;
		appname = name;
		if (autoupdate) {
			try {
				String versionxml = new String(getHttpContent(url));
				releasewindow = new JFrame(name);
				//TODO: parse xml / compare versions / get download file
				//TODO: This needs to account for if we happen to be in a jar on any of the platforms in terms of search for -other- or -win- or -mac file
				download = "";
				JTextArea box = new JTextArea();
				AutoUpdate listener = new AutoUpdate();
				updatebutton = new JButton("Update");
				updatebutton.addActionListener(listener);
				cancelbutton = new JButton("Update");
				cancelbutton.addActionListener(listener);
				checkforupdatesbutton = new JCheckBox("Check for updates?",autoupdate);
				checkforupdatesbutton.addItemListener(listener);
				includeexperimentalbutton = new JCheckBox("Include Experimental",experimental);
				includeexperimentalbutton.addItemListener(listener);
				releasewindow.show();
			} catch (IOException e) {
				//Not connected to the internet or can't contact webpage, just go on
			}
		}
	}
	private static void hideWindow() {
		releasewindow.hide();
	}
	public static boolean getCheckForUpdates() {
		return checkforupdates;
	}
	private static void cancel(ActionEvent e) {
		hideWindow();
		cancellistener.actionPerformed(e);
	}
	private static void update(ActionEvent e) {
		hideWindow();
		//TODO: Work these out from system.
		String deploy = "";
		String launch = "";
		try {
			File dir = new File(tempdir + s + pack.replaceAll("\\.", s));
			dir.mkdirs();
			String classname = dir.toString()+s+name;
			File file = new File(classname+"."+CLASS);
			file.deleteOnExit();
			InputStream is = AutoUpdate.class.getResourceAsStream("/"+fullname.replaceAll("\\.", "/")+ "."+CLASS);
			copyInputStream(is,new FileOutputStream(file));
			final String p = " ";
			Runtime.getRuntime().exec("java "+classname+p+appname+p+download+p+deploy+p+launch,null,dir).waitFor();
			
		} catch (Exception ex) {
			Error("Could not launch update program: " + ex.getMessage());
		}
		updatelistener.actionPerformed(e);
		//Dorce quit here, then updatelistener only needs to handle things like saving settings
		System.exit(0);
	}
	
	//We're at the download and extracting stage
	public static void main(String[] args) {
		try {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name","AutoUpdate");
			System.setProperty("apple.laf.useScreenMenuBar","true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initWindow(null);
		if (args.length >= ARG_COUNT) {
			String name = args[0];
			String url = args[1];
			copylocation = args[2];
			launchapp = args[3];
			deletefirst = false;
			int mode = 0;
			if (args.length > ARG_COUNT) {
				try {
					mode = Integer.parseInt(args[4]);
					deletefirst = mode > 0;
				} catch (NumberFormatException e) {
					Error(USAGE + " - delete_first must be a number");
				}
			}
			downloadUpdate(name,url);
		} else {
			Error(USAGE);
		}
	}
	private static void initWindow(String name) {
		if (installwindow == null) {
			final int height = 90;
			final int width = 200;
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Point center = ge.getCenterPoint();
			installwindow = new JFrame(AUTOUPDATE + (name == null ? "" : " - " + name));
				JPanel panel = new JPanel();
				installwindow.add(panel);
					progressBar = new JProgressBar();
					panel.add(progressBar);
					installbutton = new JButton("Install and relaunch");
					installbutton.addActionListener(new AutoUpdate());
					installbutton.setEnabled(false);
					panel.add(installbutton);
					installwindow.setBounds((int) (center.getX() - width/2),(int) (center.getY() - height/2), width, height);
					installwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				installwindow.show();
		} else {
			installwindow.setTitle(AUTOUPDATE + (name == null ? "" : " - " + name));
		}
	}
	private static void downloadUpdate(String name, String url) {
		final String couldnot = "Autoupdate could not be completed.";
		try {
			initWindow(name);
			int i = 0;
			//while file exists work out name to download to incrementing suffix
			while ((downloadfile = new File(tempdir + s + name + "AutoUpdate" + i + ".zip")).exists()) {
				i++;
			}
			if (downloadfile.createNewFile()) {
				downloadfile.deleteOnExit();
				getHttpContent(url, new FileOutputStream(downloadfile), true);
				//TODO: check file integrity against size / MD5
				installbutton.setEnabled(true);
			} else {
				Error(couldnot + " Could not create temporary file.");
			}
		} catch (IOException e) {
			Error(couldnot);
		}
	}
	private static final boolean launchApplication(String path, String application) {
		String osname = System.getProperty("os.name").toLowerCase();
		try {
			if (application.endsWith(".jar")) {
				if (osname.startsWith("mac os x")) {
					Runtime.getRuntime().exec("open -a "+path+s+application).waitFor();
				} else {
					Runtime.getRuntime().exec("java "+path+s+application).waitFor();
				}
			} else {
				if (osname.startsWith("mac os x")) {
					Runtime.getRuntime().exec("open -a "+path).waitFor();
				} else if (osname.startsWith("windows")) {
					Runtime.getRuntime().exec(path+s+application).waitFor();
				} else {
					Runtime.getRuntime().exec("java "+path+s+application).waitFor();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		copyInputStream(in,out,false,-1);
	}
	private static final void copyInputStream(InputStream in, OutputStream out, boolean update, int length) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		if (update) {
			progressBar.setMaximum(length);
		}
		int total = 0;
		while((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			if (update) {
				total = total + len;
				progressBar.setValue(total);
				progressBar.setStringPainted(true);
			}
		}
		in.close();
		out.close();
	}
	private static void createEntry(ZipFile zipFile, ZipEntry entry, String outdir, String prefix) throws IOException {
		String name = entry.getName();
		if (prefix != null) {
			name = name.substring(prefix.length());
		}
		//Error(name);
		name = outdir + s + name;
		//Error(name);
		if(entry.isDirectory()) {
			//Assume directories are stored parents first then children.
			//This is not robust, just for demonstration purposes.
			(new File(outdir, name)).mkdir();
		} else {
			copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(name)));
		}
	}
	private static boolean unzip(File infile, String outdir, boolean deletefirst) {
		//TODO: delete all in directory, maybe we should have a delete first, accept for .app on OSX
		
		Enumeration entries;
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(infile);
			
			//Scan through all to see if they're inside a single directory
			entries = zipFile.entries();
			ZipEntry first = (ZipEntry)entries.nextElement();
			String firstname = null;
			if (first.isDirectory()) {
				firstname = first.getName();
				while(entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry)entries.nextElement();
					if (!entry.getName().startsWith(firstname)) {
						firstname = null;
						break;
					}
				}
			}

			//Extract zip
			entries = zipFile.entries();
			first = (ZipEntry)entries.nextElement();
			if (firstname == null) {
				createEntry(zipFile, first, outdir, firstname);
			}
			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();
				createEntry(zipFile, entry, outdir, firstname);
			}
			zipFile.close();
			return true;
		} catch (IOException ioe) {
			Error("Could not extract zip file.");
			return false;
		}
	}
	private static void getHttpContent(String url, OutputStream out, boolean update) throws IOException {
		URL u = new URL(url); 
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET"); 
		huc.connect(); 
		int code = huc.getResponseCode();
		int length = huc.getContentLength();
		copyInputStream(huc.getInputStream(), out, update, length);
		out.close();
		huc.disconnect();
	}
	private static char[] getHttpContent(String url) throws IOException {
		URL u = new URL(url); 
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET"); 
		huc.connect(); 
		int code = huc.getResponseCode();
		char[] result = null;
		//if (code >= 200 && code < 300) {
			InputStreamReader in = new InputStreamReader(huc.getInputStream());
			//BufferedReader in = new BufferedReader(new InputStreamReader(huc.getInputStream()));
			int total = in.read(result);
		//}
		huc.disconnect();
		return result;
	}
	private static void installAndRelaunch() {
		unzip(downloadfile,copylocation,deletefirst);
		downloadfile.delete();
		if (!launchApplication(copylocation,launchapp)) {
			Error("Could not relaunch application.");
		}
		System.exit(0);
	}
	
	//Common
	public static void Error(String msg) {
		System.out.println(msg);
	}
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == checkforupdatesbutton) {
			checkforupdates = e.getStateChange() == ItemEvent.SELECTED; 
		} else if (source == includeexperimentalbutton) {
			includeexperimental = e.getStateChange() == ItemEvent.SELECTED;
		}
	}
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == installbutton) {
			installAndRelaunch();
		} else if (source == updatebutton) {
			update(e);
		} else if (source == cancelbutton) {
			cancel(e);
		}
	}
}
