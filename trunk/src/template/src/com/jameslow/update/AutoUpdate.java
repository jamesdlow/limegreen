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

//This is a big hack because I'm trying to get this into a single java class
//For some reason I thought that would be easier to copy from the jar to a temp location to launch, and it might be, we'll see
public class AutoUpdate extends Thread implements ActionListener, ItemListener {
	//Common
	private static final String s = System.getProperty("file.separator");
	private static final String tempdir = System.getProperty("java.io.tmpdir");
	private static final String fullname = AutoUpdate.class.getName();
	private static final int lastdot = fullname.lastIndexOf(".");
	private static final String pack = fullname.substring(0, lastdot);
	private static final String classname = fullname.substring(fullname.lastIndexOf(".")+1);
	private static final String CLASS = "class";
	
	//Stuff associated with checking for new versions
	private JFrame window;
	private JPanel checkpanel;
		private JCheckBox checkforupdatesbutton;
		private JCheckBox includeexperimentalbutton;
		private JCheckBox includeminorbutton;
		private JButton updatebutton;
		private JButton cancelbutton;
	private JPanel installpanel;
		private JProgressBar progressBar;
		private JButton installbutton;
	private String download;
	private String appname;
	private File downloadfile;
	private boolean checkforupdates;
	private boolean includeexperimental;
	private boolean includeminor;
	private ActionListener updatelistener;
	private ActionListener cancellistener;
	
	//Stuff associated with downloading and extracting update
	public static final String AUTOUPDATE = "AutoUpdate";
	private static final int ARG_COUNT = 3;
	private static final String USAGE = "usage: " + fullname + " download_file copy_target launch_app [delete_first]";
	
