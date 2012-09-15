package com.fonserbc.bounce;

import utils.Timer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	
	public boolean run = false;
	private SurfaceHolder surfaceHolder;
	
	public GameThread(SurfaceHolder holder) {
		surfaceHolder = holder;
	}	
	
	@Override
	public void run() {
		Canvas canvas;
		Timer timer = new Timer();
		float time = 0;
		float frequency = 0.5f;
		
		int[] colors = new int[3];
		colors[0] = Color.RED;
		colors[1] = Color.GREEN;
		colors[2] = Color.BLUE;
		int i = 0;
		
		while (!this.isInterrupted()) {
			if (run) {
				canvas = null;
				
				try {				
					canvas = this.surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						time += timer.tick();
						
						if (time > frequency) {
							i = (i+1)%colors.length;
							time = 0;
						}
						
						canvas.drawColor(colors[i]);
					}
				}
				finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
			else {
				try { Thread.sleep(100); } catch (InterruptedException ie) {}
			}
		}
	}
}
