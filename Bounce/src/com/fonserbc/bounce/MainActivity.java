package com.fonserbc.bounce;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private static final int GAME_RESULT = 1;
	
	private Intent game;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button play = (Button)findViewById(R.id.play);
        play.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play:
			game = new Intent (this, GameActivity.class);
			startActivityForResult(game, GAME_RESULT);
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
	      // TODO Switch tabs using the index.
	      } 
	      break; 
	    } 
	  } 
	}
}
