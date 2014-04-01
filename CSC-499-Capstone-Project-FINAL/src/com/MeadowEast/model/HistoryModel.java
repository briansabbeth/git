/***
 * B.Sabbeth: History Model.
 * A database model to help with input and output from and to the db. 
 *///Will give and arrayList. to Jon.
 
package com.MeadowEast.model;
public class HistoryModel {

	String clip_id;
	String short_hanzi;
	long id;
	/**
	 * Constructors.
	 */
	public HistoryModel(){
		;
	}
	
	/***
	 * @param clip_id
	 * @param hanzi
	 *      Both clips are input from main or from the databaseHelper.
	 */
	public HistoryModel(long id,String clip_id, String hanzi)
	{    
		this.id = id;
		this.clip_id = clip_id;
		this.short_hanzi = hanzi.substring(0, Math.min(hanzi.length(), 3));		
	}
	

	/**
     * Setters and Getters of the clip_id's and the trunctted hanzi. 
     */	
	public void set_clip_id(String clip_id)
	{
		this.clip_id = clip_id;
	}
	
	public void set_short_hanzi(String hanzi)
	{
		this.short_hanzi = hanzi.substring(0, Math.min(hanzi.length(), 3));		
	}
	
	public String get_clip_id(){return this.clip_id;}

	public String get_short_hanzi(){return this.short_hanzi;}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	
}
