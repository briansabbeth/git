package com.MeadowEast.audiotest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.MeadowEast.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import com.MeadowEast.Settings.PrefsActivity;
import com.MeadowEast.Settings.StatsActivity;
import com.MeadowEast.UpdateService.CheckUpdate;
import com.MeadowEast.UpdateService.DownloadService;
import com.MeadowEast.UpdateService.UnZip;
import com.MeadowEast.dbOpenHelper.TingshuoDatasource;
import com.MeadowEast.dbOpenHelper.TingshuoHistDatasource;
import com.MeadowEast.model.HistoryModel;
import com.MeadowEast.model.Model;
/**
 *@author bjsabbeth
 *@author Jonathan Kosar
 */
public class MainActivity extends Activity  implements OnClickListener,
		OnLongClickListener, OnGesturePerformedListener,  OnGestureListener  {

	public static long NSJtotalTime;
    private int turnCount;
	public static ProgressDialog mProgressDialog;
	private GestureLibrary gLibrary;
	private MediaPlayer mp;
	public static String[] cliplist;
	//public static String[] englishcliplist;
	public File sample;
	private static File mainDir;
	private static File englishDir;
	public static File clipDir;
	private File zipFILE;
	private static File file;
	private Random rnd;
	private Handler clockHandler;
	private Runnable updateTimeTask;
	private boolean clockRunning;
	private boolean clockWasRunning;
	private Long elapsedMillis;
	private Long start;
	public static Map<String, String> hanzi;
	private static Map<String, String> instructions;
	private String key;
	static final String TAG = "MainActivity";
	public static String passkey;
	ShareActionProvider mShareActionProvider ;
	GestureDetector detector;
	public int index_getClip;
	public LinkedList<String> HistLList;
	private Handler mHandler;
	private int delayTime;
	static Context context;
	boolean isNightMode = false;
	
	public static final int progress_bar_type = 0;
	
	 	private String[] drawerListViewItems;
	 	public String[] testStringList;  
	 	
	    private DrawerLayout drawerLayout;
	    private ListView drawerListView;
	    private ActionBarDrawerToggle actionBarDrawerToggle;
	    
	    public static DownloadManager mgr;
		long lastDownload = -1L;

		// String Url="http://www.meadoweast.com/capstone/clips.zip";
		String Urltxt = "http://www.meadoweast.com/capstone/clipinfo.txt";
		String unzipLocation = Environment.getExternalStorageDirectory() + "/clips/";
		String StorezipFileLocation = Environment.getExternalStorageDirectory()	+ "/Clips.zip";
		String StoretxtFileLocation = Environment.getExternalStorageDirectory()	+ "/Android/data/com.MeadowEast.audiotest/files/clipinfo.txt";
		String DirectoryName = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips/";
		String DirectoryName1 = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/";
	
		public List<String> histlist = new ArrayList<String>();
//	
//	/********DATABASE VARIABLE*******************************/
	private static final String LOGTAG = "TINGSHOU_DB ";
	TingshuoDatasource datasource;
	private ArrayList<String> availableClips;
	private ArrayList<String> englishavailableClips;
	private ArrayList<String> probabilityArray;
	public ArrayList<String> ENGLISH_CLIP_ARRAY;
	String currentClip;
	String lastClip;
	boolean sameTurn;
	//a char representing  the previous command issued by the user
	//R = repeat, S = swipe(play next), B = back;
	//In the case of a repeat, we will know if we are in the same turn by 
	//checking what the last turn is.
	char lastTurn; 
	/********DATABASE VARIABLE*******************************/
 //	

	///Shared Preferences
public int current_tick;
public int prev_clip_index;
public int current_clip_index;
public ArrayList<String> cliplistwithrepeats;
private static final String SHAREDPREF_SET = null;
private static final String SHAREDPREF_CLIP_INDEX = null;
private static final String SHAREDPREF_TOTAL_SECONDS = null;
private static final String SHAREDPREF_ENG_CLIP_INDEX = null;	
	
	/********DATABASE VARIABLE*******************************/
////history table
TingshuoHistDatasource hist_datasource;

//////////////////////////////////////////////////////////
	


	
	
	/*private final Runnable mUpdateUI = new Runnable() 
	{
	    @Override
		public void run()
	    {
	    	boolean alarmUp = (PendingIntent.getBroadcast(getBaseContext(), 0, 
	    	        new Intent("com.MeadowEast.UpdateService.Autostart"), 
	    	        PendingIntent.FLAG_NO_CREATE) != null);

	    	if (alarmUp)
	    	{
	    		Log.d(TAG, "Alarm is already active");
	    	}
	    	
	    	
	    	if (!isNetworkAvailable())
	    	{
	    		mHandler.postDelayed(mUpdateUI, 21600000);
	    		Toast.makeText(MainActivity.this, "No internet for Daily update check, try again in little!", Toast.LENGTH_SHORT).show();
		    	
	    	}
	    	else if (!alarmUp)
		    	{
	    		
	    		Alarm alarm = new Alarm();
				alarm.SetAlarm(getBaseContext());
				Log.d(TAG, "Alarm was not active but now is!");
				//mHandler.postDelayed(mUpdateUI, 86400000);
				
				//Toast.makeText(MainActivity.this, "Daily update check!", Toast.LENGTH_LONG).show();
		    	}
	    	else
	    	{
	    		Log.d(TAG, "Alarm is already active and internet");
	    	}
	        
	    }
	    
	};
	*/
	
	
	
	/*private final Runnable mUpdateUi = new Runnable(){
	    public void run(){
	        check();
	    }

	};

	private void start(){
	    new Thread(
	        new Runnable(){
	            public void run(){
	            	Log.d(TAG, "inside start");
	                Looper.prepare();
	                mHandler = new Handler();
	                check();
	                Looper.loop();
	            }
	        }
	    ).run();
	}


	private void check(){
	    if (isNetworkAvailable()== true){
	        try {
	            new checkupdate().execute();
	            delayTime = 86400000;
	            Toast.makeText(MainActivity.this, "Daily update check!", Toast.LENGTH_SHORT).show();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	            delayTime = 21600000;
	        }
	    }else{
	        delayTime = 21600000;
	        Toast.makeText(MainActivity.this, "No internet for Daily update check, try again in little!", Toast.LENGTH_SHORT).show();
	    }
	    reCheck();
	}

	private void reCheck(){
	    mHandler.postDelayed(mUpdateUi, delayTime);
	}*/
	

	
public static  void readClipInfo() {

		hanzi = new HashMap<String, String>();
		
		instructions = new HashMap<String, String>();
  
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
              
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
	       
	    String entryvalue = sharedPreferences.getString( "language_key", "");
	    Log.d(TAG, "Entryvalue " + entryvalue);
	      
	      
		Log.d(TAG, "beforeenglish" + entryvalue);
		
		File sdCard = Environment.getExternalStorageDirectory();
		mainDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/"); 
		
		englishDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/english/");

		if (entryvalue.equals("EN"))
	    {

		file = new File(englishDir, "clipinfo.txt");
		
		Log.d(TAG, "inside english readclipinfo " + entryvalue);
		
		try 
		{
		FileReader fr = new FileReader(file);
		BufferedReader in = new BufferedReader(fr);
		String line;
			while ((line = in.readLine()) != null)
			{
				String fixedline = new String(line.getBytes());
				String[] fields = fixedline.split("\\t");
				if (fields.length == 3)
					{
					hanzi.put(fields[0], fields[1]);
					Log.e(TAG, "BJS HANZI FIELD " + fields[1] + " " + fields[0] );
			
					instructions.put(fields[0], fields[2]);
					} 
				else 
					{
					Log.d(TAG, "Bad line: " + fields.length + " elements");
					Log.d(TAG, fixedline);
					}
			}
			
			in.close();
			
		} 
		catch (Exception e) 
		{
		Log.d(TAG, "Problem reading english clipinfo");
		}
		
		Log.d(TAG, "afterenglish");

		}
		else
		{

		file = new File(mainDir, "clipinfo.txt");
		Log.d(TAG, "before else readclipfinfo " + entryvalue);

		try {
				FileReader fr = new FileReader(file);
				BufferedReader in = new BufferedReader(fr);
				String line;
				while ((line = in.readLine()) != null) 
					{
						String fixedline = new String(line.getBytes(), "utf-8");
						String[] fields = fixedline.split("\\t");
						if (fields.length == 3) {
						hanzi.put(fields[0], fields[1]);
						instructions.put(fields[0], fields[2]);
					} 
				else 
					{
						Log.d(TAG, "Bad line: " + fields.length + " elements");
						Log.d(TAG, fixedline);
					}
				}
				in.close();
			}
		catch (Exception e) 
			{
				Log.d(TAG, "before else readclipfinfo " + entryvalue);
			}

		Log.d(TAG, "after");

		}
		
	
	
}


	private String getInstruction(String key) {
		Log.i(LOGTAG, "BJS getInstructions(key), key =  "+  key);
		String instructionCodes = instructions.get(key);
		int n = instructionCodes.length();
		if (n == 0) {
			return "No instruction codes for " + key;
		}
		int index = rnd.nextInt(n);
		Log.i(LOGTAG, "BJS getInstructions().index "+  key);
		switch (instructionCodes.charAt(index)) {
		case 'C':
			return "continue the conversation";
		case 'A':
			return "answer the question";
		case 'R':
			return "repeat";
		case 'P':
			return "paraphrase";
		case 'Q':
			return "ask questions";
		case 'V':
			return "create variations";
		default:
			return "Bad instruction code " + instructionCodes.charAt(index)
					+ " for " + key;
		}
	}

	private void toggleClock() {
		if (clockRunning) {
			elapsedMillis += System.currentTimeMillis() - start;
			//calcTime();
			setHanzi("");
		} else
			start = System.currentTimeMillis();
		clockRunning = !clockRunning;
		clockHandler.removeCallbacks(updateTimeTask);
		if (clockRunning)
			clockHandler.postDelayed(updateTimeTask, 200);
	}

	private void calcTime(){
		//Holds value for total weekly time
		ArrayList<Long> weekly = new ArrayList();
		//hold values for integers from file
		ArrayList<String> a = new ArrayList();
		File sdCard = Environment.getExternalStorageDirectory();
		File f = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/values.txt");
		File sdCard2 = Environment.getExternalStorageDirectory();
		File g = new File(sdCard2.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/array.txt");
		//get values
		try {
			FileInputStream fis;
			fis = new FileInputStream(f);
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					a.add(line);
					//System.out.println(line);
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				//f.delete();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		int index = Integer.parseInt(a.get(0));
		int pDay = Integer.parseInt(a.get(1));
		int pHour = Integer.parseInt(a.get(2));
		int pMinute = Integer.parseInt(a.get(3));
		//System.out.println("Index: " + index);
		//System.out.println("pDay: " + pDay);
		//System.out.println("pHour: " + pHour);
		//System.out.println("pMinute: " + pMinute);
		int temp3 = index;
		//hold previous values
		ArrayList<String> a2 = new ArrayList();
		//get previous values from text file
		try {
			FileInputStream fis2;
			fis2 = new FileInputStream(g);
			//Construct BufferedReader from InputStreamReader
			BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
			String line = null;
			try {
				while (((line = br2.readLine()) != null) && (temp3 != 0)) {
					a2.add(line);
					temp3 = temp3 - 1;
					//System.out.println(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	 
		try {
			//f.delete();
			br2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//System.out.println("LOOOOOOOOOOOOOOOK");
		for(int i = 0; i<index; ++i){
			weekly.add((long)Integer.parseInt(a2.get(i)));
		}
		Calendar currentDate = Calendar.getInstance();
		int cDay = currentDate.get(Calendar.DAY_OF_WEEK);
		int cHour = currentDate.get(Calendar.HOUR_OF_DAY);
		int cMinute = currentDate.get(Calendar.MINUTE);
		
		System.out.println("cDay: " + cDay);
		System.out.println("cHour: " + cHour);
		System.out.println("cMinute: " + cMinute);
		
		//Same Day
		long totalTime = 0;
		for(int i = 0; i < index; ++i){
			totalTime = totalTime + weekly.get(i);
		}
		long currentTime = elapsedMillis - totalTime;
		System.out.println(currentTime);
		if (cDay == pDay){
			//same hour
			if (cHour == pHour){
				//same minute
				if (cMinute == pMinute){
					//System.out.println("here1");
					//add timeElapsed to array
					//System.out.println("Time elapsed: " + elapsedMillis);
					weekly.add(index, currentTime);
					//This function will make sure each 
					//index does not have more than 60 seconds
					//temporary index value
					int i = index;
					//if value in index is over 1000ms (60s)
					while (weekly.get(i) > 1000){
					//subtract 1000ms from index
					int temp = (int) (weekly.get(i) - 1000);
					//save 1000ms in index
					weekly.add(i, (long) 1000);
					//increment index
					i = i + 1;
					//save rest of time to next index and repeat
					weekly.add(i, (long) temp);
					//System.out.println(weekly.get(i));
				}
				for (int x = 0; x <= i; ++x) {
					System.out.println(weekly.get(x));
				}
				//update value for index
				//System.out.println("here2");
				//System.out.println(weekly.get((int) index));
				//System.out.println("here3");
				//System.out.println("here4");
				index = i;
				++index;
			}
		}
	}
	
	//Same Day
	if (cDay == pDay){
		//same hour
		if (cHour == pHour){
			//DIFFERENT minute
			if (cMinute != pMinute){
				//calcualte the difference 
				//System.out.println("here5");
				int difference = (cMinute - pMinute);
				//System.out.println("here6");
				int tempIndex = 0;
				//System.out.println("here7");
				//find value of latest index
				tempIndex = index + difference;
				//System.out.println("here8");
				//if value of one week is exceeded start removing from top
				if (tempIndex > 10079){
					//System.out.println("here9");
					tempIndex = tempIndex - 1;
					//System.out.println("here9.5");
					weekly.remove(0);
				}
				//System.out.println("here10");
				//fill in value of 0 for indexes where no time is used
				for (int i = (index /*+ 1*/); i < tempIndex; ++i){
					//System.out.println("here11");
					weekly.add(i, (long) 0);
				}
				//System.out.println("here12");
				//update index with latest value
				index = tempIndex;
				//System.out.println("here13");
				weekly.add(index, currentTime);
				System.out.println("here14");
				//Log.i(LOGTAG, "Variable updated" + weekly.get((int) index));
				//System.out.
				//System.out.println("here15");
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
					//System.out.println(weekly.get(i));
				}
				for (int x = 0; x <= i; ++x) {
					//System.out.println(weekly.get(x));
				}
				//System.out.println("here16");
				index = i;
				++index;
			}
		}
	}
	//Log.i(LOGTAG, "Variable updated" + weekly.get((int) index));
	
	//Same Day
	if (cDay == pDay){
		//different hour
		if (cHour != pHour){
			int difference = cHour - pHour;
			//convert hour to minutes
			difference = difference * 60;
			//SAME Minute
			if (cMinute == pMinute){
				int tempIndex = 0;
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex - 1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
			//DIFFERENT Minute
			if (cMinute != pMinute){
				int tempIndex = 0;
				difference = difference + (cMinute - pMinute);
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex -1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
		}
	}
	
	//Different Day 
	if (cDay != pDay){
		//convert days to hours to minutes
		int difference = (cDay - pDay) * 24 * 60;
		//Same hour
		if (cHour == pHour){
			//same minute
			if (cMinute == pMinute){
				int tempIndex = 0;
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex - 1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
			//different minute
			if (cMinute != pMinute){
				int tempIndex = 0;
				difference = difference + (cMinute - pMinute);
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex - 1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
		}
		//different hour
		if (cHour != pHour){
			difference = difference + ((cHour - pHour) * 60);
			//same minute
			if (cMinute == pMinute){
				int tempIndex = 0;
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex - 1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
			//different minute
			if (cMinute != pMinute){
				int tempIndex = 0;
				difference = difference + (cMinute - pMinute);
				tempIndex = index + difference;
				if (tempIndex > 10079){
					tempIndex = tempIndex - 1;
					weekly.remove(0);
				}
				for (int i = index + 1; i < tempIndex; ++i){
					weekly.add(i, (long) 0);
				}
				index = tempIndex;
				weekly.add(index, currentTime);
				int i = index;
				while (weekly.get(i) > 1000){
					int temp = (int) (weekly.get(i) - 1000);
					weekly.add(i, (long) 1000);
					i = i + 1;
					weekly.add(i, (long) temp);
				}
				index = i;
				++index;
			}
		}
	}
	NSJtotalTime = 0;
	for (int i = 0; i < index; ++i){
		NSJtotalTime = NSJtotalTime + weekly.get(i);
	}
	System.out.println("The total time is: " + NSJtotalTime);
	//save back to file
	try {
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);
		pw.println(index);
		pw.println(cDay);
		pw.println(cHour);
		pw.println(cMinute);
		pw.close();
		//Log.d(TAG, "calcTime succesful!");
	} catch (IOException e) {
		Log.d(TAG, "calcTime Error!");
	}
	//save back to file
	try {
		g.delete();
		FileWriter fw2 = new FileWriter(g);
		PrintWriter pw2 = new PrintWriter(fw2);
		for(int i = 0; i<index; ++i){
			pw2.println(weekly.get(i));
			//Log.d(TAG, "calcTime succesful!");
		}
		pw2.close();
		//Log.d(TAG, "calcTime Succesful!");
	} catch (IOException e) {
		Log.d(TAG, "calcTime Error!");
	}
	}
	private void showTime(Long totalMillis) {
		int seconds = (int) (totalMillis / 1000);
		int minutes = seconds / 60;
		seconds = seconds % 60;
		TextView t = (TextView) findViewById(R.id.timerTextView);
		if (seconds < 10)
			t.setText("" + minutes + ":0" + seconds);
		else
			t.setText("" + minutes + ":" + seconds);
	}

	private void createUpdateTimeTask() {
		updateTimeTask = new Runnable() {
			@Override
			public void run() {
				Long totalMillis = elapsedMillis + System.currentTimeMillis()
						- start;
				showTime(totalMillis);
				clockHandler.postDelayed(this, 1000);
			}
		};
	}

	private void setHanzi(String s) {
		TextView t = (TextView) findViewById(R.id.hanziTextView);
		t.setText(s);
	}
	
	private String getHanzi(String s)
		{
		return hanzi.get(s);
		}


	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Log.d(TAG, "testing only");
		// File filesDir = getFilesDir(); // Use on virtual device
		    
		
	
		context = this;
		
//////////////////////////////////Custom Gesutre Overlay//////////////////////////////////        
        
        detector=new GestureDetector(getBaseContext(), this);
		 
		gLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gLibrary.load()) {
            finish();
       }  
        
        GestureOverlayView gOverlay = 
                (GestureOverlayView) findViewById(R.id.gestureOverlayView1);
            gOverlay.addOnGesturePerformedListener(this); 
            
           gOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
            gOverlay.setGestureColor(Color.TRANSPARENT);
            gOverlay.setUncertainGestureColor(Color.TRANSPARENT);
            
  //////////////////////////////////Custom Gesutre Overlay////////////////////////////////// 
            
            SharedPreferences sp = getSharedPreferences("MyPref", 0);
            String hexaColor = sp.getString("hexa", "0xff000000");
            
            //mHandler.post(mUpdateUI);
    		//start();
            
			
    		mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    		
    		registerReceiver(DownloadService.onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    		
    		 /**********************DATABASE CODE onCreate()******************************************/
    	     //////History Table
    	          Log.i(LOGTAG, "BJS ABOUT TO OPEN HISTDATASOURCE");
    	          hist_datasource = new TingshuoHistDatasource(this);
    	          hist_datasource.open();
    	          Log.i(LOGTAG, "BJS OPENED HISTDATASOURCE");
    	          outputHistModelList();


    	    
    	    
    	     //////Main Table
    	    
    	     lastTurn = '0';
    	     turnCount = 0;
    	          //sameTurn = false;
    	     cliplistwithrepeats = new ArrayList<String>();
    	     availableClips = new ArrayList<String>();
    	     englishavailableClips = new ArrayList<String>();
    	          probabilityArray = new ArrayList<String>();
    	          ENGLISH_CLIP_ARRAY = new ArrayList<String>();
    	          Log.i(LOGTAG, "ABOUT TO OPEN DATASOURCE");
    	            
    	datasource = new TingshuoDatasource(this);
    	datasource.open();
    	List<Model> modelList = datasource.findAll();





    	   /************************END DATABASE CODE*****************************************/


		
	
		
	  
        

		File sdCard = Environment.getExternalStorageDirectory();
		File f = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/");
		f.mkdirs();
		
		
		//boolean alarmUp = (PendingIntent.getBroadcast(getBaseContext(), 0, new Intent("AlarmService"),  PendingIntent.FLAG_NO_CREATE) != null);
	/*	boolean alarmUp1 =isServiceAlarmOn(this);
		//Log.d(TAG, "Alarm is " +  alarmUp1 );
		//startService(new Intent(this, AlarmService.class));
		if (!isServiceAlarmOn(this))
    	{

            Alarm alarm = new Alarm();
			alarm.SetAlarm(getBaseContext());
    		Log.d(TAG, "Alarm is not set " + alarmUp1 );
    	}
		else
		{
			Log.d(TAG, "initializationcheck and Alarm is already active " + alarmUp1 );
			initializationcheck();
		}*/
		
		
		initializationcheck();
		mainDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/");
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
       
		
        String pref = sharedPreferences.getString( "language_key", "");
        
        englishDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/english/");
        
        Log.i(TAG,"Startup string value " + pref); 
        
        try 
        {
        if (pref.equals("EN"))
		{
			clipDir = new File(englishDir, "clips");
			cliplist = clipDir.list();
			//Toast.makeText(getApplicationContext(),"HELLO FROM PREF! ", Toast.LENGTH_SHORT).show();
			Log.i(LOGTAG,"Startup String value inside EN" + pref); 
		}
		else
		{
		clipDir = new File(mainDir, "clips");
		cliplist = clipDir.list();
		Log.i(LOGTAG,"Startup String value inside else " + pref); 
	
		}
		
		readClipInfo();
		rnd = new Random();
		 /**********************DATABASE CODE******************************************/
		Log.i(LOGTAG, " BJS CLIPLISTLENGTH "+ cliplist.length + " initializingclips()");
		initializeAvailableClips();
		        modelList = datasource.findAll();
		        Log.i(LOGTAG,"BJS Outputting the modellist");
		        outputModelList(modelList);	
		        updateSlideHistList();
		  /*********************END DATABASE CODE***************************************************************/
        }
        catch (Exception e) 
		{
        	
		}
        
        
		
		/*findViewById(R.id.playButton).setOnClickListener(this);
		findViewById(R.id.repeatButton).setOnClickListener(this);
		findViewById(R.id.hanziButton).setOnClickListener(this);*/
		findViewById(R.id.timerTextView).setOnClickListener(this);
		findViewById(R.id.hanziTextView).setOnLongClickListener(this);
		
		
		clockHandler = new Handler();
		start = System.currentTimeMillis();
		elapsedMillis = 0L;
		clockRunning = false;
		createUpdateTimeTask();
	/*	findViewById(R.id.pauseButton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						toggleClock();
					}
				});*/
		if (savedInstanceState != null) {
			elapsedMillis = savedInstanceState.getLong("elapsedMillis");
			Log.d(TAG, "elapsedMillis restored to" + elapsedMillis);
			key = savedInstanceState.getString("key");
			String sampleName = savedInstanceState.getString("sample");
			if (sampleName.length() > 0)
				sample = new File(clipDir, sampleName);
			if (savedInstanceState.getBoolean("running"))
				toggleClock();
			else
				showTime(elapsedMillis);
			Log.d(TAG, "About to restore instruction");
			String instruction = savedInstanceState.getString("instruction");
			if (instruction.length() > 0) {
				Log.d(TAG, "Restoring instruction value of " + instruction);
				TextView t = (TextView) findViewById(R.id.instructionTextView);
				t.setText(instruction);
			}

		}
		
		
		//Slide Menu Stuff	
		 // get list items from strings.xml
       drawerListViewItems = getResources().getStringArray(R.array.items);
          
       // get ListView defined in activity_main.xml
       drawerListView = (ListView) findViewById(R.id.left_drawer);

       // Set the adapter for the list view
      drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_listview_item, histlist));
       // 2. App Icon
       drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

       // 2.1 create ActionBarDrawerToggle
       actionBarDrawerToggle = new ActionBarDrawerToggle(
               this,                  // host Activity 
               drawerLayout,         // DrawerLayout object 
               R.drawable.ic_launcher,  // nav drawer icon to replace 'Up' caret 
               R.string.drawer_open,  // "open drawer" description 
               R.string.drawer_close  // "close drawer" description 
               );

       // 2.2 Set actionBarDrawerToggle as the DrawerListener
       drawerLayout.setDrawerListener(actionBarDrawerToggle);

       // 2.3 enable and show "up" arrow
       getActionBar().setDisplayHomeAsUpEnabled(true);

       // just styling option
       drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

       drawerListView.setOnItemClickListener(new DrawerItemClickListener());
       
       drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	
		
	}
	
	public static boolean isServiceAlarmOn(Context context)
	{
		Intent i = new Intent(context, com.MeadowEast.UpdateService.Alarm.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		Log.d(TAG, "AlarmOn check is  " + pi );
		return pi != null;
		
	}
	private class DrawerItemClickListener implements ListView.OnItemClickListener 
	{
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
        	
        	Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
        	CharSequence clipIDSlideMenu = ((TextView)view).getText();
        	
        	Log.d(TAG, "From slidemenu the full string that was selected was " +clipIDSlideMenu);
        	String clipsID = (String) clipIDSlideMenu.subSequence(0,5);
        	
        	Log.d(TAG, "From slidemenu the clip that was selected was " + clipsID );
        	
        	key = clipsID;

        	Log.d(TAG, "key is " + key);
    
            String mp3file = key + ".mp3";
        
            try
            {
            	sample = new File(clipDir, mp3file);
            	
            	Log.d(TAG, "Sample After " + sample);
            	
            	
        	if (!clockRunning)
    			toggleClock();
    		
    		if (sample != null) 
    		{
    			setHanzi("");
    			if (mp != null) 
    			{
    				mp.stop();
    				mp.release();
    			}
    			mp = new MediaPlayer();
    			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    			try 
    			{
    				mp.setDataSource(getApplicationContext(),
    						Uri.fromFile(sample));
    				mp.prepare();
    				mp.start();
    			} 
    			catch (Exception e) 
    			{
    				Toast.makeText(MainActivity.this, "Wrong language, try switching in settings!", Toast.LENGTH_SHORT).show();	
    				Log.d(TAG, "Couldn't get mp3 file");
    			}
    		} 
    	
    	

            databaseSwipeHandler();
            
            createAndInsertHistModel();
           
            updateSlideHistList(); 
            }
            catch (Exception e) 
        	{
        		initializationcheck();
        	}
            drawerLayout.closeDrawer(drawerListView);
 
        }
    }
	
	@Override
	public void onPause()
	{
		
		Log.d(TAG, "!!!! onPause is being run");
		clockWasRunning = clockRunning;
		if (clockRunning)
			toggleClock();
		datasource.close();
		unregisterReceiver(DownloadService.onComplete);
		//unregisterReceiver(BroadcastReceiver Alarm);
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		String sampleName = "";
		if (sample != null)
			sampleName = sample.getName();
		outState.putString("sample", sampleName);
		// onPause has stopped the clock if it was running, so we just save
		// elapsedMillis
		outState.putLong("elapsedMillis", elapsedMillis);
		TextView t = (TextView) findViewById(R.id.instructionTextView);
		outState.putString("instruction", t.getText().toString());
		outState.putString("key", key);
		outState.putBoolean("running", clockWasRunning);
	}

	public void reset() 
	{
		TextView t;
		if (clockRunning)
			toggleClock();
		start = 0L;
		elapsedMillis = 0L;
		sample = null;
		t = (TextView) findViewById(R.id.timerTextView);
		t.setText("0:00");
		setHanzi("");
		t = (TextView) findViewById(R.id.instructionTextView);
		t.setText("");
	}

	@Override
	public boolean onLongClick(View v)
	{
		repeatHandler();
		
		switch (v.getId()) 
		{
		case R.id.hanziTextView:
			Toast.makeText(this, "Clip: " + key, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Long clicked");
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
	
		case R.id.timerTextView:
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.reset)
					.setMessage(R.string.reallyReset)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.reset();
								}
							}).setNegativeButton(R.string.no, null).show();
			break;
		}
	}


	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "llkj");
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.quit)
					.setMessage(R.string.reallyQuit)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.finish();
								}
							}).setNegativeButton(R.string.no, null).show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // actionBarDrawerToggle.syncState();
        
       /* Log.i(TAG,"Before Alarm"); 
        	
		
	  
	  	
	  	Alarm alarm = new Alarm();
	  	alarm.SetAlarm(getBaseContext());
		
	  	Log.i(TAG,"After ALarm");*/
    }
 
	 @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
