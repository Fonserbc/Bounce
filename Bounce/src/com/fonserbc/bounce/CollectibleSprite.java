package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CollectibleSprite {

	private static final int AUG = 4;
	private static final float FREQUENCY = 0.25f;

	Collectible col;
	
	Bitmap sprite;
		int iWidth, iHeight;
	
	int tilingX, tilingY;
	
	float time = 0;
	
	Rect source;
	
	int X, Y;
	
	public CollectibleSprite(Collectible col, Bitmap spriteSheet, int tilingX, int tilingY) {
		sprite = spriteSheet;
		this.col = col;
		
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
	
	public void doDraw(Canvas canvas, Vector2f pos, Paint paint) {
		int x = (int)pos.x;
		int y = (int)pos.y;
		canvas.drawBitmap(sprite, source, new Rect(x, y, x+source.width(), y+source.height()), paint);
	}
	
	public void update(float deltaTime, Vector2f v) {
		time += deltaTime;
		
		if (time > FREQUENCY) {
			time = 0;
			X = (X+1)%2;
		}
		
		if (v.x > 0) Y = 0;
		else Y = 1;
		
		source = new Rect(X*iWidth + X*AUG, Y*iHeight + Y*AUG, X*iWidth + X*AUG + iWidth, Y*iHeight + Y*AUG + iHeight);
	}
}
