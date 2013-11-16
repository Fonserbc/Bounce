package com.fonserbc.bounce;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

public class TutorialActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        
        ((TextView) findViewById(R.id.howto_title)).setTypeface(font);
        ((TextView) findViewById(R.id.howto_text)).setTypeface(font);
        
        ((TextView) findViewById(R.id.howto_text)).setText(readText());
        
    }

    private String readText() {
    	InputStream inputStream = getResources().openRawResource(R.raw.howto);
    
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
    	int i;
    	try {
    		i = inputStream.read();
    		while (i != -1) {
    			byteArrayOutputStream.write(i);
    			i = inputStream.read();
    		}
    		inputStream.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
 
    	return byteArrayOutputStream.toString();
    }
}
