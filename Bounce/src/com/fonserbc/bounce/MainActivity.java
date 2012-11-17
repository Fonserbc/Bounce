package com.fonserbc.bounce;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final int GAME_RESULT = 1;
	
	private Intent game;
	
	private Typeface font;
	
	private RankingManager rankingManager;
	
	private String mName;
	int mPoints;
	int mDifficulty;
	
	private int toastDuration = 1;
	
	AlertDialog registerMenu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button play = (Button)findViewById(R.id.play);
        Button ranking = (Button)findViewById(R.id.ranking_button);
        Button howto = (Button)findViewById(R.id.howto_button);
        Button settings = (Button)findViewById(R.id.settings_button);
        
        font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        ((TextView) findViewById(R.id.main_title)).setTypeface(font);
        play.setTypeface(font);
        ranking.setTypeface(font);
        howto.setTypeface(font);
        settings.setTypeface(font);
        
        play.setOnClickListener(this);
        ranking.setOnClickListener(this);
        howto.setOnClickListener(this);
        settings.setOnClickListener(this);
        
        try {
			rankingManager = RankingManager.getInstance(getBaseContext());
		} catch (IOException e) {
			Log.v("BOUNCE", "Could not access ranking");
		}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			game = new Intent (this, GameActivity.class);
			startActivityForResult(game, GAME_RESULT);
			break;
		case R.id.settings_button:
			startActivity(new Intent (this, Preferences.class));
			break;
		case R.id.howto_button:
			startActivity(new Intent(this, TutorialActivity.class));
			break;
		case R.id.ranking_button:
			startActivity(new Intent(this, RankingActivity.class));
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
	    	  else {
	    		  mPoints = data.getIntExtra(GameActivity.POINTS_ID, -1);
	    		  mDifficulty = data.getIntExtra(GameActivity.DIFFICULTY_ID, 1);
	    		  
	    		  if (rankingManager.isWorth(mDifficulty, mPoints))  {
	    				popNameMenu(mPoints);
	    		  }
	    	  }
	      } 
	      break; 
	    } 
	  } 
	}
	
	private void popNameMenu(int mPoints) {
		mName = "AAA";
		final MainActivity that = this;

		View registerView = getLayoutInflater().inflate(R.layout.register_menu, null);
		((TextView) registerView.findViewById(R.id.register_title)).setTypeface(font);
		((TextView) registerView.findViewById(R.id.register_got)).setTypeface(font);
		((TextView) registerView.findViewById(R.id.register_points)).setTypeface(font);
		((TextView) registerView.findViewById(R.id.register_points)).setText(((Integer)mPoints).toString());
		((TextView) registerView.findViewById(R.id.register_points_txt)).setTypeface(font);
		((TextView) registerView.findViewById(R.id.register_enter)).setTypeface(font);
		((TextView) registerView.findViewById(R.id.register_save)).setTypeface(font);
		((EditText) registerView.findViewById(R.id.register_edit)).setText(mName);
		((Button) registerView.findViewById(R.id.register_save)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				that.mName = ((TextView) (((View)v.getParent()).findViewById(R.id.register_edit))).getText().toString();
				if (that.mName.length() > 0) {
					that.returnNameAndEnd();
					that.registerMenu.cancel();
				}
				else {
					Toast toast = Toast.makeText(getBaseContext(), getString(R.string.error_name), toastDuration);
					toast.show();
				}
			}
		});
		
		registerMenu = new AlertDialog.Builder(that)
		.setView(registerView)
		.show();
	}

	protected void returnNameAndEnd() {
		rankingManager.register(mDifficulty, mName, mPoints);
	}
}
