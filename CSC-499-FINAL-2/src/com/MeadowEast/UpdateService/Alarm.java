package com.MeadowEast.UpdateService;

import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;


public class Alarm extends BroadcastReceiver 
{
	
	private static final String TAG = "Alarm BroadcastReceiver";
	
     @Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         Log.d(TAG, "made it to ALARM");
         wl.release();
        
         try 
	        {
				new CheckUpdate().execute();
				
			} 
         catch (IOException e) 
	        {
				
				e.printStackTrace();
				
			}
         
         
     }

 public void SetAlarm(Context context)
 {
     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
     Intent i = new Intent(context, Alarm.class);
     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
     
     if (!isNetworkAvailable(context))
	 	{
	      	am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60 * 4, pi); // Millisec * Second * Minute * Hours
	      	//am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); //Test with 1 minute intervals
	      	Log.d(TAG, "Alarm set for 4 hours, no internet!");
	 	
	 	}
     else
	     {	
		     am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60 * 24, pi); // Millisec * Second * Minute * Hours
		     //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); //Test with 1 minute intervals
		     Log.d(TAG, "Alarm set for 24 hours!");  
	     }
 }

 public void CancelAlarm(Context context)
	 {
	     Intent intent = new Intent(context, Alarm.class);
	     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	     alarmManager.cancel(sender);
	 }
 
 public boolean isNetworkAvailable(Context context) 
	{
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
 
}