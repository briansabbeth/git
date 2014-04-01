package com.MeadowEast.UpdateService;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.MeadowEast.R;
import com.MeadowEast.audiotest.MainActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CheckUpdate extends AsyncTask<String, String, String> 
{
	
	  private static final String TAG = "CheckUpdate Service";
	  
	  static String mainPath = "/Android/data/com.MeadowEast.audiotest/files/";
	  String clipsPath = "/Android/data/com.MeadowEast.audiotest/files/clips/";
	  
	  
	  
	  String clipinfotxtLocation = mainPath	+ "/clipinfo.txt";
	  String clipszipLocation = mainPath	+ "/clips.zip";
	  String englishzipLocation = mainPath	+ "/english.zip";
	  
	  
	  String clipinfoURL = "http://www.meadoweast.com/capstone/clipinfo.txt";
	  String clipszipURL = "http://www.meadoweast.com/capstone/clips.zip";
	  String englishclipszipURL = "http://www.kosar.info/english.zip"; 
	  
	  
	  
	  int check_point = 0;

	  
	  public CheckUpdate() throws IOException 
		{
		  
		}
		
		
	  
	  
	 boolean quickcheckupdate(String url, String path)
	 {
			
			try
				{
				
				URL urltxt = new URL(url);

				URLConnection urlConnection = urltxt.openConnection();
				
				urlConnection.connect();
				
				int file_size = urlConnection.getContentLength();
				
				String file_size_string = String.valueOf(file_size);
				
				
				Log.d(TAG, file_size_string);
				
				File file=new File(Environment.getExternalStorageDirectory(), path);
				
				long length = file.length();
				
				String s = String.valueOf(length);
				
				Log.d(TAG, s);
			
				if (length == file_size)
					{
						Log.d(TAG, "Update check, files locally and remote are of same size.");
						
						check_point++;
						return false;
						
						//Toast.makeText(this, "No new updates!", Toast.LENGTH_SHORT).show();
						
					}
				else
					{
					
						Log.d(TAG, "Files are different, downloading will start.");
						
						return true;
						
						
						
					}

			}
			catch(Exception e) 
			{
				Log.d(TAG, "Update quick check exception, something happened, either no file or interruption to request!", e);
				return false;
			}
			
		
	 } 
	 
	 
		
		@Override
		protected String doInBackground(String... params)
		{
			
			
			try
			{
			
			if(quickcheckupdate(clipinfoURL, clipinfotxtLocation))
			{
				DownloadService.startDownload(clipinfoURL, "clipinfo.txt");
				
			}
			
			if(quickcheckupdate(clipszipURL, clipszipLocation))
			{
				DownloadService.startDownload(clipszipURL, "clips.zip");
				
			}
			
			if(quickcheckupdate(englishclipszipURL, englishzipLocation))
			{
			
				DownloadService.startDownload(englishclipszipURL, "english.zip");
				
				
			}
		}
		catch(Exception e) 
			{
				Log.i(TAG, "Exception thrown in the querying of the update check", e);
			}
	

		if(check_point == 3) 
		  {
		   //Toast.makeText(getApplicationContext(), "No new updates!", Toast.LENGTH_SHORT).show(); 
		   Log.i(TAG, "No new updates for any of the three files!");
		   
		  }

			
			return null;
			}



protected void onPostExecute(String result, Context ctx)
		{		    
		    	 

		}	

	


	
}
		