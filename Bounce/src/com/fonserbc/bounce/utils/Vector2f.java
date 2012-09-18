package com.fonserbc.bounce.utils;

import android.util.FloatMath;

public class Vector2f {
	public float x;
	public float y;
	
	public Vector2f () {
		x = 0;
		y = 0;
	}
	
	public Vector2f (float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float magnitude() {
		return FloatMath.sqrt((x*x + y*y));
	}
	
	public Vector2f normalized() {
		float magnitude = magnitude();
		return new Vector2f(x/magnitude, y/magnitude);
	}

	public float dotProduct(Vector2f v) {
		return x*v.x + y*v.y;
	}
	
	public Vector2f scale(float f) {
		return new Vector2f (x*f, y*f);
	}

	public Vector2f minus(Vector2f v) {
		return new Vector2f (x - v.x, y - v.y);
	}

	public Vector2f plus(Vector2f v) {
		return new Vector2f (x + v.x, y + v.y);
	}

	public Vector2f inversed() {
		return new Vector2f (-x, -y);
	}
}