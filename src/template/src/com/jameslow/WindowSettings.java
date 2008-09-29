package com.jameslow;

public class WindowSettings {
	private int width, height, left, top;
	private boolean visible;
	
	public WindowSettings(int width, int height, int left, int top, boolean visible) {
		this.width = width;
		this.height = height;
		this.left = left;
		this.top = top;
		this.visible = visible;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getLeft() {
		return left;
	}
	public int getTop() {
		return top;
	}
	public boolean getVisible() {
		return visible;
	}
	public void setWdith(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
