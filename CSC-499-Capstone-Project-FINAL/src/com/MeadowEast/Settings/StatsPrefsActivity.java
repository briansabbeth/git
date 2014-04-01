package com.MeadowEast.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.MeadowEast.R;

public class StatsPrefsActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener{
	
	static final String TAG = "StatsPrefsActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.preferences); 
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//menu.add(Menu.NONE, 0, 0, "Show current settings");
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case 0:
    			startActivity(new Intent(this, StatsActivity.class));
    			return true;
    	}
    	return false;
    }
    @Override
    public void onStart(){
        super.onStart();
       // SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(StatsPrefsActivity.this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	
    	SharedPreferences s = getSharedPreferences("MY_PREFS", 0);

        // Create a editor to edit the preferences:
        SharedPreferences.Editor editor = s.edit();

    

	   }
	    
    
 }