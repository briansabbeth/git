package com.MeadowEast.dbOpenHelper;

import com.MeadowEast.model.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbaseOpenHelper extends SQLiteOpenHelper 
{
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String LOGTAG = "TINGSHUOHELP";

	private static final String DATABASE_NAME = "ModelDB";
	
	
	public static final String TABLE_TING_DATA = "ting_data";
	public static final String COLUMN_ID = "table_index";
	public static final String COLUMN_CLIP_TXT_NUM = "clipTxtNumber";	
	public static final String COLUMN_PROBABILITY = "probability";
	public static final String COLUMN_TURN_ZERO = "turnzero";
	public static final String COLUMN_TURN_ONE= "turnone";
	public static final String COLUMN_TURN_TWO = "turntwo";
	
	
	
	
	public DbaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}
	
	public void onCreate(SQLiteDatabase db) {
		//creates the database table
		//TABLE_CREATE is the constant command which creates the table
		//exec makes it happen = execute
		
		String CREATE_MODEL_TABLE = 
				   "CREATE TABLE ting_data ( " +
							COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
							COLUMN_CLIP_TXT_NUM + " TEXT, " +
							COLUMN_PROBABILITY + " INTEGER, " +
							COLUMN_TURN_ZERO +  " INTEGER, " +
							COLUMN_TURN_ONE + " INTEGER, " +
							COLUMN_TURN_TWO + " INTEGER )";
		
		db.execSQL(CREATE_MODEL_TABLE);//sql 
		Log.i(LOGTAG, "TABLE HAS BEEN CREATED");
		
	}
	
	public void addModel(Model model){
		Log.d("addModel", model.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(COLUMN_CLIP_TXT_NUM,model.getClipTxtNumber());
		values.put(COLUMN_ID,model.getColumnId());
		values.put(COLUMN_PROBABILITY,model.getProbability());
		values.put(COLUMN_TURN_ZERO,model.getTurnZero());
		values.put(COLUMN_TURN_ONE,model.getTurnOne());
		values.put(COLUMN_TURN_TWO,model.getTurnTwo());
 
        // 3. insert
        db.insert(TABLE_TING_DATA, // table
        		null, //nullColumnHack
        		values); // key/value -> keys = column names/ values = column values
        
        // 4. close
        db.close(); 
	}
	


	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_TING_DATA);
		onCreate(db);

	}


	

}

