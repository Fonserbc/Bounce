package com.fonserbc.bounce;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Context mContext;
	public GameThread thread;

	public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mContext = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
		
        thread = new GameThread(holder, mContext, null);
        
		context.getResources();
		Log.v("BOUNCE", "GameView");		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("BOUNCE", "surfaceCreated");
		
		if (thread.getState()==Thread.State.TERMINATED) {
			thread = new GameThread(holder, mContext, null);
		}
		thread.setState(GameThread.STATE_RUNNING);
        thread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("BOUNCE", "surfaceChanged");
		thread.doStart();
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.v("BOUNCE", "surfaceDestroyed");
		boolean retry = true;
        thread.setState(GameThread.STATE_PAUSE);
        thread.setAlive(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}
	
	public GameThread getThread() {
		return thread;
	}
}
