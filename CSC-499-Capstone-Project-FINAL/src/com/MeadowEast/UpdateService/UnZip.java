package com.MeadowEast.UpdateService;


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.MeadowEast.audiotest.MainActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;




public class UnZip
{
	private static final String TAG = "UnZip Class";
	
	static String path = "/Android/data/com.MeadowEast.audiotest/files/";
	
	static String mainPath = "/Android/data/com.MeadowEast.audiotest/files/";
	
	static File zFILE = new File(mainPath, "clips.zip");
	
	 	
	
public UnZip()
	{
		

	}	

public static boolean isZipValid(final File file) 
{
    ZipFile zipfile = null;
    
    try 
    {
    	
        zipfile = new ZipFile(file);
        return true;
    } 
    catch (ZipException e)
    {
        return false;
    } 
    catch (IOException e) 
    {
        return false;
    } 
    finally 
    {
        try 
        {
            if (zipfile != null) 
            {
                zipfile.close();
                zipfile = null;
            }
        } 
        catch (IOException e)
        {
        	
        }
    }
}



public static void startzip(String pathzip, String folderpath, Context ctx)
{
	
	File zipFile = new File(pathzip);
	Log.d(TAG, "INSDIE of startzip path is " + zipFile);
	
	if(isZipValid(zipFile))
	{
		MainActivity.mProgressDialog = new ProgressDialog(ctx);
	
		MainActivity.mProgressDialog.setMessage("Please Wait while updating...");
	
		MainActivity.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	
		MainActivity.mProgressDialog.setCancelable(false);
	
		MainActivity.mProgressDialog.show();
		
		Log.d(TAG, "Made it to StartZIp");
		
		new UnZipTask().execute(pathzip, folderpath);
	}
	else
	{
		
		Log.d(TAG, "Made it to StartZIp FALSE");
		Toast.makeText(ctx, "Zip related error", Toast.LENGTH_SHORT).show();
	}
	
	
}

}
	
	
