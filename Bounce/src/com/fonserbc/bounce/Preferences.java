package com.fonserbc.bounce;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RatingBar;

public class Preferences extends Activity implements OnClickListener, OnRatingBarChangeListener {
	
	private static int[] DIFFICULTY_TEXT = {R.string.difficulty_easy, R.string.difficulty_normal, R.string.difficulty_hard};
	
	private SharedPreferences mPrefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        
        ((TextView) findViewById(R.id.settings_title)).setTypeface(font);
        ((TextView) findViewById(R.id.sound_music)).setTypeface(font);
        ((Button) findViewById(R.id.sound_on)).setTypeface(font);
        	((Button) findViewById(R.id.sound_on)).setOnClickListener(this);
    	((Button) findViewById(R.id.music_on)).setTypeface(font);
        	((Button) findViewById(R.id.music_on)).setOnClickListener(this);
        ((TextView) findViewById(R.id.difficulty)).setTypeface(font);
        ((TextView) findViewById(R.id.difficulty_text)).setTypeface(font);
        
        mPrefs = getSharedPreferences(getString(R.string.prefs_file), 0);
 
        ((CompoundButton)findViewById(R.id.sound_on)).setChecked(mPrefs.getBoolean(getString(R.string.prefs_soundOn), true));
        ((CompoundButton)findViewById(R.id.music_on)).setChecked(mPrefs.getBoolean(getString(R.string.prefs_musicOn), true));
        ((CompoundButton)findViewById(R.id.music_on)).setEnabled(((CompoundButton)findViewById(R.id.sound_on)).isChecked());
        ((SeekBar) findViewById(R.id.sound_slider)).setProgress(mPrefs.getInt(getString(R.string.prefs_soundSlider), 3));
        	((SeekBar) findViewById(R.id.sound_slider)).setEnabled(((CompoundButton)findViewById(R.id.sound_on)).isChecked());
        ((RatingBar)findViewById(R.id.difficulty_rating)).setRating(((float)mPrefs.getInt(getString(R.string.prefs_difficulty), 1))+1f);
        	((RatingBar)findViewById(R.id.difficulty_rating)).setOnRatingBarChangeListener(this);
        	onRatingChanged((RatingBar)findViewById(R.id.difficulty_rating), ((RatingBar) findViewById(R.id.difficulty_rating)).getRating(), true);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	SharedPreferences.Editor edit = mPrefs.edit();
    	
    	edit.putInt(getString(R.string.prefs_soundSlider), ((SeekBar) findViewById(R.id.sound_slider)).getProgress());
    	edit.putBoolean(getString(R.string.prefs_soundOn), ((CompoundButton)findViewById(R.id.sound_on)).isChecked());
    	edit.putBoolean(getString(R.string.prefs_musicOn), ((CompoundButton)findViewById(R.id.music_on)).isChecked());
    	edit.putInt(getString(R.string.prefs_difficulty), (int) (((RatingBar)findViewById(R.id.difficulty_rating)).getRating()-0.5f));
    	
    	edit.commit();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sound_on:
			((SeekBar) findViewById(R.id.sound_slider)).setEnabled(((CompoundButton)v).isChecked());
			((CompoundButton)findViewById(R.id.music_on)).setEnabled(((CompoundButton)v).isChecked());
		}
	}

	public void onRatingChanged(RatingBar v, float rating, boolean user) {
		switch (v.getId()) {
		case R.id.difficulty_rating:
			Log.v("BOUNCE", ""+rating);
			int aux = 0;
			if (rating <= 1f) aux = 0;
			else if (rating <= 2f) aux = 1;
			else aux = 2;
			((TextView) findViewById(R.id.difficulty_text)).setText(DIFFICULTY_TEXT[aux]);
		}
	}
}
