package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Collectible extends Entity {

	private static float LIFE_TIME = 7;
	private static float BLINK_TIME = 2;
	private static float BLINK_FREQUENCY = 0.1f;
	
	public Vector2f pos;
	public Vector2f size;
	private float time = 0;
	private float blinkTime = 0;
	private boolean seen = true;
	
	private Paint paint;
	
	public Collectible (GameActivity game) {
		this.game = game;
		type = TYPE.Collectible;
		
		size = new Vector2f(game.mWidth/20, game.mWidth/20);
		pos = new Vector2f(size.x/2 + (float)Math.random()*(game.mWidth-size.x), size.y/2 + (float)Math.random()*(game.mHeight*3/4));
		
		paint = new Paint();
		paint.setColor(Color.YELLOW);
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		
		if (time > LIFE_TIME - BLINK_TIME) {
			blinkTime += deltaTime;
			
			if (blinkTime > BLINK_FREQUENCY) {
				blinkTime = 0;
				seen = !seen;
			}
			
			if (time > LIFE_TIME) die();
		}
	}

	@Override
	public void doDraw(Canvas c) {
		if (seen) {
			c.drawCircle(pos.x, pos.y, size.x/2, paint);
		}
	}

	@Override
	public void die() {
		game.notifyDeadEntity(this);		
	}

}
