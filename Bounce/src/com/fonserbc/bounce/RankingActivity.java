package com.fonserbc.bounce;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TextView;

public class RankingActivity extends Activity {

	private RankingManager ranking;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        
        ((TextView)findViewById(R.id.ranking_title)).setTypeface(font);
        
        try {
			ranking = RankingManager.getInstance(getBaseContext());
		} catch (IOException e) {
			Log.v("BOUNCE", "Could not access ranking");
		}
        
        ranking.fillTable((TableLayout)findViewById(R.id.table), GameActivity.DIFFICULTY_EASY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ranking, menu);
        return true;
    }
}
