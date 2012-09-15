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
        thread.doStart();
        
		context.getResources();
		Log.v("BOUNCE", "GameView");		
		setFocusable(true);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("BOUNCE", "surfaceCreated");
		
		if (thread.getState()==Thread.State.TERMINATED) {
			thread = new GameThread(holder, mContext, null);
	        thread.doStart();
		}
		thread.setRunning(true);
        thread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("BOUNCE", "surfaceChanged");
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.v("BOUNCE", "surfaceDestroyed");
		boolean retry = true;
        thread.setRunning(false);
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
