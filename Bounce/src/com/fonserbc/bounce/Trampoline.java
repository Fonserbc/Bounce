package com.fonserbc.bounce;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Trampoline {

	private GameThread game;
	
	
	private float[] lineBounds;
	private float[] line;
	private Paint linePaint;
	private float lineSpeed = 200f;
	/**** PROVISIONAL ****/
	private float[] lineDir;
	/**** PROVISIONAL ****/
	
	public Trampoline (GameThread game) {
		this.game = game;
	}
	
	public void doStart() {
		line = new float[4];
		line[0] = game.getWidth()/4;
		line[1] = 0;
		line[2] = 3*game.getWidth()/4;
		line[3] = game.getHeight();
		
		lineBounds = new float[2];
		lineBounds[0] = game.getWidth();
		lineBounds[1] = game.getHeight();
		
		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(3);
		
		/**** PROVISIONAL ****/
		lineDir = new float[4];
		lineDir[0] = lineDir [3] = -1;
		lineDir[1] = lineDir [2] = 1;
		/**** PROVISIONAL ****/
	}
	
	public void update(float deltaTime) {
		for (int i = 0; i < line.length; ++i) {
			line[i] += lineDir[i]*lineSpeed*deltaTime;
			
			if (lineDir[i] < 0) {
				if (line[i] < 0) {
					line[i] = 0;
					lineDir[i] = 1;
				}
			}
			else {
				if (line[i] > lineBounds[i%2]) {
					line[i] = lineBounds[i%2];
					lineDir[i] = -1;
				}
			}
		}
	}
	
	public void doDraw(Canvas canvas) {
		canvas.drawLine(line[0], line[1], line[2], line[3], linePaint);
	}
}
