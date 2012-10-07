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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class GameActivity extends Activity implements Runnable, SurfaceHolder.Callback {
	
	/**
	 * I'm using tag "BOUNCE" for all the Verbose logs
	 * 
	 */
	public static final int DEF_LIVES = 3;

	public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_MEDIUM = 1;
	
	public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;	
    
    public static final int MAX_TRAMPOLINES = 3;
    public static final int MAX_COLLECTIBLES = 10;
    
    public static final float TIME_BETWEEN_CHARACTER_SPAWNS = 5;
    public static final float TIME_BETWEEN_COLLECTIBLE_SPAWNS = 1;
    
	SurfaceView gameView;
		SurfaceHolder mSurfaceHolder;
	    int mWidth;
	    int mHeight;
	    boolean firstStart = true;
	    boolean canDraw = false;
	    
	public Thread thread;
		volatile boolean mRun = false;
		volatile boolean mAlive = true;
	    boolean needPauseMenu = false;
		boolean isSurfaceDestroyed = true;
		boolean wasSurfaceDestroyed = false;
	
	public AlertDialog pauseMenu;
		boolean quitting = false;
    
	Resources res;
	
	/****************/
	/** GAME STUFF **/
	/****************/
    int mMode;
    int mDifficulty = DIFFICULTY_MEDIUM;
    int mPoints = 0;
    float time = 0;
    
    Paint textPaint;
    
    int lives;
	
	private Timer timer;
	
	private FramesPerSecond FPS;
	
	private Scene scene;
	
	private ArrayList<Entity> entities;
	
	private ArrayList<Trampoline> trampolines;	
	private ArrayList<Character> characters;
		float characterSpawnTime = TIME_BETWEEN_CHARACTER_SPAWNS*3/4;
	private ArrayList<Collectible> collectibles;
		float collectibleSpawnTime = 0;
	
	private ArrayList<Entity> deadEntities;
	
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
    	if (pauseMenu == null) popPauseMenu();
    	Log.v("BOUNCE", "onPause");
    	stopThread();
    	if (pauseMenu != null) pauseMenu.dismiss();
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
				return true;
			}
			else {
				setState(STATE_PAUSE);
				popPauseMenu();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void popPauseMenu() {
		final GameActivity that = this;
		View pauseView = getLayoutInflater().inflate(R.layout.pause_menu, null);
			((Button)pauseView.findViewById(R.id.resume)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {             
	    			setState(STATE_RUNNING);
	    			pauseMenu.cancel();
	    		}});
			((Button)pauseView.findViewById(R.id.quit)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {
	    			popSureMenu(true);	
	    		}});
			((Button)pauseView.findViewById(R.id.back_to_menu)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {
	    			popSureMenu(false);
	    		}});
		
		pauseMenu = new AlertDialog.Builder(this)
			.setTitle("Game Paused")
			.setView(pauseView)
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					setState(STATE_RUNNING);
					pauseMenu.cancel();
				}
			})
			.show();
	}
	
	public void popSureMenu(boolean quit) {
		quitting = quit;
		final GameActivity that = this;

		new AlertDialog.Builder(this)
		.setMessage(R.string.sure_quit)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			if (that.quitting) {
	    			that.finish();
	    			pauseMenu.cancel();
    			}
    			else {
    				that.onBackPressed();
    			}
    		}
        })
        .setNegativeButton(R.string.no, null)
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
		canDraw = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		gameView.requestFocus();
		isSurfaceDestroyed = false;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		setState(STATE_PAUSE);
		isSurfaceDestroyed = true;
		wasSurfaceDestroyed = true;
		canDraw = false;
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
		
		lives = DEF_LIVES;
		
		res = getResources();
		
		timer = new Timer();
		FPS = new FramesPerSecond(50);
		scene = new Scene();
		
		textPaint = new Paint();
		textPaint.setColor(Color.BLUE);
		textPaint.setTextSize(20);
		
		entities = new ArrayList<Entity>();
		trampolines = new ArrayList<Trampoline>();
		characters = new ArrayList<Character>();
		collectibles = new ArrayList<Collectible>();
		deadEntities = new ArrayList<Entity>();
	}
	
	public void doStart() {
		characterImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.character_sheet_big), mWidth/2, mHeight/4, false);
		//characterImage = BitmapFactory.decodeResource(res, R.drawable.character_sheet_big);
		
		float defTrampXm = mWidth/16;
		float defTrampXM = mWidth - mWidth/16;
		float defTrampY = mHeight-mHeight/16;
		Trampoline aux = new Trampoline(this, defTrampXm, defTrampY, defTrampXM, defTrampY, false);
		entities.add(aux);
		trampolines.add(aux);
	}

	public void run() {
		boolean singleDraw = false;
		if (needPauseMenu && wasSurfaceDestroyed) {
			singleDraw = true;
			wasSurfaceDestroyed = false;
		}
		
		while (mAlive) {
			if (!mRun) try { Thread.sleep(100); } catch (InterruptedException ie) {}

	        while (mRun && canDraw) {
	            Canvas c = null;
	            try {
	            	synchronized (mSurfaceHolder) {
	            		c = mSurfaceHolder.lockCanvas();

	                    if (!singleDraw) update();
	                    doDraw(c);
	                }
	            } finally {
	                if (c != null) {
	                    mSurfaceHolder.unlockCanvasAndPost(c);
	                }
	            }

	            if (singleDraw) {
	            	singleDraw = false;
	            	Log.v("BOUNCE", "Single Draw");
	            	setState(STATE_PAUSE);
	            }
	        }
		}
	}
	
	private void update() {
		float deltaTime = timer.tick();
		
		synchronized (trampolines) {
			synchronized (characters) {
				synchronized (collectibles) {
					/*** SPAWN ***/
					doSpawn(deltaTime);
					/****/
					
					/*** COLLISIONS ***/
					for (Trampoline t : trampolines) {
						for (Character c : characters) {
							if (t.intersectsCharacter(c)) {
								c.pushVel(t.getBounce());
								t.die();
								break;
							}
						}
					}
					
					for (Collectible c : collectibles) {
						for (Character ch : characters) {
							if (c.collidesCharacter(ch)) {
								c.die();
								notifyCollectibleGet();
							}
						}
					}
					/****/
					synchronized (entities) {
						for (Entity e : entities)
							e.update(deltaTime);
				
						// Removals
						synchronized (deadEntities) {
							for (Entity e : deadEntities)  {
								entities.remove(e);
								
								switch (e.type){
								case Trampoline:
									trampolines.remove(e); break;
								case Character:
									characters.remove(e); 
									lives--;
									break;
								case Collectible:
									collectibles.remove(e); break;
								}
							}
							deadEntities.clear();
						}
					}
				}
			}
		}
	}
	
	private void doSpawn (float deltaTime) {
		synchronized (entities) {
			/** CHARACTERS SPAWN **/
			if (characters.size() < 2+mDifficulty) {
				characterSpawnTime += deltaTime;
				
				if (characterSpawnTime > TIME_BETWEEN_CHARACTER_SPAWNS) {
					characterSpawnTime = 0;
					Character newChar = new Character(this, characterImage);
					entities.add(newChar);
					characters.add(newChar);
				}
			}
			
			/** COLLECTIBLES SPAWN **/
			if (collectibles.size() < MAX_COLLECTIBLES) {
				collectibleSpawnTime += deltaTime;
				
				if (collectibleSpawnTime > TIME_BETWEEN_COLLECTIBLE_SPAWNS) {
					collectibleSpawnTime = 0;
					
					double aux = 2d*Math.random();
					if (aux > 1) {
						Collectible newCol = new Collectible(this);
						entities.add(newCol);
						collectibles.add(newCol);
					}
				}
			}
		}
	}
	
	private void doDraw (Canvas canvas) {
		if (canvas != null) {
			scene.doDraw(canvas);
			
			synchronized (entities) {
				for (Entity e : entities)
					e.doDraw(canvas);
			}
			
			canvas.drawText(mPoints + " POINTS", mWidth - 10 - textPaint.measureText(mPoints + " POINTS"), 20, textPaint);
		}
	}
	
	public void notifyDeadEntity(Entity e) {
		synchronized (deadEntities) {
			deadEntities.add(e);
		}
	}
	
	public void notifyCollectibleGet() {
		mPoints += 10;
	}

	public void actionDown(float x, float y) {
		if (bTrampoline == null) {
			bTrampoline = new Trampoline(this, x,y,x,y, true);
			synchronized (entities) {
				entities.add(bTrampoline);
			}
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

	public int getTrampolinesCont() {
		return trampolines.size();
	}
}