public boolean onOptionsItemSelected(MenuItem item) 
	{
		
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) 
			{
	            return true;
	        }
		// Take appropriate action for each action item click
		switch (item.getItemId()) 
		{
		case R.id.action_settings:
			startActivity(new Intent(this, PrefsActivity.class));
			
			return true;
		case R.id.action_check_updates:
			if (isNetworkAvailable()== true)
		    	{
					try 
						{
							new CheckUpdate().execute();
						} 
					catch (IOException e) 
						{
							
							e.printStackTrace();
						}
		    	}
			else
				{
					Toast.makeText(MainActivity.this, "No internet for update check, try again in little!", Toast.LENGTH_SHORT).show();
				}

			return true;
		case R.id.action_rewind:
			rewind();
			
			return true;
		case R.id.action_trans:
			if (passkey == null)
			{
				Toast.makeText(MainActivity.this, "Touch press so we know which clip you want!", Toast.LENGTH_SHORT).show();
			}
			else
			{
			startActivity(new Intent(this, DisplayDict.class));
			}
			return true;
		case R.id.action_stats:	
			startActivity(new Intent(this, StatsActivity.class));
			return true;
		case R.id.share:
			getDefaultShareIntent();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
////////////////////////CIRCLE IS REPEAT/////////////////////////
////    ////    ////    ////    ////    ////    ////    ////
	///    ////    ////    ////    ////    ////    ////
////   ////   ////    ////    ////    ////    ////    ////
@Override
public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gLibrary.recognize(gesture);
        //&& predictions.get(0).score > 1.0
        if (predictions.size() > 0 && predictions.get(0).score > 1.0 ) {
            
        	String action = predictions.get(0).name;
        	String hswipe = "hswipe";
        	String U = "u";
        	String circle = "circle";
        	String trig = "trig";
        	String lefthalf = "lefthalf";
        	String halfcir = "halfcir";
        	String threecir = "3cir";
        	String twolines = "twolines";
        	String bigcir = "bigcir";
        	String horovalcir= "horovalcir";
        	String pearcir = "pearcir";
        	String smallcir = "smallcir";
        	String squcir = "squcir";
        	String weirdcir = "weirdcir";
        		
        //if (action.equals( circle )|| action.equals( halfcir )|| action.equals( threecir ) || action.equals( weirdcir )|| action.equals( squcir )|| action.equals( smallcir )|| action.equals( pearcir )|| action.equals( horovalcir )|| action.equals( bigcir ))
        	//Repeat
 
            if ( action.equals( halfcir )|| action.equals( threecir ) )
            {
            
            		rewind();
			
            }
            
            //Pause
            
            if (action.equals( twolines ))
            {
            	Log.i(LOGTAG, "BJS twolines "+  key);
            	//Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            	toggleClock();
            }
        }
    }

