package com.jameslow;

import java.io.*;
import java.net.MalformedURLException;

public class FileUtils {
	public static FilenameFilter getHiddenDirFilter() {
		return new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				File file = new File(arg0 + Main.OS().fileSeparator() + arg1);
				return file.isFile() && !file.isHidden();
			}
		};
	}
	public static javax.swing.filechooser.FileFilter getExtFileFilter(final String[] exts,final String description) {
		return new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				if(f.isDirectory()) {
					return true;
				} else {
					String ext = getExt(f.getAbsolutePath());
					for(int i=0; i<exts.length; i++) {
						if (exts[i].compareTo(ext) == 0) {
							return true;
						}
					}
					return false;
				}
			}
			public String getDescription() {
				String ext = "";
				for(int i=0; i<exts.length; i++) {
					if (i == 0) {
						ext = exts[i];
					} else {
						ext = ext + ", " + exts[i];
					}
				}
				return description + " (" + ext + ")"; 
			}
		};
	}
	public static String getExt(String filename) {
		int pos = filename.lastIndexOf(".");
		if (pos >= 0 && pos < filename.length()) {
			return filename.substring(pos+1, filename.length());
		} else {
			return "";
		}
	}
	public static String[] ListFiles(String directory) {
		File dir = new File(directory);
		if (dir.isDirectory()) {
			return dir.list();
		}
		return new String[0];
	}
	public static boolean IsEmpty(String directory) {
		String[] list = ListFiles(directory);
		for (int i=0; i<list.length; i++) {
			File file = new File(list[i]);
			if (file.isFile()) {
				return false;
			}
		}
		return true;
	}
	public static void WriteStream(InputStream in, String filename) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			WriteStream(in, out);
			out.close();
		} catch (FileNotFoundException e) {
			//from new FileOutputStream(filename);
			Main.Logger().severe(e.getMessage());
		} catch (IOException e) {
			//from out.close();
			Main.Logger().severe(e.getMessage());
		}
	}
	public static void WriteStream(InputStream in, OutputStream out) {
        try {
        	int c;
			while ((c = in.read()) != -1) {
			    out.write(c);
			}
		} catch (IOException e) {
			Main.Logger().severe(e.getMessage());
		}
	}
	/**
	 * Get file folder of a file, returns the parent file if and only if the file is not a folder already
	 * @param file
	 * @return
	 * @throws MalformedURLException 
	 * @throws  
	 */
	public static String GetFolder(String file) {
		File dir = new File(file);
		if (dir.isFile()) {
			dir = dir.getParentFile();
		}
		return dir.getAbsolutePath();
	}
}