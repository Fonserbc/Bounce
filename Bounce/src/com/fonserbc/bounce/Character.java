package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.*;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Character {
	private static final float DEF_GX = 0f;
	private static final float DEF_GY = 300f;
	
	private static final float MAX_VX = 1000f;
	private static final float MAX_VY = 1000f;
	
	private Vector2f gravity;
	
	private Vector2f velocity;
	private Vector2f maxVel;
	
	Bitmap image;
	
	GameThread game;
	
	Vector2f pos, size;
	
	public Character (GameThread game) {
		this.game = game;
	}
	
	public void doStart (Bitmap image) {
		this.image = image;
		
		gravity = new Vector2f(DEF_GX, DEF_GY);
		
		velocity = new Vector2f(30, 0);
		
		pos = new Vector2f(game.getWidth()/2 - image.getWidth()/2, 0);
		size = new Vector2f(image.getWidth(), image.getHeight());
	}
	
	public void update (float deltaTime) {
		float squaredDeltaTime = deltaTime*deltaTime;
		
		pos.x += velocity.x*deltaTime + 0.5*gravity.x*squaredDeltaTime;
		pos.y += velocity.y*deltaTime + 0.5*gravity.y*squaredDeltaTime;
		
		if (velocity.x < MAX_VX) velocity.x += gravity.x*deltaTime;
		else velocity.x = MAX_VX;
		if (velocity.y < MAX_VY) velocity.y += gravity.y*deltaTime;
		else velocity.y = MAX_VY;
		
		if (pos.x < 0) {
			if (velocity.x < 0) velocity.x *= -1;
		}
		else if (pos.x+image.getWidth() > game.getWidth()) {
			if (velocity.x > 0) velocity.x *= -1;
		}
		
		if (pos.y < 0) {
			if (velocity.y < 0) velocity.y *= -1;
		}
		else if (pos.y > game.getHeight()) {
			if (velocity.y > 0) pos.y = 0;
		}
	}
	
	public void doDraw (Canvas canvas) {
		canvas.drawBitmap(image, pos.x, pos.y, null);
	}
}