void rewind()
{
	Log.i(TAG, " REPEAT "+  key);
 	try 
	{
    Toast.makeText(this, "Repeat", Toast.LENGTH_SHORT).show();	
    
    repeatHandler();
    createAndInsertHistModel();/////inserts to the history
    
    lastTurn = 'R';
	if (!clockRunning)
		toggleClock();
	if (sample != null) 
	{
		setHanzi("");
		if (mp != null) 
			{
				mp.stop();
				mp.release();
			}
				mp = new MediaPlayer();
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
				mp.setDataSource(getApplicationContext(),
						Uri.fromFile(sample));
				mp.prepare();
				mp.start();
			} 
	}
		catch (Exception e) 
			{
				Log.d(TAG, "Couldn't get mp3 file");
				Toast.makeText(this, "Try playing before repeating, please!", Toast.LENGTH_SHORT).show();	
			}


	
	
  }
  



private Intent getDefaultShareIntent()
{
	try
	{
    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    intent.setType("audio/mp3");
  
    intent.putExtra(Intent.EXTRA_SUBJECT, "TingShuo Hanzi Clip Attachment Email" );
    intent.putExtra(Intent.EXTRA_TEXT,hanzi.get(key));
    
    //startActivityForResult(Intent.createChooser(intent, "Send mail"), EMAIL_REQUEST);
   // File root = Environment.getExternalStorageDirectory();
    String name = clipDir.getName();
    Log.d(TAG, name);
        
    //File file = new File(clipDir, key);
    if (!sample.exists() || !sample.canRead())
    {
    	Toast.makeText(this, "Attachment Error, make sure you in the right language mode!", Toast.LENGTH_SHORT).show();
    	//finish();
       //return;
    }
   Uri uri = Uri.parse("file://" + sample);
   intent.putExtra(Intent.EXTRA_STREAM, uri);
   startActivity(intent);
   return intent;
	}
	catch (Exception e) 
	{
		Log.i(LOGTAG, "Share exception", e);
		Toast.makeText(this, "Slide left to play clip first so we know which clip you want to share!", Toast.LENGTH_SHORT).show();
		return null;
		
	}
	
}



