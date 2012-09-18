package com.fonserbc.bounce.utils;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FramesPerSecond {
	
	public int FPS = 60;
	
	private DecimalFormat df = new DecimalFormat("0.##");
	
	private final static double 	STAT_INTERVAL_FPS = 0.1;
	
	private final static int	FPS_HISTORY = 10;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private double intervalTime;
	
	private Timer timerFPS;
	private Timer intervalTimer;
	
	private String fpsString = "FPS:";
	
	private int it = 0;	
	
	private long WANNABE_TICK;
	
	private long lastTickStart = 0;
	
	private long lastTickEnd = 0;
	
	private long recommendedSleepTime = 0;
	
	public FramesPerSecond(int fps) {
		timerFPS = new Timer();
		intervalTimer = new Timer();
		
		tickStore = new double[FPS_HISTORY];
		for (int i = 0; i < FPS_HISTORY; i++)
			tickStore[i] = 0.0;
		
		FPS = fps;
		WANNABE_TICK = 1000 / FPS;
	}
	
	public void tickStart() {
		tickStore[it] = timerFPS.tick();
		
		it = (it + 1)%FPS_HISTORY;
		
		lastTickStart = timerFPS.getLastTickMs();
	}
	
	public void tickEnd() {
		lastTickEnd = timerFPS.falseTickMs();
		
		recommendedSleepTime = 2*WANNABE_TICK - lastTickStart - lastTickEnd;
	}
	
	public long getSleepTime() {
		
		return recommendedSleepTime;
	}
	
	public void doDraw(Canvas c) {
		intervalTime += intervalTimer.tick();
		
		if (intervalTime > STAT_INTERVAL_FPS) {
			intervalTime = 0.0;
			
			averageFps = 0.0;
			for (int i = 0; i < FPS_HISTORY; ++i)
				averageFps += tickStore[i];
			
			averageFps /= FPS_HISTORY;
			averageFps = 1/averageFps;
			
			fpsString = "FPS: " + df.format(averageFps);
		}
		
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(20);
		c.drawText(fpsString+" / "+FPS, 20, 20, paint);
	}
}
