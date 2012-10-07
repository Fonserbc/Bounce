package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Collectible extends Entity {

	private static float LIFE_TIME = 10;
	private static float CHANGE_TIME = 1;
	private static float SPEED = 60f;
	private static float FADE_SPEED = 200;
	
	private float MAX_Y;
	
	public Vector2f pos;
	public Vector2f vel;
	public Vector2f size;
	private float time = 0;
	private float changeTime = 0;
	public boolean dead = false;
	public boolean leaving = false;
	
	private Paint paint;
	
	public Collectible (GameActivity game) {
		this.game = game;
		type = TYPE.Collectible;
		
		MAX_Y = game.mHeight*4/5;
		
		size = new Vector2f(game.mWidth/20, game.mWidth/20);
		
		if (Math.random()*20 > 10) {
			pos = new Vector2f(-size.x, size.y/2 + (float)Math.random()*(game.mHeight*3/4));
			vel = new Vector2f(SPEED, 0);
		}
		else {
			pos = new Vector2f(game.mWidth, size.y/2 + (float)Math.random()*(game.mHeight*3/4));
			vel = new Vector2f(-SPEED, 0);
		}
		
		paint = new Paint();
		paint.setColor(Color.YELLOW);
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		
		if (!dead) {		
			pos.x += vel.x*deltaTime;
			pos.y += vel.y*deltaTime;
			
			if ((pos.x < 0 && vel.x < 0) || (pos.x+size.x > game.mWidth && vel.x > 0)) {
				if (time > LIFE_TIME) {
					die();
					leaving = true;
				}
				else {
					vel.x = -vel.x;
				}
			}

			if ((pos.y > MAX_Y && vel.y > 0) || (pos.y < 0 && vel.y < 0)) {
				vel.y = -vel.y;
			}
			
			changeTime += deltaTime;
			
			if (changeTime > CHANGE_TIME) {
				changeTime = 0;
				
				float rand = (float) (Math.random()*4);
				
				if (rand > 2) {
					if (rand > 3) {
						vel.y = (float) (SPEED*Math.random());
						if (rand-3 > 0.5) vel.y = -vel.y;
					}
					else {
						vel.x = (float) (SPEED*Math.random());
						if (rand-2 > 0.5) vel.x = -vel.x;
					}
				}
			}
		}
		else {
			if (!leaving) {
				int alpha = paint.getAlpha();
				alpha -= FADE_SPEED*deltaTime;
				
				if (alpha < 0) {
					alpha = 0;
					destroy();
				}
				
				paint.setAlpha(alpha);
			}
			else {
				pos.x += vel.x*deltaTime;
				pos.y += vel.y*deltaTime;
				
				if (pos.x+size.x < 0 || pos.x > game.mWidth) destroy();
			}
		}
	}

	@Override
	public void doDraw(Canvas c) {
		c.drawCircle(pos.x+size.x/2, pos.y+size.y/2, size.x/2, paint);
	}

	@Override
	public void die() {
		dead = true;
	}
	
	public void destroy() {
		game.notifyDeadEntity(this);
	}

	public boolean collidesCharacter (Character c) {
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
}