public boolean isNetworkAvailable() 
	{
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


        




/*public void nightMode()
{
	
	if(isNightMode)
		{
			isNightMode = false;
			findViewById(R.id.LinearLayout1).setBackgroundColor(0xffffffff);
			((TextView) findViewById(R.id.hanziTextView)).setTextColor(0xff000000);
			((TextView) findViewById(R.id.timerTextView)).setTextColor(0xff000000);
			((TextView) findViewById(R.id.instructionTextView)).setTextColor(0xff000000);
		}
	else
		{
			isNightMode = true;
			findViewById(R.id.LinearLayout1).setBackgroundColor(0xff000000);
			((TextView) findViewById(R.id.hanziTextView)).setTextColor(0xff444444);
			((TextView) findViewById(R.id.timerTextView)).setTextColor(0xff444444);
			((TextView) findViewById(R.id.instructionTextView)).setTextColor(0xff444444);
		}
}*/

private boolean isEmptyDirectory(File file)
{
	if(file.isDirectory())
	{
		 
		if(file.list().length>0)
		{
 
			Log.i(LOGTAG, "Directory is not empty!");
			return false;
 
		}
		else
		{
 
			Log.i(LOGTAG, "Directory is empty!");
			return true;
		}
 
	}
	else
	{
 
		Log.i(LOGTAG, "This is not a directory");
		return false;
	}
	
	
}


void initializationcheck()
	{
	 
	String englishclipsPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/english/";
	String englishzipPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/english.zip";
		
	String clipsPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips/";
	String clipszipPath = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips.zip";
	
	File englishclipDir = new File(englishclipsPath, "clips");
	File clipDir = new File(clipsPath);
	
	File englishfilezip = new File(englishzipPath);
		
	File clipszip = new File(clipszipPath);
		
	try 
	{
	
		if (clipszip.exists() && englishfilezip.exists() )
		{	
			new CheckUpdate().execute();
		}
		
	
			if (!clipszip.exists() || !englishfilezip.exists() )
			{
				Log.d(TAG, "Inside of initializationcheck for if zip exist");
				try 
				{
					new CheckUpdate().execute();
				} 
				catch (IOException e1) 
				{
					
					e1.printStackTrace();
				}
			}
			
			else if (isEmptyDirectory(clipDir) || isEmptyDirectory(englishclipDir))
			{
				
				Log.d(TAG, "Inside of initializationcheck for isEmptyDirectory ");	
				
				if(isEmptyDirectory(clipDir))
				{
					
				
				Log.d(TAG, "zipPath" + clipszipPath);
		
				UnZip.startzip(clipszipPath, clipsPath, MainActivity.this);
				}
				
				if(isEmptyDirectory(englishclipDir))
				{
				Log.d(TAG, "englishzipPath " + englishzipPath);
				
				UnZip.startzip(englishzipPath, clipsPath, MainActivity.this);
				}
				
			}
			
			if (( clipszip.exists() && englishfilezip.exists() ) && ( isEmptyDirectory(clipDir) || isEmptyDirectory(englishclipDir) )  )
			{
				Log.d(TAG, "Inside of initializationcheck for the last if statement");		
				
				Log.d(TAG, "zipPath" + clipszipPath);
		
				UnZip.startzip(clipszipPath, clipsPath, MainActivity.this);
				
				Log.d(TAG, "englishzipPath " + englishzipPath);
				
				UnZip.startzip(englishzipPath, clipsPath, MainActivity.this);
				
			}
			
		/*	else
			{
				Log.i(LOGTAG, "Nothing to do on initializationcheck");
			}*/
		
	} 
	catch (Exception e) 
	{
		Log.i(LOGTAG, "Inside of initializationcheck catach exception", e);
		
		e.printStackTrace();
	}
	
	}




/*@Override
protected void onDestroy()
{
	unregisterReceiver(DownloadService.onComplete);
	
    super.onDestroy();
}
*/


@Override
public boolean onDown(MotionEvent arg0) {
	Log.i(LOGTAG, "BJS on down ");
	//Toast.makeText(getApplicationContext(), "Single Tap Gesture", 100).show();
	return true;
}

private static final int SWIPE_THRESHOLD = 100;
private static final int SWIPE_VELOCITY_THRESHOLD = 100;

@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	///Log.i(LOGTAG, "BJS onFling");

	boolean result = false;
    try {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
        } else {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
            }
        }
    } catch (Exception exception) {
        exception.printStackTrace();
    }
    return result;
}


