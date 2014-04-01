package com.MeadowEast.audiotest;
import android.view.GestureDetector;
import com.MeadowEast.dbOpenHelper.TingshuoDatasource;
import com.MeadowEast.model.Model;
import android.view.GestureDetector.OnGestureListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnGesturePerformedListener,  OnGestureListener  {

	private ProgressDialog mProgressDialog;
	private GestureLibrary gLibrary;
	private MediaPlayer mp;
	private String[] cliplist;
	private File sample;
	private File mainDir;
	private File clipDir;
	private File zipDir;
	private Random rnd;
	private Handler clockHandler;
	private Runnable updateTimeTask;
	private boolean clockRunning;
	private boolean clockWasRunning;
	private Long elapsedMillis;
	private Long start;
	public static Map<String, String> hanzi;
	private Map<String, String> instructions;
	private String key;
	static final String TAG = "CAT";
	public static String passkey;
	ShareActionProvider mShareActionProvider ;
	GestureDetector detector;
	
	private final Handler mHandler = new Handler();
	
	public static final int progress_bar_type = 0;
	
	
	/* DATABASE VARIABLES*/
	private static final String LOGTAG = "TINGSHOU_DB ";
	TingshuoDatasource datasource;
	
	/* END DATABASE VARIABLES*/	
	

	DownloadManager mgr = null;
	long lastDownload = -1L;

	// String Url="http://www.meadoweast.com/capstone/clips.zip";
	String Urltxt = "http://www.meadoweast.com/capstone/clipinfo.txt";
	String unzipLocation = Environment.getExternalStorageDirectory() + "/clips/";
	String StorezipFileLocation = Environment.getExternalStorageDirectory()	+ "/Clips.zip";
	String StoretxtFileLocation = Environment.getExternalStorageDirectory()	+ "/Android/data/com.MeadowEast.audiotest/files/clipinfo.txt";
	String DirectoryName = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/clips/";
	String DirectoryName1 = Environment.getExternalStorageDirectory() + "/Android/data/com.MeadowEast.audiotest/files/";
	
	
	
	


	
	
	private final Runnable mUpdateUI = new Runnable() 
	{
	    public void run()
	    {
	    	if (isNetworkAvailable()== true)
	    	{
		    	try 
			    	{
						new checkupdate().execute();
						 mHandler.postDelayed(mUpdateUI, 86400000);
						 Toast.makeText(MainActivity.this, "Daily update check!", Toast.LENGTH_LONG).show();
					} 
		    	catch (IOException e) 
			    	{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	}
	    	else
		    	{
		    		mHandler.postDelayed(mUpdateUI, 21600000);
		    		Toast.makeText(MainActivity.this, "No internet for Daily update check, try again in little!", Toast.LENGTH_LONG).show();
		    	}
	        
	    }
	    
	};
	
	
	private void readClipInfo() {
		hanzi = new HashMap<String, String>();
		instructions = new HashMap<String, String>();
		File file = new File(mainDir, "clipinfo.txt");
		Log.d(TAG, "before");
		Log.d(TAG, "after");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader in = new BufferedReader(fr);
			String line;
			while ((line = in.readLine()) != null) {
				String fixedline = new String(line.getBytes(), "utf-8");
				String[] fields = fixedline.split("\\t");
				if (fields.length == 3) {
					hanzi.put(fields[0], fields[1]);
					instructions.put(fields[0], fields[2]);
				} else {
					Log.d(TAG, "Bad line: " + fields.length + " elements");
					Log.d(TAG, fixedline);
				}
			}
			in.close();
		} catch (Exception e) {
			Log.d(TAG, "Problem reading clipinfo");
		}
	}

	private String getInstruction(String key) {
		String instructionCodes = instructions.get(key);
		int n = instructionCodes.length();
		if (n == 0) {
			return "No instruction codes for " + key;
		}
		int index = rnd.nextInt(n);
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
			setHanzi("");
		} else
			start = System.currentTimeMillis();
		clockRunning = !clockRunning;
		clockHandler.removeCallbacks(updateTimeTask);
		if (clockRunning)
			clockHandler.postDelayed(updateTimeTask, 200);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "testing only");
		// File filesDir = getFilesDir(); // Use on virtual device
		
		
		 detector=new GestureDetector(getBaseContext(), this);
		 
		gLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gLibrary.load()) {
            finish();
       }  
        
        GestureOverlayView gOverlay = 
                (GestureOverlayView) findViewById(R.id.gestureOverlayView1);
            gOverlay.addOnGesturePerformedListener(this); 
		
            /**DATABASE CODE*/
    		Log.i(LOGTAG, "ABOUT TO OPEN DATASOURCE");    
            
		  datasource = new TingshuoDatasource(this);
	      datasource.open();
	  	  List<Model> modelList = datasource.findAll();
          
	  	  ////TODO:  get rid of this  once the propper functions are in place
	  	  //if the modellist is ==  0 the no data exists
	  	  //create the data
	  	  // reset the modellist
	  	  if (modelList.size() == 0)
	  	  {
		      createData();
			  Log.i(LOGTAG, "CREATE DATA CALLED");
			  modelList = datasource.findAll();

	  	  }
	  	  
	  	  //Just for grins. Loop through the modelList in a local function:
	    	outputModelList(modelList);
	  	  
	  	  
	    //createData();
		//Log.i(LOGTAG, "CREATE DATA CALLED");

	  	/**END DATABASE CODE*/
		
	
		
	    	
		
		
		mHandler.post(mUpdateUI);
		
		mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		
		registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	

		File sdCard = Environment.getExternalStorageDirectory();
		File f = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/");
		f.mkdirs();
		
		mainDir = new File(sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.audiotest/files/");

		// old
		// File filesDir = new File (sdCard.getAbsolutePath() +
		// "/Android/data/com.MeadowEast.audiotest/files");
		// mainDir = new File(filesDir, "ChineseAudioTrainer");

		clipDir = new File(mainDir, "clips");
		cliplist = clipDir.list();
		readClipInfo();
		rnd = new Random();
		
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
		/*findViewById(R.id.pauseButton).setOnClickListener(
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

	}

	public void onPause() {
		
		Log.d(TAG, "!!!! onPause is being run");
		clockWasRunning = clockRunning;
		if (clockRunning)
			toggleClock();
		datasource.close();
		unregisterReceiver(onComplete);
		super.onPause();
	}

	public void onSaveInstanceState(Bundle outState) {
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

	public void reset() {
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

	public boolean onLongClick(View v)
	{
		switch (v.getId()) {
		case R.id.hanziTextView:
			Toast.makeText(this, "Clip: " + key, Toast.LENGTH_LONG).show();
			Log.d(TAG, "Long clicked");
			break;
		}
		return true;
	}

	public void onClick(View v) 
	{
		switch (v.getId()) {
		/*case R.id.playButton:
			try {
				cliplist = clipDir.list();
				readClipInfo();
				Integer index = rnd.nextInt(cliplist.length);
				sample = new File(clipDir, cliplist[index]);
				key = sample.getName();
				key = key.substring(0, key.length() - 4);
				TextView t = (TextView) findViewById(R.id.instructionTextView);
				t.setText(getInstruction(key));
				} 
			catch (Exception e) 
				{
					Toast.makeText(getApplicationContext(),
							"Sorry, no files. Try updating! ", Toast.LENGTH_SHORT)
							.show();
					Log.e(TAG, "Null, No files or directory", e);
				}

		case R.id.repeatButton:
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
			break;
		case R.id.hanziButton:
			passkey = key;
			if (!clockRunning)
				toggleClock();
			if (sample != null)
				setHanzi(hanzi.get(key)); // Should add default value: error
											// message if no hanzi for key
			break;*/
		case R.id.timerTextView:
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.reset)
					.setMessage(R.string.reallyReset)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
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
public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent i = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivity(i);
			// setContentView(R.layout.settings_activity);
			return true;
		case R.id.action_check_updates:

			if (isNetworkAvailable()== true)
	    	{
			try {
				new checkupdate().execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	}
			else
			{
				Toast.makeText(MainActivity.this, "No internet for update check, try again in little!", Toast.LENGTH_SHORT).show();
			}

			return true;
		case R.id.action_trans:
			Intent x = new
			Intent(getApplicationContext(),DisplayDict.class);
			startActivity(x);
			return true;
		case R.id.share:
			getDefaultShareIntent();
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gLibrary.recognize(gesture);
        
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            
        	String action = predictions.get(0).name;
        	String hswipe = "hswipe";
        	String U = "u";
        	String circle = "circle";
        	String trig = "trig";
        	//String p = "p";
        	
            //Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
            
            if (action.equals( hswipe ))
            {
            	Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
            	Log.e(TAG, "hello in hswipe");
            	try {
    				cliplist = clipDir.list();
    				readClipInfo();
    				Integer index = rnd.nextInt(cliplist.length);
    				sample = new File(clipDir, cliplist[index]);
    				key = sample.getName();
    				key = key.substring(0, key.length() - 4);
    				TextView t = (TextView) findViewById(R.id.instructionTextView);
    				t.setText(getInstruction(key));
    				
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
    					Toast.makeText(getApplicationContext(),
    							"Sorry, no files. Try updating! ", Toast.LENGTH_SHORT)
    							.show();
    					Log.e(TAG, "Null, No files or directory", e);
    				}
            	
            }
            
            if (action.equals( U ))
            {
            	Toast.makeText(this, "ShowHanzi", Toast.LENGTH_SHORT).show();
            	Log.e(TAG, "hello in U");
            	passkey = key;
    			if (!clockRunning)
    				toggleClock();
    			if (sample != null)
    				setHanzi(hanzi.get(key)); // Should add default value: error
    											// message if no hanzi for key
            }
            
            if (action.equals( circle ))
            {
            	Toast.makeText(this, "Repeat", Toast.LENGTH_SHORT).show();	
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
            if (action.equals( trig ))
            {
            	Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
            	toggleClock();
            }
        }
    }

