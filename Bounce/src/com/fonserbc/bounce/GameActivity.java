package com.fonserbc.bounce;

import utils.Timer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements SurfaceHolder.Callback, Runnable {
	
	/**
	 * I'm using tag "BOUNCE" for all the Verbose logs
	 * 
	 */

	public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
	
	public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;
	
	private static final int MENU_EXIT = 1;
	
	int mMode;
	int mDifficulty;
	
	GameView gameView;
		SurfaceHolder mSurfaceHolder;
		int mWidth;
		int mHeight;
	
	Thread thread;
		volatile boolean mAlive = true;
		volatile boolean mRun = false;
	
	AlertDialog pauseMenu;
	
	Timer timer;
	
	/** PROVISIONAL **/
	float time = 0;
	float frequency = 0.5f;
	int it = 0;
	int[] colors;
	float[] lineBounds;
	float[] line;
	float[] lineDir;
	Paint linePaint;
	float lineSpeed = 200f;
	/** PROVISIONAL **/
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v("BOUNCE", "Activity onCreate");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.game_view);
        
        gameView = (GameView) findViewById(R.id.game_view);
        
        if (savedInstanceState == null) {
        	Log.v("BOUNCE", "Saved instance was null");
        }
        else {
        	Log.v("BOUNCE", "No saved instance");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EXIT:
            	finish();
            	return true;
            default:
            	return false;
        }
    }
    
    protected void onRestart() {
    	super.onStart();
    	Log.v("BOUNCE", "onRestart");
    }
    
    protected void onStart() {
    	super.onStart();
    	Log.v("BOUNCE", "onStart");
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.v("BOUNCE", "onPause");
    	setState(STATE_PAUSE);
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.v("BOUNCE", "onResume");
    	if (thread != null) setState(STATE_RUNNING);
    	else thread = new Thread(this);
    }
    
	protected void onStop() {
    	super.onStop();
    	Log.v("BOUNCE", "onStop");
    	stopThread();
    }
	
	private void stopThread() {
    	Log.v("BOUNCE", "stopThread");
		mRun = false;
		mAlive = false;
		
		boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("BOUNCE", "on Save Instance State");    
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	Log.v("BOUNCE", "on Restore Instance State");
    	
    	if (savedInstanceState != null) {
    		Log.v("BOUNCE", "restoring state not null");
    	}
    	
    }
    
    
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (gameView.isFocused()) {
				if (mMode == GameThread.STATE_PAUSE) {
					pauseMenu.cancel();
					setState(STATE_RUNNING);
				}
				else {
					setState(STATE_PAUSE);
					popPauseMenu();
				}
			}
			else finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void popPauseMenu() {
		final GameActivity that = this;
		pauseMenu = new AlertDialog.Builder(this)
        .setTitle("Game Paused")
        .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {             
    			setState(STATE_RUNNING);
    		}
        })
        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {             
    			that.finish();
    		}
        })
        .setNeutralButton("Back to menu", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
			}
		})
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				setState(STATE_RUNNING);
			}
		})
        .show();		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public void setState(int state) {
		mMode = state;
		switch(mMode) {
		case STATE_RUNNING:
			mRun = true;
			break;
		case STATE_PAUSE:
			mRun = false;
			break;
		case STATE_LOSE:
			
		case STATE_WIN:
			
		default:
		}
	}

	public void init() {    	    		
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
	}
	
	public void run() {
		while (mAlive) {
			
			if (!mRun) try { Thread.sleep(100); } catch (InterruptedException ie) {}
			
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
	            
	            long sleepTime = 0; //FPS.getSleepTime();
	            if (sleepTime > 0) {
	            	try {
	            		Thread.sleep(sleepTime);
	            	} catch (InterruptedException e) {}
	            }
	        }
		}
	}
	
	public void doDraw (Canvas canvas) {
		if (canvas != null) {
			canvas.drawColor(colors[it]);
			
			canvas.drawLine(line[0], line[1], line[2], line[3], linePaint);
			
			//FPS.doDraw(canvas);
		}
	}
	
	public void update() {
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
}
