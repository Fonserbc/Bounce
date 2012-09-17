package utils;

import java.text.DecimalFormat;

import android.graphics.Canvas;

public class FramesPerSecond {
	
	public int FPS = 60;
	
	private DecimalFormat df = new DecimalFormat("0.##");
	
	private final static int 	STAT_INTERVAL_FPS = 300;
	
	private final static int	FPS_HISTORY = 10;
	
	private double 	tickStore[];
	private double 	averageFps = 0.0;
	
	private Timer timerFPS;
	
	private String fpsString = "FPS:";
	
	private int it = 0;
	
	
	private long WANNABE_TICK = 1000 / FPS;
	
	private long lastTickStart = 0;
	
	public FramesPerSecond() {
		timerFPS = new Timer();
		
		tickStore = new double[FPS_HISTORY];
		for (int i = 0; i < FPS_HISTORY; i++)
			tickStore[i] = 0.0;
	}
	
	public void tickStart() {
		timerFPS.tick();
		
		lastTickStart = timerFPS.getLastTickMs();
	}
	
	public void tickEnd() {
		
	}
	
	public long getSleepTime() {
		
		return 0;
	}
	
	public void doDraw(Canvas c) {
		
	}
}
