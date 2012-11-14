package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.*;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Character extends Entity {
	private static final float DEF_GX = 0f;
	private static final float DEF_GY = 250f;
	
	private static final float MAX_VX = 750f;
	private static final float MAX_VY = 350f;
	
	private static final float LATERAL_BOUNCE_X = 150f;
	private static final float LATERAL_BOUNCE_Y = 80f;
	
	private static final float WALL_LOSS = 0.8f;
	
	private Vector2f gravity;
	
	Vector2f velocity;
	Vector2f maxVel;
	
	CharacterSprite sprite;
		Matrix flipRightMatrix;
	
	Vector2f pos, size;
	
	public Character (GameActivity game) {
		this.game = game;
		type = TYPE.Character;
	}
	
	public Character (GameActivity game, Bitmap spriteSheet) {
		this.game = game;
		type = TYPE.Character;
		doStart(spriteSheet);
	}
	
	public void doStart (Bitmap spriteSheet) {
		sprite = new CharacterSprite(this, spriteSheet, 5, 2);
		
		maxVel = new Vector2f(MAX_VX/480*game.mWidth, MAX_VY/800*game.mHeight);
		
		gravity = new Vector2f(DEF_GX, DEF_GY);
		
		velocity = new Vector2f(0, 0);
		
		pos = new Vector2f(game.getWidth()/2 - sprite.getWidth()/2, -sprite.getHeight());
		size = new Vector2f(sprite.getWidth(), sprite.getHeight());
	}
	
	public void update (float deltaTime) {
		float squaredDeltaTime = deltaTime*deltaTime;
		
		pos.x += velocity.x*deltaTime + 0.5*gravity.x*squaredDeltaTime;
		pos.y += velocity.y*deltaTime + 0.5*gravity.y*squaredDeltaTime;
		
		if (velocity.x < maxVel.x) velocity.x += gravity.x*deltaTime;
		else velocity.x = maxVel.x;
		if (velocity.y < maxVel.y) velocity.y += gravity.y*deltaTime;
		else velocity.y = maxVel.y;
		
		if (pos.x < 0) {
			if (velocity.x < 0) 
				if (velocity.y > 0)	velocity.x *= -WALL_LOSS;
				else {
					velocity.x = LATERAL_BOUNCE_X;
					velocity.y = -Math.max(Math.abs(velocity.y)*0.6f, LATERAL_BOUNCE_Y);
					sprite.setGrab();
				}
		}
		else if (pos.x+sprite.getWidth() > game.getWidth()) {
			if (velocity.x > 0) 
				if (velocity.y > 0) velocity.x *= -WALL_LOSS;
				else {
					velocity.x = -LATERAL_BOUNCE_X;
					velocity.y = -Math.max(Math.abs(velocity.y)*0.6f, LATERAL_BOUNCE_Y);
					sprite.setGrab();
				}
		}
		
		if (pos.y < -game.getHeight()) {
			if (velocity.y < 0) velocity.y = 0;
		}
		else if (pos.y > game.getHeight()) {
			if (velocity.y > 0) die();
		}
		
		sprite.update(deltaTime, velocity);
	}
	
	public void doDraw (Canvas canvas) {
		sprite.doDraw(canvas, pos);
		
		Paint paint = new Paint(Color.WHITE);
		if (pos.y < -sprite.getHeight()) {
			canvas.drawRect(pos.x, 0, pos.x+sprite.getWidth(), 10, paint);
		}
	}

	public float getWidht() {
		return sprite.getWidth();
	}
	
	public float getHeight() {
		return sprite.getHeight();
	}

	public void pushVel(Vector2f bounce) {
		velocity = bounce;
		sprite.setJump();
		game.playSound(R.raw.jump);
	}

	@Override
	public void die() {
		game.notifyDeadEntity(this);
		game.playSound(R.raw.die);
	}
}
