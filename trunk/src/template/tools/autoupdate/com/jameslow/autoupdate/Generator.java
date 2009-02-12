package com.jameslow.autoupdate;

public class Generator {
	//General
	private String atomfile;
	private String appname;
	private String apppage;
	private String applinkbase;

	//Version specific
	private String version;
	private String build;
	private String macdmg;
	private String maczip;
	private String winzip;
	private String otherzip;

	//Prompt user with form
	private boolean experimental;
	private String content;
	private String versionpage;

	//Constants
	private static final String XMLNS = "xmlns";
	private static final String XMLNS_LIMEGREEN = "xmlns:limegreen";
	private static final String LIMEGREEN_BUILD = "limegreen:build";
	private static final String LIMEGREEN_VERSION = "limegreen:version";
	private static final String LIMEGREEN_EXPERIMENTAL = "limegreen:experimental";
	private static final String LIMEGREEN_XMLNS = "http://code.google.com/p/limegreen";
	private static final String ATOM_FEED = "feed";
	private static final String ATOM_XMLNS = "http://www.w3.org/2005/Atom";
	private static final String ATOM_ENTRY = "entry";
	private static final String ATOM_ID = "id";
	private static final String ATOM_TITLE = "title";
	private static final String ATOM_CONTENT = "content";
	private static final String ATOM_UPDATED = "updated";
	private static final String ATOM_LINK = "link";
	private static final String ATOM_LINK_HREF = "href";
	private static final String ATOM_LINK_REL = "rel";
	private static final String ATOM_LINK_ENCLOSURE = "enclosure";
	private static final String ATOM_LINK_LENGTH = "length";
	private static final String ATOM_LINK_TYPE = "type";
	private static final String VERSION_SPLIT = "\\.";

	//General
	public void setAtomfile(String atomfile) {
		this.atomfile = atomfile;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public void setApppage(String apppage) {
		this.apppage = apppage;
	}
	public void setApplinkbase(String applinkbase) {
		this.applinkbase = applinkbase;
	}
	
	//Version specific
	public void setVersion(String version) {
		this.version = version;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public void setMacdmg(String macdmg) {
		this.macdmg = macdmg;
	}
	public void setMaczip(String maczip) {
		this.maczip = maczip;
	}
	public void setOtherzip(String otherzip) {
		this.otherzip = otherzip;
	}
	public void setWinzip(String winzip) {
		this.winzip = winzip;
	}
	
	public void createXML() {
		XMLHelper helper = new XMLHelper(atomfile,ATOM_FEED);
		
	}
	public void execute() {
		createXML();
		System.out.println("AutoUpdate code will go here: " + appname);
	}
}