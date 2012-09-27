package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.Vector2f;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;

public class Trampoline {

	private static final float EXTRA_FACTOR = 0.3f;
	private static final int NUM_COLORS = 6;
	private static final float COLOR_SPEED = 1f;
	
	private float MIN_LENGTH;
	private float BOUNCE_FORCE;
	
	private GameActivity game;
	
	private boolean beingBuild = false;
	
	private float[] lineBounds;
	private float[] line;
	private Paint linePaint;
	private LinearGradient gradient;
		private int[] gradientColors;
		private float[] gradientPos;
	
	public Trampoline (GameActivity game, float minX, float minY, float maxX, float maxY, boolean beingBuild) {
		this.game = game;
		this.beingBuild = beingBuild;
		
		MIN_LENGTH = game.getWidth()/10;
		BOUNCE_FORCE = game.getHeight()*0.8f;
		
		line = new float[4];
		line[0] = minX;
		line[1] = minY;
		line[2] = maxX;
		line[3] = maxY;
		
		lineBounds = new float[2];
		lineBounds[0] = game.getWidth();
		lineBounds[1] = game.getHeight();
		
		linePaint = new Paint();
		linePaint.setStrokeWidth(5);
		
		gradientColors = new int[NUM_COLORS];
			gradientColors[0] = Color.YELLOW;
			gradientColors[1] = Color.RED;
			gradientColors[2] = Color.MAGENTA;
			gradientColors[3] = Color.BLUE;
			gradientColors[4] = Color.CYAN;
			gradientColors[5] = Color.GREEN;
		
		gradientPos = new float[NUM_COLORS];
			gradientPos[0] = 0f;
		for (int i = 1; i < NUM_COLORS; ++i)
			gradientPos[i] = i/(float)(NUM_COLORS - 1);
		
		for (int i = 0; i < NUM_COLORS; ++i)
			Log.v("BOUNCE", ""+gradientPos[i]);
	}
	
	public void update(float deltaTime) {
		/*for (int i = 0; i < NUM_COLORS; ++i) {
			gradientPos[i] += COLOR_SPEED*deltaTime;
			if (gradientPos[i] > 1) gradientPos[i] -= 1; 
		}*/
	}
	
	public void doDraw(Canvas canvas) {
		gradient = new LinearGradient(line[0], line[1], line[2], line[3], gradientColors, gradientPos, Shader.TileMode.REPEAT);
		
		linePaint.setShader(gradient);
		canvas.drawLine(line[0], line[1], line[2], line[3], linePaint);
	}
	
	public boolean intersectsCharacter(Character c) {
		if (!beingBuild && c.velocity.y > 0) {
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
		    if(minY < c.pos.y+c.size.y/2) {
		      minY = c.pos.y+c.size.y/2;
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
		Vector2f aux = new Vector2f(line[2]-line[0], line[3]-line[1]);
		float length = aux.magnitude();
		if (length <= 0) {
			die();
			Log.v("BOUNCE", "Trampoline was 0, aborting");
		}
		else if (game.getTrampolinesCont() > game.MAX_TRAMPOLINES) {
			die();
			Log.v("BOUNCE", "Too much trampolines, aborting");
		}
		else {
			if (length < MIN_LENGTH*1.5 && length > 0) {
				Vector2f aux2 = aux.normalized().scale(MIN_LENGTH*1.5f);
				Log.v("BOUNCE", "Trampoline too small: was "+length+" now is "+aux2.magnitude());
				float dx = (aux2.x/2 - aux.x/2);
				float dy = (aux2.y/2 - aux.y/2);
				line[0] -= dx;
				line[1] -= dy;
				line[2] += dx;
				line[3] += dy;
			}
			beingBuild = false;
		}
	}
	
	
}