	//We're at the checking for updates stage
	//Consuming classes should implement an ActionListener for when the auto update proceeds and cancelled
	//Consuming classes can check if the user has checked check for updates in the future by calling getCheckForUpdates(), this can then be saved as a setting
	public void checkForUpdates(String appname, String url, String version, int build,
				boolean allowminor, boolean allowexperimental, boolean allowautoupdate,
				boolean minor, boolean experimental, boolean autoupdate,
				ActionListener update, ActionListener cancel) {
		if (allowautoupdate && autoupdate) {
			checkforupdates = autoupdate;
			includeexperimental = experimental;
			includeminor = minor;
			cancellistener = cancel;
			updatelistener = update;
			this.appname = appname;
			//try {
				//Get and parse XML
				//String versionxml = new String(getHttpContent(url));
				//TODO: parse xml / compare versions / get download file
				//TODO: This needs to account for if we happen to be in a jar on any of the platforms in terms of search for -other- or -win- or -mac file
				boolean needtoupdate = true;
				if (allowexperimental && experimental) {
					
				}
				if (allowminor && minor) {
					
				}
				if (needtoupdate) {
					download = "http://jameslow.com/content/software/Limelight-mac-0.3.zip";
					
					//Construct window
					int width = 550;
					int height = 350;
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					Point center = ge.getCenterPoint();
					window = new JFrame(AUTOUPDATE + " - " + appname);
						Container pane = window.getContentPane();
						JTextArea box = new JTextArea();
							box.setEnabled(false);
						pane.add(box,BorderLayout.CENTER);
						checkpanel = new JPanel();
							checkforupdatesbutton = new JCheckBox("Check for updates?",autoupdate);
								checkforupdatesbutton.addItemListener(this);
							checkpanel.add(checkforupdatesbutton);
							if (allowexperimental) {
								includeexperimentalbutton = new JCheckBox("Include Experimental?",experimental);
									includeexperimentalbutton.addItemListener(this);
								checkpanel.add(includeexperimentalbutton);
							}
							if (allowminor) {
								includeminorbutton = new JCheckBox("Include Minor?",minor);
									includeminorbutton.addItemListener(this);
								checkpanel.add(includeminorbutton);
							}
							cancelbutton = new JButton("Cancel");
								cancelbutton.addActionListener(this);
							checkpanel.add(cancelbutton);
							updatebutton = new JButton("Update");
								updatebutton.addActionListener(this);
							checkpanel.add(updatebutton);
						pane.add(checkpanel,BorderLayout.SOUTH);
						installpanel = new JPanel();
							progressBar = new JProgressBar();
							installpanel.add(progressBar,BorderLayout.CENTER);
							installbutton = new JButton("Install and relaunch");
								installbutton.addActionListener(this);
								installbutton.setEnabled(false);
							installpanel.add(installbutton, BorderLayout.WEST);
					window.setBounds((int) (center.getX() - width/2),(int) (center.getY() - height/2), width, height);
					window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					window.show();
				} else {
					//TODO: Not sure if we need to make sure number is correct
					cancellistener.actionPerformed(new ActionEvent(this,0,""));
				}
			//} catch (IOException e) {
				//Not connected to the internet or can't contact webpage, just go on
			//}
		}
	}
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == checkforupdatesbutton) {
			checkforupdates = e.getStateChange() == ItemEvent.SELECTED; 
		} else if (source == includeexperimentalbutton) {
			includeexperimental = e.getStateChange() == ItemEvent.SELECTED;
		} else if (source == includeminorbutton) {
			includeminor = e.getStateChange() == ItemEvent.SELECTED;
		}
	}
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)e.getSource();
		if (source == installbutton) {
			installAndRelaunch(e);
		} else if (source == updatebutton) {
			update();
		} else if (source == cancelbutton) {
			cancel(e);
		}
	}
	private void hideWindow() {
		window.hide();
	}
	public boolean getCheckForUpdates() {
		return checkforupdates;
	}
	public boolean getIncludeExperiemental() {
		return includeexperimental;
	}
	public boolean getIncludeMinor() {
		return includeminor;
	}
	private void cancel(ActionEvent e) {
		hideWindow();
		cancellistener.actionPerformed(e);
	}
	private void update() {
		try {
			Container pane = window.getContentPane();
			pane.remove(checkpanel);
			pane.add(installpanel,BorderLayout.SOUTH);
			this.start();
		} catch (Exception e) {
			Error("Could not launch update program: " + e.getMessage());
		}
	}
	public void run() {
		downloadUpdate();
    }
	
	private void downloadUpdate() {
		final String couldnot = "Autoupdate could not be completed.";
		try {
			int i = 0;
			//while file exists work out name to download to incrementing suffix
			while ((downloadfile = new File(tempdir + s + appname + AUTOUPDATE + i + ".zip")).exists()) {
				i++;
			}
			if (downloadfile.createNewFile()) {
				getHttpContent(download, new FileOutputStream(downloadfile));
				//TODO: check file integrity against size / MD5
				installbutton.setEnabled(true);
			} else {
				Error(couldnot + " Could not create temporary file.");
			}
		} catch (IOException e) {
			Error(couldnot);
		}
	}
	private void installAndRelaunch(ActionEvent e) {
		try {
			final String p = " ";
			hideWindow();
			updatelistener.actionPerformed(e);
			File dir = new File(tempdir + s + pack.replaceAll("\\.", s));
			dir.mkdirs();
			String classfile = dir.toString()+s+classname;
			File file = new File(classfile+"."+CLASS);
			InputStream is = AutoUpdate.class.getResourceAsStream("/"+fullname.replaceAll("\\.", "/")+ "."+CLASS);
			copyInputStream(is,new FileOutputStream(file));
			//TODO: Work these out from system.
			String deploy = "/Users/James/Documents/Programs/James/Eclipse/Limelight/build/dist/Limelight.app";
			String launch = "Limelight.App";
			Runtime.getRuntime().exec("java "+fullname+p+downloadfile.getAbsolutePath()+p+deploy+p+launch,null,new File(tempdir)).waitFor();
		} catch (Exception ex) {
			Error(ex.getMessage());
		}
		//Force quit here, then updatelistener only needs to handle things like saving settings
		System.exit(0);
	}
	private void getHttpContent(String url, OutputStream out) throws IOException {
		URL u = new URL(url); 
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET"); 
		huc.connect(); 
		int code = huc.getResponseCode();
		int length = huc.getContentLength();
		copyInputStream(huc.getInputStream(), out, length);
		out.close();
		huc.disconnect();
	}
	private char[] getHttpContent(String url) throws IOException {
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
	private void copyInputStream(InputStream in, OutputStream out, int length) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		progressBar.setMaximum(length);
		int total = 0;
		while((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
			total = total + len;
			progressBar.setValue(total);
			progressBar.setStringPainted(true);
		}
		in.close();
		out.close();
	}	
	
	//We're at the download and extracting stage
	public static void main(String[] args) {
		try {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name",AUTOUPDATE);
			System.setProperty("apple.laf.useScreenMenuBar","true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (args.length >= ARG_COUNT) {
			String filename = args[0];
			String copylocation = args[1];
			String launchapp = args[2];
			boolean deletefirst = false;
			int mode = 0;
			if (args.length > ARG_COUNT) {
				try {
					mode = Integer.parseInt(args[3]);
					deletefirst = mode > 0;
				} catch (NumberFormatException e) {
					Error(USAGE + " - delete_first must be a number");
				}
			}
			File downloadfile = new File(filename);
			downloadfile.deleteOnExit();
			unzip(downloadfile,copylocation,deletefirst);
			downloadfile.delete();
			if (!launchApplication(copylocation,launchapp)) {
				Error("Could not relaunch application.");
			}

		} else {
			Error(USAGE);
		}
		System.exit(0);
	}
	private static boolean launchApplication(String path, String application) {
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
	private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		int total = 0;
		while((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}
	
	//Common
	public static void Error(String msg) {
		System.out.println(msg);
	}
}
