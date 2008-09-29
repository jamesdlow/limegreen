package com.jameslow;

import java.awt.*;
import javax.swing.*;

public class AboutPanel extends JPanel {
	private static final String BR = "<br>";
	public AboutPanel() {
		super();
		setFont(new Font("Arial", Font.PLAIN, 10));
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		Dimension size = new Dimension(200,150); 
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		//Box.createRigidArea(size)
		Settings settings = Main.Settings();
		String string = new String("<html>");
		string = string + "<h2>" + settings.getTitle() + "</h2>";
		string = string + BR + settings.getAboutText() + BR;
		string = string + BR + "Version: " + settings.getVersion();
		string = string + BR + "Build: " + settings.getBuild();
		string = string + BR + "Date: " + settings.getBuildDate();
		JLabel text = new JLabel(string);
		add(text);
		Link link = new Link(settings.getAboutURL(),settings.getAboutURL());
		add(link);
	}
}
