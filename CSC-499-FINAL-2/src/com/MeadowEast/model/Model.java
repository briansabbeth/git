package com.MeadowEast.model;

/**
 *
 * 
 *  Model interface between the database helper and the database  
 * @author bjsabbeth
 *  @TODO:
 *  	Most likley I will want to take care of the probability doubling as well as the checking 
 *      number of turn updates in this 
 *
 */
public class Model {

		private long id;
		private String clipTxtNumber;
		private String columnId;	
		private int turnZero;
		private int turnOne;
		private int turnTwo;
		
		private int probability;
	
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getClipTxtNumber() {
			return clipTxtNumber;
		}
		public void setClipTxtNumber(String clipTxtNumber) {
			this.clipTxtNumber= clipTxtNumber;
		}
		public String getColumnId() {
			return columnId;
		}
		

		
		public void setInitialProbabilityAndThreeTurns()
		{
			this.probability = 1;
			this.turnZero= 0;
			this.turnOne = 0;
			this.turnTwo = 0;
		}
		

		
		public void setProbability(int probability){
			this.probability = probability;
		}
		
		public void setTurnZero(int turnZero){
			this.turnZero = turnZero;
		}
		
		public int getProbability(){
			return probability;
		}
		public void setTurnOne(int turnOne){
			this.turnOne = turnOne;
		}	
		public void setTurnTwo(int turnTwo){
			this.turnTwo = turnTwo;
		}	
	
		public int getTurnZero(){
			return turnZero;
		}
		
		public int getTurnOne(){
			return turnOne;
		}
		
		
		public int getTurnTwo(){
			return turnTwo;
		}
		
		public void setColumnId(String columnId) {
			this.columnId = columnId;
		}
}
