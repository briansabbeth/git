package com.MeadowEast.UpdateService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.MeadowEast.audiotest.MainActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


public class DownloadService 
{
	
	private static final String TAG = "DownloadService";
	

	public static final String URL = "urlpath";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.MeadowEast.UpdateService";
	
	static long lastDownload = -1L;
	//static DownloadManager mgr;
	
   
	public DownloadService ()
	{
	}
		 
		 
public static void startDownload(String url, String name) 
	{

		Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).mkdirs();
		File sdCard = Environment.getExternalStorageDirectory();
		File f = new File(sdCard.getAbsolutePath()
				+ "/Android/data/com.MeadowEast.audiotest/files/clips");
		f.mkdirs();

		lastDownload = MainActivity.mgr.enqueue(new DownloadManager.Request(Uri.parse(url))
				.setAllowedNetworkTypes(
						DownloadManager.Request.NETWORK_WIFI
								| DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false)
				.setTitle(name)
				.setDescription("Update files for Hanzi")
				.setDestinationInExternalPublicDir(
						"/Android/data/com.MeadowEast.audiotest/files/", name));
	}
	

public static BroadcastReceiver onComplete = new BroadcastReceiver() 
{
		@Override
		public void onReceive(Context ctxt, Intent intent)
		{

			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
			{

				Bundle extras = intent.getExtras();
				DownloadManager.Query q = new DownloadManager.Query();
				q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
				Cursor c = MainActivity.mgr.query(q);

				if (c.moveToFirst()) 
				{
					int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
					
					if (status == DownloadManager.STATUS_SUCCESSFUL) 
					{
						// process download
						String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
						// get other required data by changing the constant
						// passed to getColumnIndex
						
						String clipszip = "clips.zip";
						String englishzip = "english.zip";
						
						if (title.equals(clipszip) ) 
						{
							
							//String path=android.os.Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.MeadowEast.audiotest/files/";
							//new UnZipTask().execute(path + "clips.zip", path + "clips/");
							
							String zipPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips.zip";
							String clipsPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips/";
							
							Log.d(TAG, "zipPath" + zipPath);
							
							
							UnZip.startzip(zipPath, clipsPath, ctxt);
							
							Toast.makeText(ctxt,"Finished Downloading " + title,Toast.LENGTH_SHORT).show();	
						}
						else if (title.equals(englishzip))
						{
							String englishzipPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/english.zip";
							String englishclipsPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/";
							
							Log.d(TAG, "englishzipPath " + englishzipPath);
							
							
							UnZip.startzip(englishzipPath, englishclipsPath, ctxt);
							
							Toast.makeText(ctxt,"Finished Downloading " + title,Toast.LENGTH_SHORT).show();	
						}
						else
						{
							//MainActivity.cliplist = MainActivity.clipDir.list();
							//MainActivity.readClipInfo();
						}
	
						
					}
				 }
			}
		}
		
	};
	
}	
	
	
	




	
	

