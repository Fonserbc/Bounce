package com.fonserbc.bounce;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("BOUNCE", "surfaceChanged");

		int lastState = GameThread.STATE_RUNNING;
		if (thread.getState()==Thread.State.TERMINATED) {
			lastState = thread.getLastState();
			thread = new GameThread(holder, mContext, null);
			((MainActivity) mContext).thread = thread;
			Log.v("BOUNCE", "Thread was TERMINATED");
		}
		thread.doStart(lastState);
		thread.start();
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.v("BOUNCE", "surfaceDestroyed");
		boolean retry = true;
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
	
	public boolean onTouchEvent (MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			thread.actionDown(event.getX(), event.getY());
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			thread.actionMove(event.getX(), event.getY());
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			thread.actionUp(event.getX(), event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}
}