public void onSwipeRight() {
	Log.d(TAG, "right");
	
}
//////////////  bjsbjsbjsbjsbjs   swipe calls get instructions  bjsbjsbjsbjsbjs  \\\\\\\\\\\\\\\\\
//////////////  bjsbjsbjsbjsbjs   swipe calls get instructions  bjsbjsbjsbjsbjs  \\\\\\\\\\\\\\\\\\
//////////////  bjsbjsbjsbjsbjs   swipe calls get instructions  bjsbjsbjsbjsbjs  \\\\\\\\\\\\\\\\\\\

public void onSwipeLeft() {
	lastTurn = 'S';
	Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
	
	Log.i(TAG, "BJS onSwipeLeft() " + lastTurn);
	
	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);

	 sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
      
     String entryvalue = sharedPreferences.getString( "language_key", "");
     Log.d(TAG, "Entryvalue " + entryvalue);
     

	try{

	
	Log.d(TAG, "Inside Play before English" + entryvalue);
	

	File sdCard = Environment.getExternalStorageDirectory();
	mainDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/"); 
	englishDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/english/");
	
	if (entryvalue.equals("EN"))
	   {
			clipDir = new File(englishDir, "clips");
			cliplist = clipDir.list();
			Log.d(TAG, "Inside EN " + entryvalue);
		
		}
	else
		{
			clipDir = new File(mainDir, "clips");
			cliplist = clipDir.list();
			Log.d(TAG, "Inside ELSE CH " + entryvalue);
			
			
		}
	
	readClipInfo();
	 /*datasource = new TingshuoDatasource(this);
     datasource.open();*/
 	  

	
	
	String test = Integer.toString(cliplist.length);
	
	/*initializeAvailableClips();
    modelList = datasource.findAll();
    Log.i(LOGTAG,"BJS Outputting the modellist"); 
    outputModelList(modelList);	
	String test = Integer.toString(cliplist.length);*/
	
	Log.e(TAG, test);
	//try {
		
	
	Log.e(TAG, test);

	//BJS CHANGES
	/////
	// key = sample.getName();
	// key = key.substring(0, key.length() - 4);
	String test_clip = getClip();
	Log.e(TAG, "BJS onSwipeLeft().testClip() " + test_clip);
	
	key = test_clip;
	
	Log.e(TAG, "BJS onSwipeLeft().testClip() " + key);
	
	String mp3file = key + ".mp3";
    
    Log.e(TAG, "BJS onSwipeLeft().testClip() " + test_clip);    
    
     databaseSwipeHandler();
     createAndInsertHistModel();
    
     updateSlideHistList();
        
      sample = new File(clipDir, mp3file);
      
      Log.e(TAG, "BJS This is index_getclip inside of play " + mp3file);
      Log.e(TAG, "BJS This is key inside of play " + key);
      Log.e(TAG, "BJS This is sample inside of play " + sample);
         
        
      turnCount++;
		//Log.e(TAG, "BJS onSwipeLeft().turnCount = " + turnCount);
		//Log.i(TAG, "BJS onSwipeLeft().key - 4 " + key);
		
      	TextView t = (TextView) findViewById(R.id.instructionTextView);
		t.setText(getInstruction(key));
		Log.e(TAG, "BJS TEXTVIEW " + t);
		
		
		
		
		if (!clockRunning)
			toggleClock();
		
		if (sample != null) {
			setHanzi("");
			if (mp != null) {
				mp.stop();
				mp.release();
			}
			mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mp.setDataSource(getApplicationContext(),
						Uri.fromFile(sample));
				mp.prepare();
				mp.start();
			} catch (Exception e) {
				Log.d(TAG, "Couldn't get mp3 file");
			}
		} 
	}
	catch (Exception e) 
	{
		initializationcheck();
	}
	
    Log.i(LOGTAG, "BJS ABOUT TO OPEN HISTDATASOURCE");
    hist_datasource = new TingshuoHistDatasource(this);
    hist_datasource.open();
    Log.i(LOGTAG, "BJS OPENED HISTDATASOURCE");
    outputHistModelList();
}

