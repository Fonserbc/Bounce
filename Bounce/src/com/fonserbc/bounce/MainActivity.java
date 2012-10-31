package com.fonserbc.bounce;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;

public class MainActivity extends Activity implements OnClickListener {

	private static final int GAME_RESULT = 1;
	
	private Intent game;
	private Intent settings;
	
	private Typeface font;
	
	// OPTIONS
	SharedPreferences mPrefs;
	
	boolean soundOn = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.settings);
        setContentView(R.layout.activity_main);
        
        Button play = (Button)findViewById(R.id.play);
        Button settings = (Button)findViewById(R.id.settings_button);
        
        font = Typeface.createFromAsset(getAssets(), "fonts/Minecraftia.ttf");
        play.setTypeface(font);
        settings.setTypeface(font);
        
        play.setOnClickListener(this);
        settings.setOnClickListener(this);
        
        restorePrefs();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	//savePrefs();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	restorePrefs();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			game = new Intent (this, GameActivity.class);
			
			startActivityForResult(game, GAME_RESULT);
			break;
		case R.id.settings_button:
			settings = new Intent (this, Preferences.class);
			startActivity(settings);
			
			break;
		}
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (GAME_RESULT) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	    	  int quitting = data.getIntExtra(GameActivity.QUITTING_ID, 0);
	    	  if (quitting != 0) finish();
	      } 
	      break; 
	    } 
	  } 
	}
	
	private void savePrefs() {
    	SharedPreferences.Editor ed = mPrefs.edit();
    	
        ed.putBoolean(GameActivity.SOUND_ON_ID, soundOn);
        
        ed.commit();
	}
    
    private void restorePrefs() {
    	mPrefs = PreferenceManager.getDefaultSharedPreferences(this);;
    	
    	soundOn = mPrefs.getBoolean(getString(R.string.prefs_soundOn), soundOn);
	}
}