public void startDownload(String url, String name) 
	{

		Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).mkdirs();
		File sdCard = Environment.getExternalStorageDirectory();
		File f = new File(sdCard.getAbsolutePath()
				+ "/Android/data/com.MeadowEast.audiotest/files/clips");
		f.mkdirs();

		lastDownload = mgr.enqueue(new DownloadManager.Request(Uri.parse(url))
				.setAllowedNetworkTypes(
						DownloadManager.Request.NETWORK_WIFI
								| DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false)
				.setTitle(name)
				.setDescription("Update files for Hanzi")
				.setDestinationInExternalPublicDir(
						"/Android/data/com.MeadowEast.audiotest/files/", name));

	}

	BroadcastReceiver onComplete = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {

			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

				Bundle extras = intent.getExtras();
				DownloadManager.Query q = new DownloadManager.Query();
				q.setFilterById(extras
						.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
				Cursor c = mgr.query(q);

				if (c.moveToFirst()) {
					int status = c.getInt(c
							.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (status == DownloadManager.STATUS_SUCCESSFUL) {
						// process download
						String title = c.getString(c
								.getColumnIndex(DownloadManager.COLUMN_TITLE));
						// get other required data by changing the constant
						// passed to getColumnIndex
						Toast.makeText(ctxt, "Downloading " + title,
								Toast.LENGTH_LONG).show();
						String zip = "clips.zip";

						if (title.equals(zip)) {
							Toast.makeText(ctxt,
									"Finished Downloading " + title,
									Toast.LENGTH_LONG).show();
							//String path=android.os.Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.MeadowEast.audiotest/files/";
							//new UnZipTask().execute(path + "clips.zip", path + "clips/");
							
							try 
							{
								unzip();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							}
						else
						{
							cliplist = clipDir.list();
							readClipInfo();
						}
	
						
					}
				}
			}
		}
	};




private class UnZipTask extends AsyncTask<String, Void, Boolean> {

		@SuppressWarnings("rawtypes")
		@Override
		protected Boolean doInBackground(String... params) {
			String filePath = params[0];
			String destinationPath = params[1];

			File archive = new File(filePath);
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, destinationPath);
				}

				UnzipUtil d = new UnzipUtil(filePath, destinationPath);
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

			mProgressDialog.dismiss();
			cliplist = clipDir.list();
			readClipInfo();

			}

		
		
