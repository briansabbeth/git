package com.MeadowEast.Settings;


import com.MeadowEast.R;

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

public class PrefsActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener{
	CheckBoxPreference isReg;
	static final String TAG = "PrefsActivity";
	
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
    			startActivity(new Intent(this, SettingsActivity.class));
    			return true;
    	}
    	return false;
    }
    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(PrefsActivity.this);
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

        // Let's do something a preference value changes
        if (key.equals("night_mode_key")) 
        {
            // Create a reference to the checkbox (in this case):
            CheckBoxPreference night_mode_check = (CheckBoxPreference)getPreferenceScreen().findPreference("night_mode_key");
      
            editor.putBoolean("night_mode_key", night_mode_check.isChecked());

	        // Save the results:
            editor.commit();
        }

        if (key.equals("language_key")) 
        {

	       sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	       
	       String entryvalue = sharedPreferences.getString( "language_key", "");
	       Log.d(TAG, "Entryvalue " + entryvalue);
	      
      
	    if (entryvalue.equals("EN"))
	    {
	    	Log.d(TAG, "EN " + entryvalue);
	    	Toast.makeText(getBaseContext(), "English Selected", Toast.LENGTH_SHORT).show();
	    }
	    else if (entryvalue.equals("CH"))
	    {
	    	Log.d(TAG, "CH " + entryvalue);
	    	Toast.makeText(getBaseContext(), "Chinese Selected", Toast.LENGTH_SHORT).show();
	    }
	    else
	    {
	    	Toast.makeText(getBaseContext(), "You may not go where no1 has gone before, pick again!", Toast.LENGTH_SHORT).show();
	    }

	   }
	    
    }
 }