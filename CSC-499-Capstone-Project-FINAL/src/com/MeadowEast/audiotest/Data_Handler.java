package com.MeadowEast.audiotest;

/**@author bjsabbeth
 * Data_Handler class will be the interface between MainActivity
 * and the back end.  
 * Creates the database from the dbhelper class and communicates with it 
 * via the TingshuoDatasource class.
 * Loads and updates to the database are handled here
 * Clips are returned publicly to the MainActivity
 * Probability is generated here as well. 
 * 
 *  Will be able to accommodate non random pulling of clips as per 
 *  instructions: 50 clips updating 10 at a time until we are out 
 *  of clips.
 */

import android.content.ContentValues;
import android.content.Context;

import com.MeadowEast.dbOpenHelper.TingshuoDatasource;
import com.MeadowEast.model.Model;
import java.util.ArrayList;
import android.util.Log;

public class Data_Handler {
	//Array list of all the available clips that the program is currently running
	ArrayList<String> availableClips;
	//array list of the weighted probabilities.
	ArrayList<String> probabilityArray;
	//Current clip being called. Used for testing.
	String currentClip;
	//Last clip being called. Used for testing.
	String lastClip;
	//boolean sameTurn says if the turn is a repeat or just a play
    
	//database;
	TingshuoDatasource datasource;
    
	String LOGTAG = "BJS DB ";
	
	boolean sameTurn;

	/**
	 * Constructor 
	 * 			Initializes: AvailableClip list
	 * 				         the probabilityArray
	 * 	        			 the same turn & current clip.
	 */
	Data_Handler()
	{
		currentClip = lastClip = "0";
		availableClips = new ArrayList<String>();
		probabilityArray = new ArrayList<String>();
		
		Log.i(LOGTAG, "ABOUT TO OPEN DATASOURCE");
		
      //  datasource = new TingshuoDatasource(this);
	      datasource.open();
		
	}
}
