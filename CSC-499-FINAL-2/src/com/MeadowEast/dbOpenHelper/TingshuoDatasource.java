package com.MeadowEast.dbOpenHelper;

import java.util.ArrayList;
import java.util.List;
import com.MeadowEast.model.Model;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
* Datasources represent individual tables.
* Datasources methods will be called by the rest of the app
* Single table = single datasource
* DbaseOpenHelper methods previously were called from the MainActivity
* MainActivity no longer deals directly with the database or DbaseOpenHelper
* This new data-source in package TingshuoDatabaseOpenHelper
* interactions are now hidden by data-source object
*????I THINK: that
*/



public class TingshuoDatasource {

    /**
* String for each column.
* to be used only in the debugging output
* TODO: must use this or similar to get and reset data in the dbase.
*/
    private static final String [] allColumns =
    {
     DbaseOpenHelper.COLUMN_CLIP_TXT_NUM,
     DbaseOpenHelper.COLUMN_ID,
     DbaseOpenHelper.COLUMN_PROBABILITY,
     DbaseOpenHelper.COLUMN_TURN_ZERO,
     DbaseOpenHelper.COLUMN_TURN_ONE,
     DbaseOpenHelper.COLUMN_TURN_TWO
    };
  private static final String LOGTAG = "TINGSHOU_DS ";
SQLiteOpenHelper dbhelper;
SQLiteDatabase database;

/*
* Receives context of the local activity//this activity//
* CONTEXT allows access to application-specific resources and classes
*/
public TingshuoDatasource(Context context)
{
//the activity context taken as arguement to the dbhelper
        dbhelper = new DbaseOpenHelper(context);
}
/*
* open()/opens this specific table
* when open log it.
*/

public void open()
{
Log.i(LOGTAG, "Database opened");
//.getWritableDatabase() opens the database connection
database = dbhelper.getWritableDatabase();	
}
/*
* close()/closes this specific table
* when closed log it.
*/	
public void close()
{
Log.i(LOGTAG, "Database closed");
dbhelper.close();
}

/*
* Create: publicly adds data to the dbase.
* ContentValues implements the map interface.
* Put values into the map where the KEY is the column name
* and the VALUE.
* basic steps are:
* 1)take a model object
* 2)insert it to the database
* 3)return the tour object
*
*/
public Model createModel(Model model){

Log.i(LOGTAG, "Creating model");	
ContentValues values = new ContentValues();
values.put(DbaseOpenHelper.COLUMN_CLIP_TXT_NUM,model.getClipTxtNumber());
  values.put(DbaseOpenHelper.COLUMN_PROBABILITY,model.getProbability());
values.put(DbaseOpenHelper.COLUMN_TURN_ZERO,model.getTurnZero());
values.put(DbaseOpenHelper.COLUMN_TURN_ONE,model.getTurnOne());
values.put(DbaseOpenHelper.COLUMN_TURN_TWO,model.getTurnTwo());

///here is where the insert happens.
long insertId = database.insert(DbaseOpenHelper.TABLE_TING_DATA, null, values);
model.setId(insertId);

return model;	
}

/*
* To query the entire table and return everything from it
* Uses the query and cursor class (query returns cursor)
* Creates a list of models and a model object.
* Cursor will begin before the first row of the database
* while movetonext() is true.
* Each column is added to the model object
* Each model object is added to the modellist.
*
* @return the modellist
*/
public List<Model> findAll()
{
List<Model> modelList = new ArrayList<Model>();
Cursor cursor = database.query(DbaseOpenHelper.TABLE_TING_DATA, allColumns,
null, null, null, null, null);

//getCount() obviously returns the number of rows in the database
Log.i(LOGTAG, cursor.getCount() + " = count");
if (cursor.getCount() > 0)
{
while (cursor.moveToNext())
{
Model model = new Model();
model.setId(cursor.getLong(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_ID)));
model.setClipTxtNumber(cursor.getString(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_CLIP_TXT_NUM)));
model.setProbability(cursor.getInt(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_PROBABILITY)));
model.setTurnZero(cursor.getInt(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_TURN_ZERO)));
model.setTurnOne(cursor.getInt(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_TURN_ONE)));
model.setTurnTwo(cursor.getInt(cursor.getColumnIndex(DbaseOpenHelper.COLUMN_TURN_TWO)));

modelList.add(model);

}
}
return modelList;
}




public Model findModel(String clip_id_number)
{
//get readable database from the dbhelper class.
// SQLiteDatabase db = dbhelper.getReadableDatabase();

Cursor cursor =
database.query(DbaseOpenHelper.TABLE_TING_DATA,
allColumns,
"clipTxtNumber = ?",
new String[] { clip_id_number},
null,
null,
null,
null);

if (cursor != null)
     cursor.moveToFirst();

 
//pack the model with the db info.
Model model = new Model();
model.setClipTxtNumber(cursor.getString(0));
model.setColumnId(cursor.getString(1));

model.setProbability(cursor.getInt(2));
model.setTurnZero(cursor.getInt(3));
        model.setTurnOne(cursor.getInt(4));
        model.setTurnTwo(cursor.getInt(5));
     return model;
    
    

}
  
/**
* @param clip_id_number
* Gets the histModel from the history table in the database.
* @return
*/

public Model findHistModel(String clip_id_number)
{
//get readable database from the dbhelper class.
// SQLiteDatabase db = dbhelper.getReadableDatabase();

Cursor cursor =
database.query(DbaseOpenHelper.TABLE_HIST_DATA,
allColumns,
"clipTxtNumber = ?",
new String[] { clip_id_number},
null,
null,
null,
null);

if (cursor != null)
     cursor.moveToFirst();

 
//pack the model with the db info.
Model model = new Model();
model.setClipTxtNumber(cursor.getString(0));
model.setColumnId(cursor.getString(1));

model.setProbability(cursor.getInt(2));
model.setTurnZero(cursor.getInt(3));
        model.setTurnOne(cursor.getInt(4));
        model.setTurnTwo(cursor.getInt(5));
     return model;
    
    

}


public void update_data(String clip_id_number,
int turn_zero,int turn_one,int turn_two){

String[] whereArgs = {clip_id_number};


//Log.i(LOGTAG,"update_data: clip_id_number = " + clip_id_number + " probability = " + new_probability);
ContentValues data = new ContentValues();
//data.put(DbaseOpenHelper.COLUMN_PROBABILITY, new_probability);
data.put(DbaseOpenHelper.COLUMN_TURN_ZERO,turn_zero);
data.put(DbaseOpenHelper.COLUMN_TURN_ONE,turn_one);
data.put(DbaseOpenHelper.COLUMN_TURN_TWO,turn_two);

database.update(DbaseOpenHelper.TABLE_TING_DATA, data, DbaseOpenHelper.COLUMN_CLIP_TXT_NUM +"=?", whereArgs);



}
/*
* FIND IF DATA IS PRESENT RETURN TRUE ELSE RETURN FALSE;
*/
public boolean isDataInDatabase(String clip_id_number)
{
//get readable database from the dbhelper class.
// SQLiteDatabase db = dbhelper.getReadableDatabase();

Cursor cursor =
database.query(DbaseOpenHelper.TABLE_TING_DATA,
allColumns,
"clipTxtNumber = ?",
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








}