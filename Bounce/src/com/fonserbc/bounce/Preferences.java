package com.fonserbc.bounce;

import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Button;

public class Preferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        //Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Minecraftia.ttf");
        
        //((Button) findViewById(R.id.sound_on)).setTypeface(font);
    }
}