public void onSwipeTop() {
}

public void onSwipeBottom() {
	//Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
	toggleClock();
}
@Override
public void onLongPress(MotionEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public boolean dispatchTouchEvent(MotionEvent e)
{
    detector.onTouchEvent(e);

    return super.dispatchTouchEvent(e);
}

@Override
public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
		float arg3) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onShowPress(MotionEvent arg0) {
	//Toast.makeText(this, "ShowHanziTap", Toast.LENGTH_SHORT).show();
	
	Log.e(TAG, "onShowPress");
	try
	{
	passkey = key;
			
			Log.e(TAG, "The key that is inside of onShowPress " + passkey);
			if (!clockRunning)
				toggleClock();
				
			if (sample != null)
				setHanzi(hanzi.get(key)); 
				
			else
			{
				Toast.makeText(this, "Can you first slide left to play, so we know which one you want!", Toast.LENGTH_LONG).show();
			}
	}
	catch (Exception e) 		
	{
		
		Log.i(LOGTAG, "Exception inside of onShowPress" + e);
	}
}

@Override
public boolean onTouchEvent(MotionEvent event) {
	Log.i(LOGTAG, "testsing3");
    return detector.onTouchEvent(event);
}

@Override
public boolean onSingleTapUp(MotionEvent e) {
	
	
    Log.i(LOGTAG, "testsing2");
    return true;
}

public boolean onSingleTapConfirmed(MotionEvent e) { 

	//Toast.makeText(getApplicationContext(), "Single Tap Gesture", 100).show();
	Log.i(LOGTAG, "testsing1");
    return true; 
}

/**
* Called by Android.
* When program is resumed, open the database.
*/
@Override
protected void onResume()
{

super.onResume();
datasource.open();
registerReceiver(DownloadService.onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
boolean pref = sharedPreferences.getBoolean("night_mode_key", false);
	if (pref)
		{
			findViewById(R.id.LinearLayout1).setBackgroundColor(0xff000000);
			((TextView) findViewById(R.id.hanziTextView)).setTextColor(0xff444444);
			((TextView) findViewById(R.id.timerTextView)).setTextColor(0xff444444);
			((TextView) findViewById(R.id.instructionTextView)).setTextColor(0xff444444);
			Log.e(TAG, "Resume, inside true, boolean for night mode is " + pref);
		}
	else
		{
			findViewById(R.id.LinearLayout1).setBackgroundColor(0xffffffff);
			((TextView) findViewById(R.id.hanziTextView)).setTextColor(0xff000000);
			((TextView) findViewById(R.id.timerTextView)).setTextColor(0xff000000);
			((TextView) findViewById(R.id.instructionTextView)).setTextColor(0xff000000);	
			Log.e(TAG, "Resume, inside false, boolean for night mode is " + pref);
		}
	File sdCard = Environment.getExternalStorageDirectory();
	mainDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/");
	
	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    
	//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    
	
    String pref2 = sharedPreferences.getString( "language_key", "");
    
    englishDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/english/");
    
    Log.i(TAG,"Startup string value " + pref2); 	
	
    
    if (pref2.equals("EN"))
	{
		clipDir = new File(englishDir, "clips");
		cliplist = clipDir.list();
		//Toast.makeText(getApplicationContext(),"HELLO FROM PREF! ", Toast.LENGTH_SHORT).show();
		Log.i(LOGTAG,"Startup String value inside EN" + pref); 
		update_cliplistwithrepeats();
	}
	else
	{
		clipDir = new File(mainDir, "clips");
		cliplist = clipDir.list();
		Log.i(LOGTAG,"Startup String value inside else " + pref); 
		update_cliplistwithrepeats();
	}
	
	readClipInfo();
	//initializeAvailableClips();
	//initialize_subset();
Log.i(TAG, "You came in and entered RESUME");


}

public void updateSlideHistList()
{
	List<HistoryModel> histModelList = hist_datasource.findAll();
	
	histlist.clear();
	
	for (int i = histModelList.size()-1; i >=0; i--)
	{
	
	String tempconstr = histModelList.get(i).get_clip_id() + " " + histModelList.get(i).get_short_hanzi();

	histlist.add(new String (tempconstr) );
	
	Log.i(LOGTAG, "THIS what  has " + histlist); 
	
	}
	
	drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_listview_item, histlist));
}

/*1)Set the boolean sameTurn to false;
*2)Set the turnNumber to 0;
*3)Get a randomindex from the array.
*4)Return the array index
*
*/
private String getClip()
{
 // sameTurn = false;
 
  rnd = new Random();
  int index = rnd.nextInt(cliplistwithrepeats.size());
  //index_getClip = index;
  return cliplistwithrepeats.get(index);

}

//
//private String getClip()
//{
//
//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
//
//sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
//
//String entryvalue = sharedPreferences.getString( "language_key", "");
//
//Log.d(TAG, "Entryvalue inside of getClip " + entryvalue);
//
//
//if (entryvalue.equals("EN"))
//{
//Log.d(TAG, "getclip 1");
//rnd = new Random();
//Log.d(TAG, "getclip 2 " + ENGLISH_CLIP_ARRAY.size());
//int index = rnd.nextInt(ENGLISH_CLIP_ARRAY.size());
//Log.d(TAG, "REturn of ENGLISH_CLIP_ARRAY.get(index) " + index);
//Log.d(TAG, "REturn of ENGLISH_CLIP_ARRAY.get(index) " + ENGLISH_CLIP_ARRAY.get(index));
//return ENGLISH_CLIP_ARRAY.get(index);
//}
//else
//{
//rnd = new Random();
//int index = rnd.nextInt(cliplistwithrepeats.size());
//return cliplistwithrepeats.get(index);
//}
//
//
//}

/********************************
* DATABASE STUFF
*******************************/

/* setAvailableClips():
* 0)Clear availableClips and probability array.
* 1)Get a substring of each entry in the cliplist (clipString -.mp3)
* 2)Put each substring into the availableClips arrayList.
* 3)Look into the database, if the data is not there enter it, the probability = 1
* 3.5) If the data is there, find the probability,
* 4)Enter it into the probabilty array the appropriate number of times
* 5)??????Use the probabilityArray to randomly pick the next clip
*
* NOTE: This can be done on every update as well as every time
* we rotate ten (ten go out and ten come in).
*
*/

/********************************
* DATABASE STUFF
*******************************/

