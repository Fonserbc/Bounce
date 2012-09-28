package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class CharacterSprite {
	
	private static final int AUG = 4;

	Character character;
	
	Bitmap sprite;
		int iWidth, iHeight;
	
	int tilingX, tilingY;
	
	boolean grabbing = false;
		float GRAB_TIME = 0.2f;
	boolean jumping = false;
		float JUMP_TIME = 0.2f;
	
	float FALL_TIME = 0.3f;
		int fallIt = 0;
	
	float time = 0;
	
	Rect source;
	
	int X, Y;
	
	public CharacterSprite(Character character, Bitmap spriteSheet, int tilingX, int tilingY) {
		sprite = spriteSheet;
		this.character = character;
		
		this.tilingX = tilingX;
		this.tilingY = tilingY;
		
		iWidth = (sprite.getWidth() - tilingX*AUG)/tilingX;
		iHeight = (sprite.getHeight() - tilingY*AUG)/tilingY;
		
		X = 0; Y = 0;
		
		source = new Rect(X*iWidth + X*AUG, Y*iHeight + Y*AUG, X*iWidth + X*AUG + iWidth, Y*iHeight + Y*AUG + iHeight);
	}

	public float getWidth() { 
		return source.width();
	}

	public float getHeight() {
		return source.height();
	}

	public void doDraw(Canvas canvas, Vector2f pos) {
		int x = (int)pos.x;
		int y = (int)pos.y;
		canvas.drawBitmap(sprite, source, new Rect(x, y, x+source.width(), y+source.height()), null);
	}
	
	public void update(float deltaTime, Vector2f v) {
		if (v.x < 0) Y = 0;
		else Y = 1;
		
		if (grabbing) {
			X = 4;
			time += deltaTime;
			
			if (time > GRAB_TIME) {
				time = 0;
				grabbing = false;
				X = 2;
			}
		}
		else if (jumping) {
			if (v.y > 0) {
				time = 0;
				jumping = false;
				X = 2;
			}
			else {
				X = 0;
				time += deltaTime;
				
				if (time > JUMP_TIME) {
					time = 0;
					jumping = false;
					X = 2;
				}
			}
		}
		else {			
			if (v.y < 0) {
				time = 0;
				X = 1;
			}
			else {
				time += deltaTime;
				
				if (time > FALL_TIME) {
					time = 0;
					fallIt += 1;
					
					X = 2 + fallIt%2;
				}
			}
		}
		
		source = new Rect(X*iWidth + X*AUG, Y*iHeight + Y*AUG, X*iWidth + X*AUG + iWidth, Y*iHeight + Y*AUG + iHeight);
	}
	
	public void setJump() {
		jumping = true;
		time = 0;
	}
	
	public void setGrab() {
		grabbing = true;
		jumping = false;
		time = 0;
	}
}
