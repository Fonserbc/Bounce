package utils;

public class Timer {
	
	private static final float DEF_MAXSTEP = 0.05f;
	private float gameTime = 0;
	public float maxStep = 0.05f;
	private long wallLastTimeStamp = 0;
	private long lastTickMs = 0;
	
	public Timer() {
	}
	
	public float tick() {
		long wallCurrent = System.currentTimeMillis();
		lastTickMs = (wallCurrent - wallLastTimeStamp);
		float wallDelta = lastTickMs/1000f;
		wallLastTimeStamp = wallCurrent;
		
		float gameDelta = Math.min(wallDelta, maxStep);
		gameTime += gameDelta;
		return gameDelta;
	}

	public long getLastTickMs() {
		return lastTickMs;
	}
	
	public float getLastTick() {
		return Math.min(lastTickMs/1000f, maxStep);
	}

	public float falseTick() {
		return (System.currentTimeMillis() - wallLastTimeStamp)/1000f;
	}
	
	public long falseTickMs() {
		return (System.currentTimeMillis() - wallLastTimeStamp);
	}

	public void setReal(boolean real) {
		if (real) maxStep = 100000f;
		else maxStep = DEF_MAXSTEP;
	}
	
	public float getGameTime() {
		return gameTime;
	}
}
