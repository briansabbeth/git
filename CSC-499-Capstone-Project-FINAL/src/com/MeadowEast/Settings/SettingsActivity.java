package com.MeadowEast.Settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import com.MeadowEast.R;

public class SettingsActivity extends Activity 
{	
	static final String TAG = "ShowSettingsActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.show_settings_layout);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		StringBuilder builder = new StringBuilder();

		builder.append("\n" + sharedPrefs.getBoolean("night_mode_key", true));
		
		builder.append("\n" + sharedPrefs.getString("language_key", "-1"));

		TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
		
		settingsTextView.setText(builder.toString());	
	}

}