private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {

		if (entry.isDirectory()) 
		{
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}

		Log.v("", "Extracting: " + entry);
		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try 
		{

		} finally 
		{
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		}
	}

	private void createDir(File dir) 
	{
		if (dir.exists()) {
			return;
		}
		if (!dir.mkdirs()) {
			throw new RuntimeException("Can not create dir " + dir);
		}
	}
	
	}

private class checkupdate extends AsyncTask<String, String, String> 
{
		public checkupdate() throws IOException 
		{

		}
		int check_point = 0;
		@Override
		protected String doInBackground(String... params)
		{
			check_point = 0;
			
		try
			{
			

			URL urltxt = new URL("http://www.meadoweast.com/capstone/clipinfo.txt");

			URLConnection urlConnection = urltxt.openConnection();
			urlConnection.connect();
			
			int file_size = urlConnection.getContentLength();
			
			String file_size_string = String.valueOf(file_size);
			
			
			Log.d(TAG, file_size_string);
			
			File file=new File(Environment.getExternalStorageDirectory(), "/Android/data/com.MeadowEast.audiotest/files/clipinfo.txt");
			long length = file.length();
			String s = String.valueOf(length);
			Log.d(TAG, s);
		
			if (length == file_size)
				{
					Log.d(TAG, "they are equal");
					 check_point = 1;

					//Toast.makeText(getBaseContext(), "No new updates!", Toast.LENGTH_LONG).show();
					
				}
			else
				{
					startDownload("http://www.meadoweast.com/capstone/clipinfo.txt", "clipinfo.txt");
					
				}

		}
		catch(Exception e) 
		{
			
		}
	
			try
				{
				
			URL urlzip = new URL("http://www.meadoweast.com/capstone/clips.zip");
	
			URLConnection urlConnection = urlzip.openConnection();
			urlConnection.connect();
			
			int file_size = urlConnection.getContentLength();
			
			String file_size_string = String.valueOf(file_size);
		
			Log.d(TAG, file_size_string);
			
			File file=new File(Environment.getExternalStorageDirectory(), "/Android/data/com.MeadowEast.audiotest/files/clips.zip");
			long length = file.length();
			String s = String.valueOf(length);
			Log.d(TAG, s);
			
				if (length == file_size)
					{
						Log.d(TAG, "they are equal");
						 check_point = 1;

						//Toast.makeText(getBaseContext(), "No new updates!", Toast.LENGTH_LONG).show();					
					}
				else
					{
						
						startDownload("http://www.meadoweast.com/capstone/clips.zip", "clips.zip");
					}
		
			}
			catch(Exception e) 
			{
				
			}
			
			return DirectoryName;
			}
		

		
protected void onPostExecute(String result)
		{		    
		    	 if(check_point == 1) 
				  {
				    Toast.makeText(MainActivity.this, "No new updates!", Toast.LENGTH_SHORT).show(); 
				  }

		}	
}

