package com.MeadowEast.UpdateService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.os.AsyncTask;
import android.util.Log;

import com.MeadowEast.audiotest.MainActivity;

public class UnZipTask extends AsyncTask<String, Void, Boolean> 
{

	
	private static final String TAG = "UnZipTask AsyncTask";
	
	
		@SuppressWarnings("rawtypes")
		@Override
		protected Boolean doInBackground(String... params) 
		{
			String filePath = params[0];
			String destinationPath = params[1];
			
			Log.d(TAG, "Made it to UNZIPTASK");
			
			File archive = new File(filePath);
			
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, destinationPath);
				}

				UnZipUtil d = new UnZipUtil(filePath, destinationPath);
				d.unzip();

			} catch (Exception e) 
			{
				return false;
			}

			return true;
		}
	
		@Override
		protected void onPostExecute(Boolean result) 
			{

			MainActivity.mProgressDialog.dismiss();
			MainActivity.cliplist = MainActivity.clipDir.list();
			MainActivity.readClipInfo();
			//MainActivity.initializeAvailableClips();
			}

		
		
private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException 
			{

		if (entry.isDirectory()) 
			{
				createDir(new File(outputDir, entry.getName()));
				return;
			}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) 
			{
				createDir(outputFile.getParentFile());
			}

		Log.v("", "Extracting: " + entry);
		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try 
			{
	
			} 
		finally 
			{
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			}
	}

private void createDir(File dir) 
		{
			if (dir.exists()) 
				{
					return;
				}
			if (!dir.mkdirs()) 
				{
					throw new RuntimeException("Can not create dir " + dir);
				}
		}
	
}



