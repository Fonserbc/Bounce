package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Trampoline {

	private static final float EXTRA_FACTOR = 0.3f;
	private float MIN_LENGTH;
	private float BOUNCE_FORCE;
	
	private GameThread game;
	
	private boolean beingBuild = false;
	
	private float[] lineBounds;
	private float[] line;
	private Paint linePaint;
	
	public Trampoline (GameThread game, float minX, float minY, float maxX, float maxY, boolean beingBuild) {
		this.game = game;
		this.beingBuild = beingBuild;
		
		MIN_LENGTH = game.getWidth()/10;
		BOUNCE_FORCE = game.getHeight();
		
		line = new float[4];
		line[0] = minX;
		line[1] = minY;
		line[2] = maxX;
		line[3] = maxY;
		
		lineBounds = new float[2];
		lineBounds[0] = game.getWidth();
		lineBounds[1] = game.getHeight();
		
		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(3);
	}
	
	public void update(float deltaTime) {
		
	}
	
	public void doDraw(Canvas canvas) {
		canvas.drawLine(line[0], line[1], line[2], line[3], linePaint);
	}
	
	public boolean intersectsCharacter(Character c) {
		if (!beingBuild) {
			float minX = line[0];
		    float maxX = line[2];
	
		    if(line[0] > line[2]) {
		      minX = line[2];
		      maxX = line[0];
		    }
		    // Find the intersection of the segment's and rectangle's x-projections
	
		    if(maxX > c.pos.x+c.size.x) {
		      maxX = c.pos.x+c.size.x;
		    }
		    if(minX < c.pos.x) {
		      minX = c.pos.x;
		    }
		    if(minX > maxX) { // If their projections do not intersect return false
		      return false;
		    }
	
		    // Find corresponding min and max Y for min and max X we found before	    
		    float minY = line[1];
		    float maxY = line[3];
	
		    float dx = line[2] - line[0];
	
		    if(Math.abs(dx) > 0.0000001) {
		      float a = (line[3] - line[1]) / dx;
		      float b = line[1] - a * line[0];
		      minY = a * minX + b;
		      maxY = a * maxX + b;
		    }
	
		    if(minY > maxY) {
		      float tmp = maxY;
		      maxY = minY;
		      minY = tmp;
		    }
		    // Find the intersection of the segment's and rectangle's y-projections
	
		    if(maxY > c.pos.y+c.size.y) {
		      maxY = c.pos.y+c.size.y;
		    }
		    if(minY < c.pos.y) {
		      minY = c.pos.y;
		    }
		    if(minY > maxY) { // If Y-projections do not intersect return false
		      return false;
		    }
	
		    return true;
		}
		else return false;
	}

	public Vector2f getBounce() {
		Vector2f aux = new Vector2f(line[3]-line[1], line[2]-line[0]);
		
		float bounceFactor = MIN_LENGTH/aux.magnitude();

		float invX = 1;
		float invY = 1;
		
		if (aux.x == 0) invY = -1;
		else if (aux.y < 0) invX = -1;
		else invY = -1;
		
		return aux.cartesianProduct(new Vector2f(invX,invY)).normalized().scale(bounceFactor+EXTRA_FACTOR).scale(BOUNCE_FORCE);
	}

	public void die() {
		game.notifyDeadTrampoline(this);		
	}

	public void setMax(float x, float y) {
		line[2] = x;
		line[3] = y;
	}

	public void finish() {
		beingBuild = false;
	}
	
	
}