private Intent getDefaultShareIntent()
{
	
    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    intent.setType("audio/mp3");
  
    intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
    intent.putExtra(Intent.EXTRA_TEXT,hanzi.get(key));
    
    //startActivityForResult(Intent.createChooser(intent, "Send mail"), EMAIL_REQUEST);
   // File root = Environment.getExternalStorageDirectory();
    String name = clipDir.getName();
    Log.d(TAG, name);
        
    //File file = new File(clipDir, key);
    if (!sample.exists() || !sample.canRead())
    {
    	Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
    	finish();
       //return;
    }
   Uri uri = Uri.parse("file://" + sample);
   intent.putExtra(Intent.EXTRA_STREAM, uri);
   startActivity(intent);
    return intent;
	
}

public void unzip() throws IOException 
	{
	String path = android.os.Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.MeadowEast.audiotest/files/";
	zipDir = new File(mainDir, "clips.zip");
	
	if (isZipValid(zipDir) == true)
	{
		mProgressDialog = new ProgressDialog(MainActivity.this);

		mProgressDialog.setMessage("Please Wait while updating...");

		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		
		new UnZipTask().execute(path + "clips.zip", path + "clips/");
	}
	else
	{
		Toast.makeText(this, "Zip related error", Toast.LENGTH_SHORT).show();
	}

	}