/* setAvailableClips():
* 0)Clear availableClips and probability array.
* 1)Get a substring of each entry in the cliplist (clipString -.mp3)
* 2)Put each substring into the availableClips arrayList.
* 3)Look into the database, if the data is not there enter it, the probability = 1
* 3.5) If the data is there, find the probability,
* 4)Enter it into the probabilty array the appropriate number of times
* 5)??????Use the probabilityArray to randomly pick the next clip
*
* NOTE: This can be done on every update as well as every time
* we rotate ten (ten go out and ten come in).
*
*/



private void initializeAvailableClips()
{
   
	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);

	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
     
    String entryvalue = sharedPreferences.getString( "language_key", "");
    
    Log.d(TAG, "Entryvalue inside of getClip " + entryvalue);
    

/*if (entryvalue.equals("EN"))
{
Log.i(LOGTAG, "BJS ENGLISH InINITIALIZE");

englishavailableClips.clear();
ENGLISH_CLIP_ARRAY.clear();


String tempKey;	
Model tempModel = new Model();


//get the data and create the probaility array with that
//clip number the appropriate amount of times.
for(int i = 0; i< cliplist.length; ++i)
{
tempKey = cliplist[i];
tempKey = tempKey.substring(0, tempKey.length() - 4);
englishavailableClips.add(i, tempKey);

 * If the dats is already in the database:
* 1)get the probabilty of the data by adding the turn fields
* 2)add it to the probability
* If data is not in the database:
* 1)create and insert using Model class;
* 2)add the clip number one time to the probabilityArray.

if (datasource.isDataInDatabase(tempKey))
{
tempModel = datasource.findModel(tempKey);
//if ((tempModel.getTurnZero() + tempModel.getTurnOne()+tempModel.getTurnTwo)>3())
///
///
for(int j = 0; j<tempModel.getProbability(); ++j)
{	
ENGLISH_CLIP_ARRAY.add(tempKey);
Log.i(LOGTAG, "IN DBASE " + tempModel.getClipTxtNumber() + " PROB "+ tempModel.getProbability());	
}

}
else//put it in the databases add it to the probabilityArray as 1.
{
createAndInsertModel(tempKey);
ENGLISH_CLIP_ARRAY.add(tempKey);

}

}
}
else
{*/
Log.i(LOGTAG, "BJS InINITIALIZE");

availableClips.clear();
probabilityArray.clear();


String tempKey;	
Model tempModel = new Model();


//get the data and create the probaility array with that
//clip number the appropriate amount of times.
for(int i = 0; i< cliplist.length; ++i)
{
tempKey = cliplist[i];
tempKey = tempKey.substring(0, tempKey.length() - 4);
availableClips.add(i, tempKey);
/*
* If the dats is already in the database:
* 1)get the probabilty of the data by adding the turn fields
* 2)add it to the probability
* If data is not in the database:
* 1)create and insert using Model class;
* 2)add the clip number one time to the probabilityArray.
*/
if (datasource.isDataInDatabase(tempKey))
{
tempModel = datasource.findModel(tempKey);
//if ((tempModel.getTurnZero() + tempModel.getTurnOne()+tempModel.getTurnTwo)>3())
///
///
for(int j = 0; j<tempModel.getProbability(); ++j)
{	
probabilityArray.add(tempKey);
Log.i(LOGTAG, "IN DBASE " + tempModel.getClipTxtNumber() + " PROB "+ tempModel.getProbability());	
}

}
else//put it in the databases add it to the probabilityArray as 1.
{
createAndInsertModel(tempKey);
probabilityArray.add(tempKey);

}

}
}
//}

/*
* Make a model to be inserted into the database;
* Initially sets all turns(repeats) to zero
* and sets the probability to one.
*/
private Model createAndInsertModel(String clip_txt_number)
{
Model model = new Model(); //declare a new object only once
// and reuse objects below
model.setClipTxtNumber(clip_txt_number);
model.setProbability(1);
model.setTurnZero(0);
model.setTurnOne(0);
model.setTurnTwo(0);
model = datasource.createModel(model);
return model;
}










/*1)Set the boolean sameTurn to false;
*2)Set the turnNumber to 0;
*3)Get a randomindex from the array.
*4)Return the array index
*
*/




/**
* Example:
* |Orig | |Shift | |Repeat | |Repeat | |Shift |
* [1][2][0] ->[0][1][2] ->[1][1][2] ->[2][1][2]->[0][2][1]
* update_data(String clip_id_number, int new_probability,
int turn_zero,int turn_one,int turn_two){
*/


////////////Above the
///////////
//////////
////////////////////

private void repeatHandler()
{
Log.i(LOGTAG, " BJS REPEAT " + lastTurn);
    int turn0,turn1, turn2, tempProb;
    
Model tempModel = getModel(key);
turn0 = tempModel.getTurnZero();
turn1 = tempModel.getTurnOne();
turn2 = tempModel.getTurnTwo();	

int tempCount = turn0 + turn1 + turn2;
int nextCount = 1 + turn0 + turn1;

Log.i(LOGTAG, " BJS REPEAT tempCount = " + tempCount + "nextCount = " + nextCount);	

    if ((tempCount==3))
    {
      Log.i(LOGTAG, "in tempcount == 3");	
      Log.i(LOGTAG, " BJS REPEAT About to increase probArray");	
        outputModel();
        turn0++;
        Log.i(LOGTAG, " BJS REPEAT About to increase probArray" + probabilityArray.size());	
        probabilityArray.add(key);	
        Log.i(LOGTAG, " BJS REPEAT After to increase probArray" + probabilityArray.size());	

datasource.update_data(key,turn0,turn1 ,turn2 );
outputModel();
}
else
{
Log.i(LOGTAG, " BJS OUTPUT MODEL BEFORE");	
outputModel();
        turn0 = turn0 + 1;
datasource.update_data(key, turn0, turn1, turn2);
Log.i(LOGTAG, " BJS OUTPUT MODEL AFTER");	
outputModel();

}

}


public void outputModel(){
Model model = getModel(key);
    Log.i(LOGTAG,"BJS "+ model.getClipTxtNumber()+ " " +
     " " + model.getTurnZero() + " " + model.getTurnOne() + " " + model.getTurnTwo());

}


////////////below the
///////////
//////////
////////////////////
private void databaseSwipeHandler()
{


Log.i(LOGTAG, "BJS datapaseSwipeHandler ");

int turn0, turn1,turn2;
Model tempModel = getModel(key);
turn0 = tempModel.getTurnZero();
turn1 = tempModel.getTurnOne();
turn2 = tempModel.getTurnTwo();
int tempCount = turn0 + turn1 + turn2;/////previousTotalRepeats.
Log.i(LOGTAG, "BJS DBSWIPEHN() PRIOR TO UPDATE "+ key+ " " + turn0 + " " + turn1+ " " + turn2 + " ");	
        
/**
* If the there turn counts were added to more than four and
* after this turn they will be less than 4 than the probability will
* reduce return to the clip to a probability of one.
*/
if(tempCount > 3 && (turn0 + turn1)< 4 )
{
Log.i(LOGTAG,"BJS Size of the probability array is" + probabilityArray.size());
probabilityArray.remove(key);
Log.i(LOGTAG,"BJS Size of the probability array is now" + probabilityArray.size());
}


Log.i(LOGTAG, "BJS UPDATE & OUTPUT MODEL ");
outputModel();	
Log.i(LOGTAG, "BJS UPDATED MODEL");
datasource.update_data(key, 0, turn0, turn1);
outputModel();	
Log.i(LOGTAG, "BJS UPDATE & OUTPUT MODEL ");

//Old stuff
//tempProb = tempModel.getProbability();
//Log.i(LOGTAG, " BJS DBSWIPEHN() POST UPDATE "+ key+ " " + turn0 + " " + turn1+ " " + turn2 + " ");
      
}

private Model getModel(String clip_txt_number)
{
Model model = datasource.findModel(clip_txt_number);
return model;
}



////Just to test consistancies
//
//private void outputAvailableClips()
//{
// for(int i = 0; i< cliplist.length; ++i)
// {
// Log.i(LOGTAG, " AVAILABLECLIP "+ availableClips.get(i));
// }
//}


/*
* LOGCAT out the modellist or the model. Just for testing
*/
public void outputModelList(List<Model> modelList)
{
 
for (int i = 0; i < modelList.size(); i++)
{
Log.i(LOGTAG, "BJS CLIPNUM " +modelList.get(i).getClipTxtNumber() + " PROB "+ modelList.get(i).getProbability());

}
}

