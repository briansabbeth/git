package com.MeadowEast.Settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.MeadowEast.R;
import com.MeadowEast.audiotest.MainActivity;

public class StatsActivity extends Activity 
{	
		static final String TAG = "StatsActivity";
	
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.stats_settings_layout);

			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

			StringBuilder builder = new StringBuilder();

			builder.append("\n" + sharedPrefs.getString("time_usage_key", "7 day time usage is " + MainActivity.NSJtotalTime));
	
			TextView settingsTextViewStats = (TextView) findViewById(R.id.stats_settings_text_view);
			
			settingsTextViewStats.setText(builder.toString());
		}
		
		

	}
