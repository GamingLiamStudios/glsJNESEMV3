package com.GLS.NESEJ;

import java.awt.Color;

public class Sprite {
	public Color[] data;
	private int w, h;
	public Sprite(int w, int h) {
		data = new Color[w*h];
		this.w = w;
		this.h = h;
	}
	public Color getPixel(int r, int c) {
		return data[(c*w)+r];
	}
	public boolean setPixel(int x, int y, Color col) {
		if (x >= 0 && x < w && y >= 0 && y < h) {
			data[y*w + x] = col;
			return true;
		} else return false;
	}
	public int getWidth() {
		return w;
	}
	public int getHeight() {
		return h;
	}
}