private boolean isNetworkAvailable() 
	{
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}



static boolean isZipValid(final File file) {
    ZipFile zipfile = null;
    try {
        zipfile = new ZipFile(file);
        return true;
    } catch (ZipException e) {
        return false;
    } catch (IOException e) {
        return false;
    } finally {
        try {
            if (zipfile != null) {
                zipfile.close();
                zipfile = null;
            }
        } catch (IOException e) {
        }
    }
}

/*@Override
protected void onDestroy()
{
	unregisterReceiver(onComplete);
    super.onDestroy();
}*/

public boolean onDown(MotionEvent arg0) {
	Log.i(LOGTAG, "testsing5");
	//Toast.makeText(getApplicationContext(), "Single Tap Gesture", 100).show();
	return true;
}

public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
		float arg3) {
	// TODO Auto-generated method stub
	return false;
}

public void onLongPress(MotionEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public boolean dispatchTouchEvent(MotionEvent e)
{
    detector.onTouchEvent(e);

    return super.dispatchTouchEvent(e);
}

public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
		float arg3) {
	// TODO Auto-generated method stub
	return false;
}

public void onShowPress(MotionEvent arg0) {
	// TODO Auto-generated method stub
	Log.i(LOGTAG, "testsing4");
	//Toast.makeText(getApplicationContext(), "Show Press gesture", 100).show();
}

@Override
public boolean onTouchEvent(MotionEvent event) {
	Log.i(LOGTAG, "testsing3");
    return detector.onTouchEvent(event);
}

public boolean onSingleTapUp(MotionEvent e) {
	
	Toast.makeText(this, "ShowHanziTap", Toast.LENGTH_SHORT).show();
	Log.e(TAG, "hello in U");
	passkey = key;
	if (!clockRunning)
		toggleClock();
	if (sample != null)
		setHanzi(hanzi.get(key)); // Should add default value: error
									// message if no hanzi for key
   // Toast.makeText(getApplicationContext(), "Single Tap Gesture", 100).show();
    Log.i(LOGTAG, "testsing2");
    return true;
}
public boolean onSingleTapConfirmed(MotionEvent e) { 

	//Toast.makeText(getApplicationContext(), "Single Tap Gesture", 100).show();
	Log.i(LOGTAG, "testsing1");
    return true; 
}
/********************************
 * DATABASE STUFF
 ********************************/
private void createData()
{
	Model model = new Model();  //declare a new object only once
								// and reuse objects below
	model.setClipTxtNumber("0990");
	model.setProbability(1);
	model.setTurnZero(1);
	model.setTurnOne(0);
	model.setTurnTwo(0);
	Log.i(LOGTAG, "Modelinfo  " + model.getClipTxtNumber());

	model = datasource.createModel(model);
	Log.i(LOGTAG, "Model has been created with id " + model.getId());



	model = new Model();
	model.setClipTxtNumber("0001");    	model.setProbability(2);
	model.setTurnZero(0);
  	model.setTurnOne(1);
  	model.setTurnTwo(0);
////  
////	
	model = datasource.createModel(model);
 	Log.i(LOGTAG, "Model has been created with id " + model.getId());
//
//    model = new Model();
//	model.setClipTxtNumber("0021");
//	model.setProbability(2);
//	model.setTurnZero(0);
//	model.setTurnOne(1);
//	model.setTurnTwo(0);
//  
//
//	
//	model = datasource.createModel(model);
//	Log.i(LOGTAG, "Model has been created with id " + model.getId());
//
//
//

}




/**
 * Called by Android.
 * When database is resumed, open the database.
 */ 
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	datasource.open();
	registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
}

public void outputModelList(List<Model> modelList)
{
 
	for (int i = 0; i < modelList.size(); i++) 
	{
	    Log.i(LOGTAG, modelList.get(i).getClipTxtNumber());
	    
	}  
}


/********************************
 * End DATABASE STUFF
 *******************************/
}
