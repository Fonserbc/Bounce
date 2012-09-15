package com.fonserbc.bounce;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static final int MENU_EXIT = 1;
	
	private GameView gameView;
	private GameThread gameThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.game_view);
        
        gameView = (GameView) findViewById(R.id.game_view);
        gameThread = gameView.getThread();
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
    
    protected void onPause() {
    	super.onPause();
    	Log.v("BOUNCE", "onPause");
    	gameView.getThread().pause();
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.v("BOUNCE", "onResume");
    	gameView.getThread().unPause();
    }
}
