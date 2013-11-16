package com.fonserbc.bounce;

import java.io.IOException;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TextView;

public class RankingActivity extends TabActivity implements OnGestureListener {
	
	public static final float MIN_LENGTH = 100;
	public static final float MIN_VEL = 100;

	private RankingManager ranking;
	
	private GestureDetector gestures;
	
	Typeface font;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        
        font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        
        ((TextView)findViewById(R.id.ranking_title)).setTypeface(font);
        
        try {
			ranking = RankingManager.getInstance(getBaseContext());
		} catch (IOException e) {
			Log.v("BOUNCE", "Could not access ranking");
		}
        
        TabHost host = getTabHost();
        host.setup ();

        TabSpec easyTab = host.newTabSpec(getString(R.string.difficulty_easy));
        easyTab.setIndicator(getResources().getString(R.string.difficulty_easy));
        easyTab.setContent(R.id.scroll_easy);
        host.addTab(easyTab);

        TabSpec normalTab = host.newTabSpec(getString(R.string.difficulty_normal));
        normalTab.setIndicator(getResources().getString(R.string.difficulty_normal));
        normalTab.setContent(R.id.scroll_normal);
        host.addTab(normalTab);
        
        TabSpec hardTab = host.newTabSpec(getString(R.string.difficulty_hard));
        hardTab.setIndicator(getResources().getString(R.string.difficulty_hard));
        hardTab.setContent(R.id.scroll_hard);
        host.addTab(hardTab);

        host.setCurrentTabByTag(getString(R.string.difficulty_easy));
        
        LinearLayout ll = (LinearLayout) host.getChildAt(0);
        TabWidget tw = (TabWidget) ll.getChildAt(0);

        for (int i = 0; i < 3; ++i) {
	        RelativeLayout rllf = (RelativeLayout) tw.getChildAt(i);
	        TextView lf = (TextView) rllf.getChildAt(1);
	        lf.setTextAppearance(getBaseContext(), android.R.attr.textAppearanceMedium);
	        lf.setTypeface(font);
        }
        
        fillTables();
        
        gestures = new GestureDetector(this);
    }
    
    private void fillTables() {
    	ranking.fillTable((TableLayout)findViewById(R.id.table_easy), GameActivity.DIFFICULTY_EASY);
        ranking.fillTable((TableLayout)findViewById(R.id.table_normal), GameActivity.DIFFICULTY_MEDIUM);
        ranking.fillTable((TableLayout)findViewById(R.id.table_hard), GameActivity.DIFFICULTY_HARD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ranking, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.menu_clear:
        	popSureMenu();
        	return false;
        default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
	public void popSureMenu() {
		final RankingActivity that = this;

		AlertDialog sureMenu = new AlertDialog.Builder(this)
		.setMessage(R.string.sure_erase)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			try {
					ranking.resetData();
					fillTables();
				} catch (IOException e) {
					AlertDialog error = new AlertDialog.Builder(that)
					.setMessage(R.string.error_erase)
					.setNegativeButton(R.string.ok, null)
					.show();
					
					((TextView) error.findViewById(android.R.id.message)).setTypeface(that.font);
					((TextView) error.findViewById(android.R.id.button2)).setTypeface(that.font);
				}
    		}
        })
        .setNegativeButton(R.string.no, null)
        .show();
		((TextView) sureMenu.findViewById(android.R.id.message)).setTypeface(font);
		((TextView) sureMenu.findViewById(android.R.id.button1)).setTypeface(font);
		((TextView) sureMenu.findViewById(android.R.id.button2)).setTypeface(font);
	}
	
	public boolean fling(float dX) {
		TabHost host = getTabHost();
		int tab = host.getCurrentTab();
		if (dX < 0) {
			if (tab < 2) tab += 1;
			else return false;
		}
		else {
			if (tab > 0) tab -= 1;
			else return false;
		}
		host.setCurrentTab(tab);
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
        if (e1==null || e2==null)
            return false;
        float dX = e2.getX()-e1.getX();
        
        if ((dX > MIN_LENGTH || dX < -MIN_LENGTH) && Math.abs(velocityX) > MIN_VEL) {
        	return fling(dX);
        }
        else return false;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	    if (gestures != null) {
	        if (gestures.onTouchEvent(ev))
	            return true;
	    }
	    return super.dispatchTouchEvent(ev);
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