public void outputModel(Model model){
    Log.i(LOGTAG,"COLUMN_ID = "+ model.getColumnId() + "MODEL TXT = "+ model.getClipTxtNumber() + "Probability = " + model.getProbability() +
     "TURN 0 = " + model.getTurnZero() + "TURNONE = " + model.getTurnOne() + "TURNTWO = " + model.getTurnTwo());

}


////////////////
////////////////
///Hist Table///
////////////////
////////////////
//
/**
* Returns the in order list for the history list that is to be displayed
* Calls private function histListForOutput which sets the propper ordering
* JON this is the one you call to get the list of historyModel objects
* @return
*/
public List<HistoryModel> getHistory()
{
List<HistoryModel> histModelList = hist_datasource.findAll();
     
return histListForOutput(histModelList);
}

/*
* Take a List of history models, reverse it and return the reversed list
*/
private List<HistoryModel> histListForOutput (List<HistoryModel> histModelList)
{

List<HistoryModel> temp = new ArrayList<HistoryModel>();

for (int i = histModelList.size()-1; i >=0; i--)
{
//Log.i(LOGTAG, "BJS IN THE REVERSE THING= ");
temp.add(histModelList.get(i));
}
    outputHistModelList();
    return temp;
}

/*
* outputs the histmodellist
* for purposes of output testing
*/
public void outputHistModelList()
{
List<HistoryModel> histModelList = hist_datasource.findAll();

for (int i = 0; i < histModelList.size(); i++)
{

Log.i(LOGTAG, "BJS HISTCLIP " +histModelList.get(i).getId() + " PROB "+ histModelList.get(i).get_clip_id() + " " + histModelList.get(i).get_short_hanzi());



}

}



private HistoryModel createAndInsertHistModel()
{
HistoryModel hmodel = new HistoryModel();
hmodel.set_clip_id(key);
String temp = hanzi.get(key);

hmodel.set_short_hanzi(temp);	///////CHANGE THIS TO WHATEVER VARIABLE YOU WANT
///////CONTAINING HANZI
hmodel = hist_datasource.createModel(hmodel);
Log.i(LOGTAG,"BJS CreateAndInsertHist.." + hmodel.get_clip_id() + " " + hmodel.get_short_hanzi());
outputHistModelList();

return hmodel;

}



/********************************
* End DATABASE STUFF
*******************************/


/********************************
* BJS SHARED_PREFERENCES
*******************************/
private void tick_check()
{
// Log.i("tick_check", "BJS SP " + (current_tick));
// if(current_tick + (mod_seconds) == 2400)
if(((current_tick) % 2400) == 0 )
{
update_cliplistwithrepeats();
}
current_tick ++;
}


/*
* 1) Clear the previous cliplistwith repeats
* 2) Get the starting index from shared preferences
* 3) If the starting index is less than 50 away from the
* end of the array
* Create a cliplistwithrepeats that will be of the sizestart index-cliplist.length
* 4) Else create a cliplistwithrepeats that is length 50 + the added probability numbers.
* 5)Update the shared preferences starting index
*/
private void update_cliplistwithrepeats()
{
Log.i(LOGTAG,"BJS clock update_cliplistwithrepeats() ");
    //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
    String entryvalue = sharedPreferences.getString( "language_key", "");

    ///if the entryvalue is english goto english setup
if (entryvalue.equals("EN"))
{
update_english_cliplist_with_repeats();
return;
}

int current_clip_index = get_current_clip_index_from_shared_prefs();
Log.i(LOGTAG,"BJS clock starting index " + current_clip_index);


if(current_clip_index + 50 > cliplist.length)
{
Log.i(LOGTAG,"ABOUT TO INITIALIZE SUBSET," + current_clip_index + " to " + cliplist.length);
initialize_subset(current_clip_index, cliplist.length);
store_current_clip_index_to_shared_prefs(current_clip_index);////This may be a do nothing issue

}
else
{
Log.i(LOGTAG,"ABOUT TO INITIALIZE SUBSET," + current_clip_index + " to " + current_clip_index + 50);
initialize_subset(current_clip_index, current_clip_index + 50);
store_current_clip_index_to_shared_prefs(current_clip_index + 10);
}
}

public void update_english_cliplist_with_repeats()
{
//initialize_subset(get_current_eng_clip_index_from_shared_prefs(), cliplist.length);
initialize_subset(0, cliplist.length);

}



private void initialize_subset(int startingClip, int limit)
{
    Log.i(LOGTAG, "BJS startingClip "+startingClip + "limit " + limit);
    cliplistwithrepeats.clear();


String tempKey;	
Model tempModel = new Model();


//get the data and create the probaility array with that
//clip number the appropriate amount of times.

for(int i = startingClip; i< limit; ++i)
{
// Log.i(LOGTAG, "BJS in for " + " " + i);

tempKey = cliplist[i];
//Log.i(LOGTAG, "BJS in tempKey " + tempKey );

tempKey = tempKey.substring(0, tempKey.length() - 4);
/*
* If the data is already in the database:
* 1)get the probabilty of the data by adding the turn fields
* 2)add it to the probability
* If data is not in the database:
* 1)create and insert using Model class;
* 2)add the clip number one time to the probabilityArray.
*/
if (datasource.isDataInDatabase(tempKey))
{
tempModel = datasource.findModel(tempKey);
int turn0, turn1, turn2;
turn0 = tempModel.getTurnZero();
turn1 = tempModel.getTurnOne();
turn2 = tempModel.getTurnTwo();

if ((turn0 + turn1 + turn2)>3)
{
//Log.i(LOGTAG, "BJS adding REPEAT tempkey" + tempKey);

cliplistwithrepeats.add(tempKey);

}
// Log.i(LOGTAG, "BJS adding tempkey " + tempKey);

cliplistwithrepeats.add(tempKey);
}

else//put it in the databases add it to the probabilityArray as 1.
{
//Log.i(LOGTAG, "BJS adding tempkey" + tempKey);

createAndInsertModel(tempKey);
cliplistwithrepeats.add(tempKey);

}


}

}






//
private void store_current_clip_index_to_shared_prefs(int current_clip_index)
{
	
	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putInt(SHAREDPREF_CLIP_INDEX, current_clip_index);	
	editor.commit();
	Log.i("store_current_clip_to...", "BJS SP stored clip = " +
	get_current_clip_index_from_shared_prefs());

}

public int get_current_clip_index_from_shared_prefs()
{
	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, Context.MODE_PRIVATE);
	int extractedClipIndex = prefs.getInt(SHAREDPREF_CLIP_INDEX, 0);
	Log.i("get_current_clip_index...","BJS SP extractedClipIndex = " + extractedClipIndex);
	return extractedClipIndex;
}

private void store_current_tick_to_shared_prefs()
{
	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putInt(SHAREDPREF_TOTAL_SECONDS, current_tick);
    //commit the change
    editor.commit();
    Log.i("store_updated sec in sp ","BJS SP store_updated_sec_SP = " + get_current_tick_from_shared_prefs());
    
}
private int get_current_tick_from_shared_prefs()
{

	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, Context.MODE_PRIVATE);
	int extractedTick = prefs.getInt(SHAREDPREF_TOTAL_SECONDS, 0);	
	Log.d("get_current_tick","BJS SP extracted ticks = " + extractedTick);	
	return extractedTick;
}

private void store_current_eng_clip_index_to_shared_prefs(int current_clip_index)
{
	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, MODE_PRIVATE);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putInt(SHAREDPREF_ENG_CLIP_INDEX, current_clip_index);	
	editor.commit();
	Log.i("store_current_clip_to...", "BJS SP stored clip = " +
	get_current_clip_index_from_shared_prefs());

}

public int get_current_eng_clip_index_from_shared_prefs()
{
	
	SharedPreferences prefs = getSharedPreferences(SHAREDPREF_SET, Context.MODE_PRIVATE);
	int extractedClipIndex = prefs.getInt(SHAREDPREF_ENG_CLIP_INDEX, 0);
	Log.i("get_current_clip_index...","BJS SP extractedClipIndex = " + extractedClipIndex);
	return extractedClipIndex;
}



/********************************
* End DATABASE STUFF
*******************************/




}
