package com.fonserbc.bounce;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {

	static final String VGAFIB_URL = "http://vgafib.upc.es/";
	static final String TWITTER_URL = "https://twitter.com/fonserbc";
	
	Typeface font;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        font = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        
        ((TextView)findViewById(R.id.about_title)).setTypeface(font);
        ((TextView)findViewById(R.id.about_made)).setTypeface(font);
        ((TextView)findViewById(R.id.about_twitter)).setTypeface(font);
        	findViewById(R.id.about_twitter).setOnClickListener(this);
        ((TextView)findViewById(R.id.about_title)).setTypeface(font);
        findViewById(R.id.about_vgafib).setOnClickListener(this);
    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_twitter:
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(TWITTER_URL));
			startActivity(i);
			break;
		case R.id.about_vgafib:
			Intent a = new Intent(Intent.ACTION_VIEW);
			a.setData(Uri.parse(VGAFIB_URL));
			startActivity(a);
			break;
		}
	}
}
