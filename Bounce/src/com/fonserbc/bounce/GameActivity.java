package com.fonserbc.bounce;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements Runnable, SurfaceHolder.Callback {
	
	/**
	 * I'm using tag "BOUNCE" for all the Verbose logs
	 * 
	 */

	private static final int MENU_EXIT = 1;
	
	private GameView gameView;
	public GameThread thread;
	
	public AlertDialog pauseMenu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v("BOUNCE", "Activity onCreate");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.game_view);
        
        gameView = (GameView) findViewById(R.id.game_view);
        thread = gameView.getThread();
        
        if (savedInstanceState == null) {
        	Log.v("BOUNCE", "Saved instance was null");
        }
        else {
        	thread.restoreState(savedInstanceState);
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
    	thread.pause();
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.v("BOUNCE", "onResume");
    	if (thread != null) thread.unPause();
    }
    
    protected void onStop() {
    	super.onStop();
    	Log.v("BOUNCE", "onStop");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("BOUNCE", "on Save Instance State");
        
        thread.saveState(outState);       
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	Log.v("BOUNCE", "on Restore Instance State");
    	
    	if (savedInstanceState != null) {
    		thread.restoreState(savedInstanceState);
    		Log.v("BOUNCE", "restoring state not null");
    	}
    	
    }
    
    
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (gameView.isFocused()) {
				if (thread.mMode == GameThread.STATE_PAUSE) {
					pauseMenu.cancel();
					thread.setState(GameThread.STATE_RUNNING);
				}
				else {
					thread.setState(GameThread.STATE_PAUSE);
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
    			thread.setState(GameThread.STATE_RUNNING);
    		}
        })
        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {             
    			that.finish();
    		}
        })
        .setNeutralButton("Back to menu", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				thread.setState(GameThread.STATE_RUNNING);
			}
		})
        .show();		
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
