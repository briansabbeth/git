package com.MeadowEast.dbOpenHelper;
import java.util.ArrayList;
import java.util.List;

//import com.MeadowEast.model.Model;
import com.MeadowEast.model.HistoryModel;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Datasources represent individual tables.
 * Datasources methods will be called by the rest of the app
 *     Single table = single datasource
 * DbaseOpenHelper methods previously were called from the MainActivity
 * 		MainActivity no longer deals directly with the database or DbaseOpenHelper
 * This new data-source in package TingshuoDatabaseOpenHelper
 * 		interactions are now hidden by data-source object
 *????I THINK: that 
 */
public class TingshuoHistDatasource {
	
    /**
     * String for each column.  
     * 	to be used only in the debugging output
     * TODO: must use this or similar to get and reset data in the dbase.
     */
    private static final String [] allColumns = 
    {
    	DbaseOpenHelper.COLUMN_HCLIP_TXT_NUM,
    	DbaseOpenHelper.COLUMN_ID_H,
    	DbaseOpenHelper.COLUMN_HANZI,
    };
    
    
 	private static final String LOGTAG = "TINGSHOU_DS ";
	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	
	/*
	 *    Receives context of the local activity//this activity//
	 * 	  CONTEXT allows access to application-specific resources and classes
	 */
	public TingshuoHistDatasource(Context context)
	{   
		//the activity context taken as arguement to the dbhelper 
        dbhelper = new DbaseOpenHelper(context);
	}
	/*
	 * open()/opens this specific table
	 *    when open log it.  
	 */

	public void  open()
	{
		Log.i(LOGTAG, "Database opened");
		//.getWritableDatabase() opens the database connection
		database = dbhelper.getWritableDatabase();	
	}
	/*
	 *  close()/closes this specific table
	 *    when closed log it.  
	 */	
	public void  close()
	{
		Log.i(LOGTAG, "Database closed");
		dbhelper.close();
	}
	
	/*
	 *  Create: publicly adds data to the dbase. 
	 *  ContentValues implements the map interface.
	 *  Put values into the map where the KEY is the column name
	 *  and the VALUE.
	 *  basic steps are:
	 *    1)take a model object
	 *    2)insert it to the database
	 *    3)return the tour object
	 *  
	 */
	public HistoryModel createModel(HistoryModel hmodel){
		
		if (getSize() > 9)
		{
			Log.i(LOGTAG, "GREATER THAN 9 ");
			deleteFirstRow();
		}
		
		Log.i(LOGTAG, "Creating model");		
		ContentValues values = new ContentValues();
		values.put(DbaseOpenHelper.COLUMN_HCLIP_TXT_NUM,hmodel.get_clip_id());
 		values.put(DbaseOpenHelper.COLUMN_HANZI,hmodel.get_short_hanzi());		
	   ///here is where the insert happens.
		
		Log.i(LOGTAG, "Creating model");		

 		long insertId = database.insert(DbaseOpenHelper.TABLE_HIST_DATA, null, values);
		hmodel.setId(insertId);
		
		
	   return hmodel;	
	}
	
	
	/*
	 * To query the entire table and return everything from it
	 *   Uses the query and cursor class (query returns cursor)
	 *   Creates a list of models and a model object.
	 *   Cursor will begin before the first row of the database
	 *   while movetonext() is true. 
	 *   Each column is added to the model object
	 *   Each model object is added to the modellist.
	 *    
	 *   @return the modellist
	 */
	public List<HistoryModel> findAll()
	{
		List<HistoryModel> histmodelList = new ArrayList<HistoryModel>();
		Cursor cursor = database.query(DbaseOpenHelper.TABLE_HIST_DATA, allColumns, 
				null, null, null, null, null);	
		//getCount() obviously returns the number of rows in the database
		Log.i(LOGTAG, cursor.getCount() + " = count");
		if (cursor.getCount() > 0)
		{
			while (cursor.moveToNext())
			{
				HistoryModel hist_model = new HistoryModel
						(cursor.getLong(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_ID_H)),
					     cursor.getString(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_HCLIP_TXT_NUM)),
					     cursor.getString(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_HANZI)));
				
