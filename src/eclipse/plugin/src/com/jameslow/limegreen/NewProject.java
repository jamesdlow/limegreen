package com.jameslow.limegreen;

import java.io.*;
import java.net.*;
import java.util.jar.Manifest;

import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.*;
/*
 * http://www.ibm.com/developerworks/opensource/library/os-eclipse-custwiz/
 * http://cvalcarcel.wordpress.com/2009/07/11/writing-an-eclipse-plug-in-part-2-creating-a-custom-project-in-eclipse-adding-to-the-new-project-wizard/
 * http://cvalcarcel.wordpress.com/2009/07/26/writing-an-eclipse-plug-in-part-4-create-a-custom-project-in-eclipse-new-project-wizard-the-behavior/#comment-798
 */
public class NewProject extends Wizard implements INewWizard {
	private WizardNewProjectCreationPage _pageOne;
	private String PROJECT_TYPE, PROJECT_NAME, WIZARD_NAME, WIZARD_TITLE, PROJECT_DESC;
	
	public NewProject() {
		PROJECT_TYPE = getManifestName();
		PROJECT_NAME = PROJECT_TYPE+" Project";
		WIZARD_NAME = PROJECT_NAME+" Wizard";
		WIZARD_TITLE = "New "+PROJECT_NAME;
		PROJECT_DESC = "Create a "+PROJECT_NAME;
		setWindowTitle(WIZARD_TITLE);
	}
	
	public String getManifestName() {
		String name = "Custom";
		try {
			Manifest manifest = new Manifest(getClass().getResourceAsStream("/META-INF/MANIFEST.MF"));
			name = manifest.getMainAttributes().getValue("Bundle-Name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public String getTemplatePath() {
		return "/template";
	}
	
	public File getTemplateDir() {
		//https://forum.hibernate.org/viewtopic.php?p=2264274
		URL url = getClass().getResource(getTemplatePath());
		if ("file".compareTo(url.getProtocol()) != 0) {
			try {
				url = org.eclipse.core.runtime.FileLocator.resolve(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File file = new File(url.getPath());
		return file;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) { }
	
	public void addPages() {
		super.addPages();
		_pageOne = new WizardNewProjectCreationPage(WIZARD_NAME);
		_pageOne.setTitle(PROJECT_NAME);
		_pageOne.setDescription(PROJECT_DESC);
		addPage(_pageOne);
	}
	
	public void createProject(String projectName, URI location) {
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!newProject.exists()) {
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				projectLocation = null;
			}
			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc, null);
				if (!newProject.isOpen()) {
					newProject.open(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean performFinish() {
		File template = getTemplateDir();
		URI location = null;
		if (!_pageOne.useDefaults()) {
			 location = _pageOne.getLocationURI();
		}
		createProject(_pageOne.getProjectName(),location);
		if (location == null) {
			location = _pageOne.getLocationURI();
		}
		try {
			copyFiles(template,new File(location));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void copyFiles(File src, File dest) throws IOException {
		//Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath()+".");
		} else if (!src.canRead()) { //check to ensure we have rights to the source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath()+".");
		}
		//is this a directory copy?
		if (src.isDirectory()) 	{
			if (!dest.exists()) { //does the destination already exist?
				//if not we need to make it exist if possible (note this is mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
				}
			}
			//get a listing of files...
			String list[] = src.list();
			//copy all the files in the list.
			for (int i = 0; i < list.length; i++)
			{
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				copyFiles(src1 , dest1);
			}
		} else { 
			//This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
			int bytesRead;
			try {
				//open the files for input and output
				fin =  new FileInputStream(src);
				fout = new FileOutputStream (dest);
				//while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer,0,bytesRead);
				}
			} catch (IOException e) { //Error copying file... 
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + 
							src.getAbsolutePath() + "to" + dest.getAbsolutePath()+".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { //Ensure that the files are closed (if they were open).
				if (fin != null) { fin.close(); }
				if (fout != null) { fout.close(); }
			}
		}
	}
}