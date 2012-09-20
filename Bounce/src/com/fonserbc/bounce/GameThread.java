package com.fonserbc.bounce;

import com.fonserbc.bounce.utils.FramesPerSecond;
import com.fonserbc.bounce.utils.Timer;
import com.fonserbc.bounce.utils.Vector2f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    
    public static final int MAX_TRAMPOLINES = 3;
	
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
	
	private FramesPerSecond FPS;
	
	private Scene scene;
	
	private Trampoline[] trampolines;
	private int nTramp = 0;
	
	private Character[] characters;
	private int nChar = 0;
	
	private Bitmap characterImage;
	
	public GameThread (SurfaceHolder surfaceHolder, Context context, Handler handler) {
		mSurfaceHolder = surfaceHolder;
		mContext = context;
		mHandler = handler;
		
		mDifficulty = DIFFICULTY_MEDIUM;
		
		timer = new Timer();
		FPS = new FramesPerSecond(50);
		scene = new Scene();
		
		trampolines = new Trampoline[3];
		characters = new Character[3];
		trampolines[nTramp++] = new Trampoline(this);
		characters[nChar++] = new Character(this);
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
    		
    		for (int i = 0; i < nTramp; ++i)
    			trampolines[i].doStart();
    		
    		characterImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.character), mWidth/10, mHeight/10, false);
    		
    		for (int i = 0; i < nChar; ++i)
    			characters[i].doStart(characterImage);
    		
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
	                //FPS.tickStart();
	                
	                synchronized (mSurfaceHolder) {
	                    update();
	                    doDraw(c);
	                }
	            } finally {
	                if (c != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(c);
	                }
	                //FPS.tickEnd();
	            }
	            
	            //long sleepTime = FPS.getSleepTime();
	            long sleepTime = -1;
	            if (sleepTime > 0) {
	            	try {
	            		sleep(sleepTime);
	            	} catch (InterruptedException e) {}
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
		
		for (int i = 0; i < nTramp; ++i) {
			for (int j = 0; j < nChar; ++j) {
				if (trampolines[i].intersectsCharacter(characters[j])) {
					characters[j].pushVel(trampolines[i].getBounce());
				}
			}
		}
		
		for (int i = 0; i < nTramp; ++i)
			trampolines[i].update(deltaTime);
		
		for (int i = 0; i < nChar; ++i)
			characters[i].update(deltaTime);
	}
	
	private void doDraw (Canvas canvas) {
		if (canvas != null) {
			scene.doDraw(canvas);
			
			for (int i = 0; i < nTramp; ++i)
				trampolines[i].doDraw(canvas);
			
			for (int i = 0; i < nChar; ++i)
				characters[i].doDraw(canvas);
			
			//FPS.doDraw(canvas);
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
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
}