			//	hist_model.setId(cursor.getLong(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_ID)));
			//	hist_model..setClipTxtNumber(cursor.getString(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_CLIP_TXT_NUM)));
			//	hist_model.setProbability(cursor.getInt(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_PROBABILITY)));

			    histmodelList.add(hist_model);
				
			}
		}
		return histmodelList;
	}

	
	
	
	public HistoryModel findHistModel(String clip_id_number)
	{ 
		//get readable database from the dbhelper class.
	    //	SQLiteDatabase db = dbhelper.getReadableDatabase();		
		
		Cursor cursor = 
				database.query(DbaseOpenHelper.TABLE_HIST_DATA, 
						       allColumns, 
				              "h_clipTxtNumber = ?", 
				               new String[] { clip_id_number}, 
				               null, 
				               null, 
				               null, 
				               null);
	
		if (cursor != null)
    		cursor.moveToFirst();
		
 
		//pack the model with the db info.  
		HistoryModel hmodel = new HistoryModel();
		hmodel.setId(cursor.getLong(1));
		hmodel.set_clip_id(cursor.getString(0));
		
		hmodel.set_short_hanzi(cursor.getString(2));
	
    	return hmodel;
    	
    	

	}
  
	
	
	public void update_data(String clip_id_number, int new_probability, 
							int turn_zero,int turn_one,int turn_two){
		
		String[] whereArgs = {clip_id_number}; 
		
		
		Log.i(LOGTAG,"update_data:  clip_id_number = " + clip_id_number + " probability = " + new_probability);
		ContentValues data = new ContentValues();
		data.put(DbaseOpenHelper.COLUMN_PROBABILITY, new_probability);
	    data.put(DbaseOpenHelper.COLUMN_TURN_ZERO,turn_zero);
	    data.put(DbaseOpenHelper.COLUMN_TURN_ONE,turn_one);
	    data.put(DbaseOpenHelper.COLUMN_TURN_TWO,turn_two);
	
		database.update(DbaseOpenHelper.TABLE_HIST_DATA, data, DbaseOpenHelper.COLUMN_HCLIP_TXT_NUM +"=?", whereArgs);
	
	
	
	}
	/*
	 * FIND IF DATA IS PRESENT RETURN TRUE ELSE RETURN FALSE;
	 */
	public boolean isDataInDatabase(String clip_id_number)
	{ 
		//get readable database from the dbhelper class.
	    //	SQLiteDatabase db = dbhelper.getReadableDatabase();		
		
		Cursor cursor = 
				database.query(DbaseOpenHelper.TABLE_HIST_DATA, 
						       allColumns, 
				              "h_clipTxtNumber = ?", 
				               new String[] { clip_id_number}, 
				               null, 
				               null, 
				               null, 
				               null);
	
		if (cursor != null)
    		cursor.moveToFirst();
	    if(cursor.getCount()<=0)
	    {
	            return false;
	    }
	             return true;  	

	}

	
	
	
	
	public long getSize(){

		
		
		
		long numRows = DatabaseUtils.queryNumEntries(database, "h_data");
	
		return numRows;
	}
	
	/*
	 * Deleting a todo
	 */

	public void deleteI(String i) {
		database.delete(DbaseOpenHelper.TABLE_HIST_DATA, DbaseOpenHelper.COLUMN_ID_H + " = ?",
	            new String[] { String.valueOf(i) });		
	}

	 public void deleteFirstRow()
	 {
	     Cursor cursor = database.query(DbaseOpenHelper.TABLE_HIST_DATA, null, null, null, null, null, null); 

	         if(cursor.moveToFirst()) {
	             String rowId = cursor.getString(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_ID_H)); 

	             database.delete(DbaseOpenHelper.TABLE_HIST_DATA, DbaseOpenHelper.COLUMN_ID_H + "=?",  new String[]{rowId});
	            }
	     }   

}