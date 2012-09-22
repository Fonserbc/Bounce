package com.fonserbc.bounce;

import java.util.ArrayList;

import com.fonserbc.bounce.utils.FramesPerSecond;
import com.fonserbc.bounce.utils.Timer;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements Runnable, SurfaceHolder.Callback {
	
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
    
    public static final int MAX_TRAMPOLINES = 3;
	
	SurfaceView gameView;
		SurfaceHolder mSurfaceHolder;
	    int mWidth;
	    int mHeight;
	    boolean firstStart = true;
	    
	public Thread thread;
		volatile boolean mRun = false;
		volatile boolean mAlive = true;
	    boolean needPauseMenu = false;
		boolean isSurfaceDestroyed = true;
	
	public AlertDialog pauseMenu;
    
	Resources res;
	
	/****************/
	/** GAME STUFF **/
	/****************/
    int mMode;
    int mDifficulty;
	
	private Timer timer;
	
	private FramesPerSecond FPS;
	
	private Scene scene;
	
	private ArrayList<Trampoline> trampolines;
	private int nTramp = 0;
	
	private ArrayList<Character> characters;
	private int nChar = 0;
	
	private ArrayList<Trampoline> deadTrampolines;
	
	private Bitmap characterImage;
	
	private Trampoline bTrampoline;
	/****************/
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v("BOUNCE", "Activity onCreate");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.game_view);
        
        init();
        
        if (savedInstanceState == null) {
        	Log.v("BOUNCE", "Saved instance was null");
        }
        else {
        	Log.v("BOUNCE", "No saved instance");
        }
    }
    
    protected void onRestart() {
    	super.onStart();
    	Log.v("BOUNCE", "onRestart");
    	needPauseMenu = true;
    }
    
    protected void onStart() {
    	super.onStart();
    	Log.v("BOUNCE", "onStart");
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.v("BOUNCE", "onResume");
    	gameView = (SurfaceView) findViewById(R.id.game_view);
    	mSurfaceHolder = gameView.getHolder();
    	mSurfaceHolder.setSizeFromLayout();
    	mSurfaceHolder.addCallback(this);
    	gameView.setFocusable(true);
    	gameView.requestFocus();
    	
    	if (needPauseMenu && !isSurfaceDestroyed) {
    		popPauseMenu();
    	}
    	stopThread();
    	thread = new Thread(this);
    	mAlive = true;
    	thread.start();
    }
    
    protected void onPause() {
    	Log.v("BOUNCE", "onPause");
    	stopThread();
    	if (pauseMenu != null) pauseMenu.cancel();
    	super.onPause();
    }
    
    protected void onStop() {
    	super.onStop();
    	Log.v("BOUNCE", "onStop");
    }
    
    private void stopThread() {
    	Log.v("BOUNCE", "stopThread");
    	if (thread != null) {
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
	        thread = null;
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
    }
    
    
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mMode == STATE_PAUSE) {
				pauseMenu.cancel();
				setState(STATE_RUNNING);
			}
			else {
				setState(STATE_PAUSE);
				popPauseMenu();
			}
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
	
	public void setState(int state) {
		mMode = state;
		switch(state) {
		case STATE_RUNNING:
			mRun = true;
			Log.v("BOUNCE", "setState RUNNING");
			break;
		case STATE_PAUSE:
			mRun = false;
			Log.v("BOUNCE", "setState PAUSE");
			break;
		case STATE_LOSE:

		case STATE_WIN:

		default:
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v("BOUNCE", "surfaceChanged");
		mWidth = width;
		mHeight = height;
		setState(STATE_RUNNING);
		if (needPauseMenu) {
			popPauseMenu();
		}
		if (firstStart) {
			doStart();
			firstStart = false;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		gameView.requestFocus();
		isSurfaceDestroyed = false;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		setState(STATE_PAUSE);
		isSurfaceDestroyed = true;
	}
	
	public boolean onTouchEvent (MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			actionDown(event.getX(), event.getY());
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			actionMove(event.getX(), event.getY());
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			actionUp(event.getX(), event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	public void init() {
		Log.v("BOUNCE", "Init");
		mDifficulty = DIFFICULTY_MEDIUM;
		
		res = getResources();
		
		timer = new Timer();
		FPS = new FramesPerSecond(50);
		scene = new Scene();
		
		trampolines = new ArrayList<Trampoline>();
		characters = new ArrayList<Character>();
		deadTrampolines = new ArrayList<Trampoline>();
	}
	
	public void doStart() {
		characterImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.character), mWidth/10, mHeight/10, false);
		
		trampolines.add(new Trampoline(this, mWidth/4, mHeight/2, mWidth*3/4, mHeight/2, false));
		characters.add(new Character(this));
		
		for (Character c : characters)
			c.doStart(characterImage);
	}

	public void run() {
		boolean singleDraw = false;
		if (needPauseMenu) {
			singleDraw = true;
		}
		
		while (mAlive) {
			if (!mRun) try { Thread.sleep(100); } catch (InterruptedException ie) {}

	        while (mRun) {
	            Canvas c = null;
	            try {
	            	synchronized (mSurfaceHolder) {
	            		c = mSurfaceHolder.lockCanvas();
	            		//FPS.tickStart();

	                    if (!singleDraw) update();
	                    doDraw(c);
	                }
	            } finally {
	                if (c != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(c);
	                }
	                //FPS.tickEnd();
	            }

	            long sleepTime = -1; //FPS.getSleepTime();
	            if (sleepTime > 0) {
	            	try {
	            		Thread.sleep(sleepTime);
	            	} catch (InterruptedException e) {}
	            }

	            if (singleDraw) {
	            	singleDraw = false;
	            	setState(STATE_PAUSE);
	            }
	        }
		}
	}
	
	private void update() {
		float deltaTime = timer.tick();
		
		synchronized (trampolines) {
			synchronized (characters) {
				
				for (Trampoline t : trampolines) {
					for (Character c : characters) {
						if (t.intersectsCharacter(c)) {
							c.pushVel(t.getBounce());
							t.die();
						}
					}
				}
				
				for (Trampoline t : trampolines)
					t.update(deltaTime);
				
				for (Character c : characters)
					c.update(deltaTime);
			}
		
			// Removals
			for (Trampoline t : deadTrampolines)  {
				trampolines.remove(t);
			}
		}
		deadTrampolines.clear();
	}
	
	private void doDraw (Canvas canvas) {
		if (canvas != null) {
			scene.doDraw(canvas);
			
			synchronized (trampolines) {
				for (Trampoline t : trampolines)
					t.doDraw(canvas);
			}
			
			synchronized (characters) {
				for (Character c : characters)
					c.doDraw(canvas);
			}
			
			//FPS.doDraw(canvas);
		}
	}
	
	public void notifyDeadTrampoline(Trampoline t) {
		synchronized (deadTrampolines) {
			deadTrampolines.add(t);
		}
	}

	public void actionDown(float x, float y) {
		if (bTrampoline == null) {
			bTrampoline = new Trampoline(this, x,y,x,y, true);
			synchronized (trampolines) {
				trampolines.add(bTrampoline);
			}
		}
	}

	public void actionMove(float x, float y) {
		synchronized (trampolines) {
			if (bTrampoline != null) {
				bTrampoline.setMax(x, y);
			}
		}
	}

	public void actionUp(float x, float y) {
		synchronized (trampolines) {
			if (bTrampoline != null) {
				bTrampoline.setMax(x, y);
				bTrampoline.finish();
				bTrampoline = null;
			}
		}
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
}
