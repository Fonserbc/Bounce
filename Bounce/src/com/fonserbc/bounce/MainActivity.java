package com.fonserbc.bounce;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;

public class MainActivity extends Activity implements OnClickListener {

	private static final int GAME_RESULT = 1;
	
	private Intent game;
	
	private Typeface font;
	
	// OPTIONS
	SharedPreferences mPrefs;
	
	boolean soundOn = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button play = (Button)findViewById(R.id.play);
        Button sound = (Button)findViewById(R.id.sound_on);
        
        font = Typeface.createFromAsset(getAssets(), "fonts/Minecraftia.ttf");
        play.setTypeface(font);
        sound.setTypeface(font);
        
        play.setOnClickListener(this);
        sound.setOnClickListener(this);
        
        restorePrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	savePrefs();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			game = new Intent (this, GameActivity.class);
			
			game.putExtra(GameActivity.SOUND_ON_ID, soundOn);
			
			startActivityForResult(game, GAME_RESULT);
			break;
		case R.id.sound_on:
			soundOn = ((CompoundButton) v).isChecked();
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
    	mPrefs = getPreferences(MODE_PRIVATE);
    	
    	soundOn = mPrefs.getBoolean(GameActivity.SOUND_ON_ID, soundOn);
    	((CompoundButton)findViewById(R.id.sound_on)).setChecked(soundOn);
	}
}
