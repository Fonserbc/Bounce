package com.fonserbc.bounce;

import utils.Timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
    
    public int mDifficulty;
    
	public boolean mRun = false;
	
	private SurfaceHolder mSurfaceHolder;
	private Context mContext;
	private Handler mHandler;
	
	private Timer timer;
	
	/**** PROVISIONAL ****/
	private float time = 0;
	private float frequency = 0.5f;
	private int i = 0;
	private int[] colors;
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
        }
	}

	public void doStart() {
        synchronized (mSurfaceHolder) {
        	setState(STATE_RUNNING);
        	
        	colors = new int[3];
        	colors[0] = Color.RED;
    		colors[1] = Color.GREEN;
    		colors[2] = Color.BLUE;
        }
	}

	@Override
    public void run() {
        while (mRun) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    if (mMode == STATE_RUNNING) {
                    	update();
                    	doDraw(c);
                    }
                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
	
	public void pause() {
        synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }
	
	public void unPause() {
        setState(STATE_RUNNING);
    }
	
	public void setRunning (boolean run) {
		mRun = run;
	}
	
	private void update() {
		time += timer.tick();
		
		if (time > frequency) {
			i = (i+1)%colors.length;
			time = 0;
		}
	}
	
	private void doDraw (Canvas canvas) {
		canvas.drawColor(colors[i]);
	}
	
	public Bundle saveState(Bundle map) {
        synchronized (mSurfaceHolder) {
        	//TODO
        }
        return map;
	}
}
