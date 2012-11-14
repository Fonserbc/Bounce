package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Collectible extends Entity {

	private static float LIFE_TIME = 10;
	private static float CHANGE_TIME = 1;
	
	private float SPEED = 70f;
	private float MAX_Y;
	private float CRITICAL_DISTANCE = 10;
	
	public Vector2f pos;
	public Vector2f vel;
		private Vector2f escape;
	public Vector2f size;
	private float time = 0;
	private float changeTime = 0;
	public boolean dead = false;
	public boolean leaving = false;
	public boolean inDanger = false;
	
	private CollectibleSprite sprite;
	
	public Collectible (GameActivity game, Bitmap spriteSheet) {
		this.game = game;
		type = TYPE.Collectible;
		
		doStart(spriteSheet);
	}
	
	public void doStart (Bitmap spriteSheet) {
		sprite = new CollectibleSprite(this, spriteSheet, 4, 2);
		
		SPEED *= 2;
		SPEED /= 4-game.mDifficulty;
		
		MAX_Y = game.mHeight*4/5;
		CRITICAL_DISTANCE = game.mWidth/(4-game.mDifficulty);
		
		
		size = new Vector2f(sprite.getWidth(), sprite.getHeight());
		
		if (Math.random()*20 > 10) {
			pos = new Vector2f(-size.x, size.y/2 + (float)Math.random()*(game.mHeight*3/4));
			vel = new Vector2f(SPEED, 0);
		}
		else {
			pos = new Vector2f(game.mWidth, size.y/2 + (float)Math.random()*(game.mHeight*3/4));
			vel = new Vector2f(-SPEED, 0);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		
		if (!dead) {
			if (pos.x > 0 && pos.x+size.x < game.mWidth && inDanger) vel = escape;
				
			pos.x += vel.x*deltaTime;
			pos.y += vel.y*deltaTime;
			
			if ((pos.x < 0 && vel.x < 0) || (pos.x+size.x > game.mWidth && vel.x > 0)) {
				if (time > LIFE_TIME) {
					leaving = true;
					die();
				}
				else {
					if (pos.x < 0 && vel.x < 0) pos.x = 0;
					else if (pos.x+size.x > game.mWidth && vel.x > 0) pos.x = game.mWidth-size.x;
					if (!inDanger) vel.x = -vel.x;
				}
			}

			if ((pos.y > MAX_Y && vel.y > 0) || (pos.y < 0 && vel.y < 0)) {
				if (pos.y > MAX_Y && vel.y > 0) pos.y = MAX_Y;
				else if (pos.y < 0 && vel.y < 0) pos.y = 0;
				if (!inDanger) vel.y = -vel.y;
			}
			
			changeTime += deltaTime;
			
			if (!leaving && changeTime > CHANGE_TIME) {
				changeTime = 0;
				
				float rand = (float) (Math.random()*4);
				
				if (rand > 2) {
					if (rand > 3) {
						vel.y = (float) (SPEED*Math.random()+SPEED/3);
						if (rand-3 > 0.5) vel.y = -vel.y;
					}
					else {
						vel.x = (float) (SPEED*Math.random()+SPEED/3);
						if (rand-2 > 0.5) vel.x = -vel.x;
					}
				}
			}
		}
		else {
			if (leaving) {
				pos.x += vel.x*deltaTime;
				pos.y += vel.y*deltaTime;
				
				if (pos.x+size.x < 0 || pos.x > game.mWidth) destroy();
			}
		}
		
		sprite.update(deltaTime, vel);
		inDanger = false;
	}

	@Override
	public void doDraw(Canvas c) {
		sprite.doDraw(c, pos, null);
	}

	@Override
	public void die() {		
		if (!leaving) {
			game.playSound(R.raw.power_up);
			dead = true;
			sprite.die();
		}
	}
	
	public void destroy() {
		game.notifyDeadEntity(this);
	}

	public boolean collidesCharacter (Character c) {
		if (!dead && !leaving) {
			if (c.pos.minus(pos).magnitude() < CRITICAL_DISTANCE) {
				inDanger = true; 
				escape = pos.minus(c.pos).normalized().scale(SPEED);
			}
			
			Vector2f min = c.pos;
			Vector2f max = new Vector2f(c.pos.x + c.size.x, c.pos.y + c.size.y);
			Vector2f center = new Vector2f(pos.x + size.x/2, pos.y + size.y/2);
			float radius = size.x/2;
			
			Vector2f closestPoint = new Vector2f(center.x, center.y);
			
			if (center.x < min.x) closestPoint.x = min.x;
			else if (center.x > max.x) closestPoint.x = max.x;
			if (center.y < min.y) closestPoint.y = min.y;
			else if( center.y > max.y ) closestPoint.y = max.y;
	
			Vector2f diff = new Vector2f(closestPoint.x - center.x, closestPoint.y - center.y);
			
			if (diff.x * diff.x + diff.y * diff.y > radius * radius) return false;
			else {
				return true;
			}
		}
		else return false;
	}
}
