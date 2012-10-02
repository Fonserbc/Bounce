package com.fonserbc.bounce;

import android.graphics.Canvas;

public abstract class Entity {
	public static enum TYPE {
		Trampoline,
		Character,
		Collectible
	}
	
	public TYPE type;
	
	protected GameActivity game;
	
	public abstract void update (float deltaTime);
	
	public abstract void doDraw(Canvas c);
	
	public abstract void die();
}
