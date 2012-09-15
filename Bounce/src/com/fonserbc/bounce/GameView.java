package com.fonserbc.bounce;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	public boolean surfaceCreated;
	public GameThread thread;

	public GameView(Context context) {
		super(context);getHolder().addCallback(this);
		
		context.getResources();
		Log.v("BOUNCE", "GameView");
		surfaceCreated = false;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("BOUNCE", "surfaceCreated");
		if (surfaceCreated == false) {
			createThread(holder);
			Log.v("GameView", "Thread created");
			surfaceCreated = true;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("BOUNCE", "surfaceChanged");
	}
	
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.v("BOUNCE", "surfaceDestroyed");
		surfaceCreated = false;
	}

	public void createThread(SurfaceHolder holder) {
		thread = new GameThread(holder);
        thread.run = true;
        thread.start();
	}

	public void terminateThread() {
		thread.run = false;
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            Log.e("FUNCTION", "terminateThread corrupts");
        }   
	}

}
