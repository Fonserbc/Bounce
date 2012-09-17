package com.fonserbc.bounce;

import utils.Timer;
import utils.Vector2f;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	
	public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
	
	public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;
	
    public int mMode;
    
    private int mLastMode = -1;
    
    public int mDifficulty;
    
    private int mWidth;
    private int mHeight;
    
	public boolean mRun = false;
	
	public boolean mAlive = true;
	
	public boolean mRestored = false;
	
	private SurfaceHolder mSurfaceHolder;
	private Context mContext;
	private Handler mHandler;
	
	private Timer timer;
	
	/**** PROVISIONAL ****/
	private float time = 0;
	private float frequency = 0.5f;
	private int it = 0;
	private int[] colors;
	private float[] lineBounds;
	private float[] line;
	private float[] lineDir;
	private Paint linePaint;
	private float lineSpeed = 200f;
	/**** PROVISIONAL ****/
	
	public GameThread (SurfaceHolder surfaceHolder, Context context, Handler handler) {
		mSurfaceHolder = surfaceHolder;
		mContext = context;
		mHandler = handler;
		
		mDifficulty = DIFFICULTY_MEDIUM;
		
		timer = new Timer();		
	}
	
	public void setState(int mode) {
		synchronized (mSurfaceHolder) {
           mMode = mode;
           
           if (mMode == STATE_RUNNING) {
        	   mRun = true;
        	   Log.v("BOUNCE", "STATE RUNNING");
           }
           else if (mMode == STATE_PAUSE) {
        	   mRun = false;
        	   Log.v("BOUNCE", "STATE PAUSE");
           }
        }
	}

	public void doStart(int lastState) {
        synchronized (mSurfaceHolder) {        	
        	mWidth = mSurfaceHolder.getSurfaceFrame().width();
    		mHeight = mSurfaceHolder.getSurfaceFrame().height();
    		
        	colors = new int[3];
        	colors[0] = Color.RED;
    		colors[1] = Color.GREEN;
    		colors[2] = Color.BLUE;
    		
    		line = new float[4];
    		line[0] = mWidth/4;
    		line[1] = 0;
    		line[2] = 3*mWidth/4;
    		line[3] = mHeight;
    		
    		lineDir = new float[4];
    		lineDir[0] = lineDir [3] = -1;
    		lineDir[1] = lineDir [2] = 1;
    		
    		lineBounds = new float[2];
    		lineBounds[0] = mWidth;
    		lineBounds[1] = mHeight;
    		
    		linePaint = new Paint();
    		linePaint.setColor(Color.BLACK);
    		linePaint.setStrokeWidth(3);
    		
    		setState(lastState);
        }
	}

	@Override
    public void run() {
		while (mAlive) {
			
			if (!mRun) try { sleep(100); } catch (InterruptedException ie) {}
			
	        while (mRun) {
	            Canvas c = null;
	            try {
	                c = mSurfaceHolder.lockCanvas(null);
	                synchronized (mSurfaceHolder) {
	                    update();
	                    doDraw(c);
	                }
	            } finally {
	                if (c != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(c);
	                }
	            }
	        }
		}
    }
	
	public void pause() {
        synchronized (mSurfaceHolder) {
        	mLastMode = mMode;
        	setState(STATE_PAUSE);
        }
    }
	
	public void unPause() {
        if (mLastMode < 0) setState(mLastMode);
    }
	
	@Deprecated
	public void setRunning (boolean run) {
		mRun = run;
	}
	
	private void update() {
		float deltaTime = timer.tick();
		time += deltaTime;
		
		if (time > frequency) {
			it = (it+1)%colors.length;
			time = 0;
		}
		
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
	
	private void doDraw (Canvas canvas) {
		if (canvas != null) {
			canvas.drawColor(colors[it]);
			
			canvas.drawLine(line[0], line[1], line[2], line[3], linePaint);
		}
	}
	
	public Bundle saveState(Bundle map) {
        synchronized (mSurfaceHolder) {
        	if (map != null) {
        		map.putInt("mDifficulty", mDifficulty);
        		
        		Log.v("BOUNCE", "saved State");
        	}
        }
        return map;
	}

	
	/**
	 * Here you should just save IMPORTANT values that you don't want to loose if the app gets killed by the OS
	 * 
	 * mDifficulty is just an example
	 */
	public void restoreState(Bundle savedState) {
		synchronized (mSurfaceHolder) {			
			Log.v("BOUNCE", "restoring State");
			
			mDifficulty = savedState.getInt("mDifficulty");
			
			mRestored = true;
        }
	}

	public void setAlive(boolean b) {
		mAlive = b;
		Log.v("BOUNCE", "Set Alive: "+b);
	}

	public int getLastState() {
		return mLastMode;
	}
}
