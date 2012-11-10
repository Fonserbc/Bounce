package com.fonserbc.bounce;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.fonserbc.bounce.utils.Timer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends Activity implements Runnable, SurfaceHolder.Callback {
	
	/**
	 * I'm using tag "BOUNCE" for all the Verbose logs
	 * 
	 */
	public static final String QUITTING_ID = "quitting";
	public static final String SOUND_ON_ID = "soundOn";
	
	public static final int DEF_LIFES = 5;

	public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_MEDIUM = 1;
	
	public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;	
    
    public int MAX_TRAMPOLINES = 3;
    public int MAX_COLLECTIBLES = 10;
    
    public static final float TIME_BETWEEN_CHARACTER_SPAWNS = 5;
    public static final float TIME_BETWEEN_COLLECTIBLE_SPAWNS = 1;

	static final float TOTAL_TIME = 60f;
    
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
	SharedPreferences mPrefs;
	
	Typeface font;
	
	private DecimalFormat df = new DecimalFormat("0");
	
	private SoundPool mSoundPool;
	MediaPlayer player;
		public boolean soundOn = false;
		public boolean musicOn = false;
		float volume = 0f;
			int sound_jump;
			int sound_die;
			int sound_music;
			int sound_hit;
			int sound_power_up;
			int sound_fuse;
	
	/****************/
	/** GAME STUFF **/
	/****************/
    int mMode;
    int mDifficulty = DIFFICULTY_MEDIUM;
    int mPoints = 0;
    float time = 0;
    
    Paint pointsPaint;
    Paint timePaint;
    
    int lives;
	
	private Timer timer;
		private float addedTime = 0;
	
	private Scene scene;
	
	private ArrayList<Entity> entities;
	
	private ArrayList<Trampoline> trampolines;	
	private ArrayList<Character> characters;
		float characterSpawnTime = TIME_BETWEEN_CHARACTER_SPAWNS*3/4;
	private ArrayList<Collectible> collectibles;
		float collectibleSpawnTime = 0;
	
	private ArrayList<Entity> deadEntities;
	
	private Bitmap characterImage;
	private Bitmap collectibleImage;
	
	private Trampoline bTrampoline;
	/****************/
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v("BOUNCE", "Activity onCreate");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        font = Typeface.createFromAsset(getAssets(), "fonts/Minecraftia.ttf");        
        //setFonts();
        
        restorePreferences();
        
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
    	if (player != null) player.release();
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
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
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
		else return super.onKeyDown(keyCode, event);
	}
	
	private void popPauseMenu() {
		Log.v("BOUNCE", "Popping pause menu");
		final GameActivity that = this;
		View pauseView = getLayoutInflater().inflate(R.layout.pause_menu, null);
		((TextView) pauseView.findViewById(R.id.game_paused)).setTypeface(font);
			((Button)pauseView.findViewById(R.id.resume)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {             
	    			setState(STATE_RUNNING);
	    			pauseMenu.cancel();
	    		}});
			((TextView) pauseView.findViewById(R.id.resume)).setTypeface(font);
			((Button)pauseView.findViewById(R.id.quit)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {
	    			popSureMenu(true);	
	    		}});
			((TextView) pauseView.findViewById(R.id.quit)).setTypeface(font);
			((Button)pauseView.findViewById(R.id.back_to_menu)).setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View view) {
	    			popSureMenu(false);
	    		}});
			((TextView) pauseView.findViewById(R.id.back_to_menu)).setTypeface(font);
		
		pauseMenu = new AlertDialog.Builder(that)
			.setView(pauseView)
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					setState(STATE_RUNNING);
				}
			}).show();
	}
	
	public void popSureMenu(boolean quit) {
		quitting = quit;
		final GameActivity that = this;

		AlertDialog sure = new AlertDialog.Builder(this)
		.setMessage(R.string.sure_quit)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			if (that.quitting) {
    				Intent resultIntent = new Intent();
    				resultIntent.putExtra(QUITTING_ID, 1);
    				that.setResult(Activity.RESULT_OK, resultIntent);
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
		((TextView) sure.findViewById(android.R.id.message)).setTypeface(font);
		((TextView) sure.findViewById(android.R.id.button1)).setTypeface(font);
		((TextView) sure.findViewById(android.R.id.button2)).setTypeface(font);
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
			finish();
			break;
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
		res = getResources();
		
		Context context = getBaseContext();
		
		mSoundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
			sound_jump = mSoundPool.load(context, R.raw.jump, 1);
			sound_die = mSoundPool.load(context, R.raw.die, 1);
			sound_hit = mSoundPool.load(context, R.raw.hit, 1);
			sound_music = mSoundPool.load(context, R.raw.bounce, 1);
			sound_power_up = mSoundPool.load(context, R.raw.power_up, 1);
			sound_fuse = mSoundPool.load(context, R.raw.fuse, 1);
				
		timer = new Timer();
		//FPS = new FramesPerSecond(50);
		scene = new Scene();
		
		pointsPaint = new Paint();
		pointsPaint.setTypeface(font);
		pointsPaint.setColor(Color.BLUE);
		pointsPaint.setTextSize(30);
		
		timePaint = new Paint();
		timePaint.setTypeface(font);
		timePaint.setColor(Color.YELLOW);
		timePaint.setTextSize(40);
		
		entities = new ArrayList<Entity>();
		trampolines = new ArrayList<Trampoline>();
		characters = new ArrayList<Character>();
		collectibles = new ArrayList<Collectible>();
		deadEntities = new ArrayList<Entity>();
	}
	
	private void restorePreferences() {
		mPrefs = getSharedPreferences(getString(R.string.prefs_file), 0);		
    	
    	soundOn = mPrefs.getBoolean(getString(R.string.prefs_soundOn), soundOn);
    	musicOn = mPrefs.getBoolean(getString(R.string.prefs_musicOn), musicOn);
    	volume = mPrefs.getInt(getString(R.string.prefs_soundSlider), 3)/5f;
		
		mDifficulty = mPrefs.getInt(getString(R.string.prefs_difficulty), 1);
		
		lives = DEF_LIFES - mDifficulty;
	}

	public void doStart() {
		characterImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.character_sheet_big), mWidth/2, mHeight/4, false);
		collectibleImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.pigeon_sheet_big), mWidth/2, mHeight/8, false);
		
		float defTrampXm = mWidth/16;
		float defTrampXM = mWidth - mWidth/16;
		float defTrampY = mHeight-mHeight/16;
		Trampoline aux = new Trampoline(this, defTrampXm, defTrampY, defTrampXM, defTrampY, false);
		aux.setLifetime(-1);
		entities.add(aux);
		trampolines.add(aux);
	}

	public void run() {
		playMusic();
		
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
									if (lives < 0) setState(STATE_LOSE);
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
						Collectible newCol = new Collectible(this, collectibleImage);
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
			
			canvas.drawText(""+mPoints, mWidth - 10 - pointsPaint.measureText(""+mPoints), pointsPaint.getTextSize() + 10, pointsPaint);
			canvas.drawText("Lives: "+lives, 10, pointsPaint.getTextSize() + 10, pointsPaint);
			if (addedTime > timer.getGameTime()) addedTime = timer.getGameTime();
			float leftTime = TOTAL_TIME + addedTime - timer.getGameTime();
			if (leftTime < 0) setState(STATE_LOSE);
			String time = df.format(leftTime);
			canvas.drawText(time, mWidth/2 - timePaint.measureText(time)/2, timePaint.getTextSize()-5, timePaint);
		}
	}
	
	public void notifyDeadEntity(Entity e) {
		synchronized (deadEntities) {
			deadEntities.add(e);
		}
	}
	
	public void notifyCollectibleGet() {
		mPoints += 10;
		addedTime += 2;
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
	
	public void playSound (int id) {
		if (soundOn) {
			int soundId = 0;
			switch (id) {
			case R.raw.jump: soundId = sound_jump; break;
			case R.raw.die: soundId = sound_die; break;
			case R.raw.hit: soundId = sound_hit; break;
			case R.raw.power_up: soundId = sound_power_up; break;
			case R.raw.fuse: soundId = sound_fuse; break;
			}
			mSoundPool.play(soundId, (volume > 0.99f)? 0.99f : volume, (volume > 0.99f)? 0.99f : volume, 1, 0, 0);
		}
	}
	
	public void playMusic () {
		if (soundOn && musicOn) {
			player = MediaPlayer.create(getBaseContext(), R.raw.bounce);
			player.setLooping(true);
			player.setVolume(volume,  volume);
			player.start();
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
