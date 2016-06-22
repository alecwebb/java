// Three-column database that supports query, add, and remove in
// logarithmic time.
import java.util.*;
public class TripleStore extends Record{
  //Declare Global Variables
  public String wildcard = "*"; //default wildcard
  public TreeSet<Record> erp;
  public TreeSet<Record> rpe;
  public TreeSet<Record> per;
	
  // Create an empty TripleStore. Initializes storage trees
  public TripleStore(){
	  erp = new TreeSet<Record>(ERPCompare);		//create ERP TreeSet
	  erp.clear();									//make empty
	  rpe = new TreeSet<Record>(RPECompare);		//create RPE TreeSet
	  rpe.clear();									//make empty
	  per = new TreeSet<Record>(PERCompare);		//create PER TreeSet
	  per.clear();									//make empty
  }
  // Access the current wild card string for this TripleStore which
  // may be used to match multiple records during a query() or
  // remove() call
  public String getWild(){
	  return this.wildcard;	 //retrieve the wildcard
  }
  // Set the current wild card string for this TripleStore
  public void setWild(String w){
	  this.wildcard = w;	//set the wildcard
  }
  // Ensure that a record is present in the TripleStore by adding it
  // if necessary.  Returns true if the addition is made, false if the
  // Record was not added because it was a duplicate of an existing
  // entry. A Record with any fields may be added to the TripleStore
  // including a Record with fields that are equal to the
  // TripleStore's current wild card.
  // 
  // Target Complexity: O(log N)
  // N: number of records in the TripleStore
  public boolean add(String entity, String relation, String property){
	  Boolean added  = false;								//***************************
	  Boolean erpAdd = false;								//Create truth variables to
	  Boolean rpeAdd = false;								//determine each adds success
	  Boolean perAdd = false;								//***************************
	  Record toAdd = makeRecord(entity, relation, property);//create the record to be added
	  
	  if(erp.contains(toAdd)!=true){						//check if record currently exists
		  erp.add(toAdd);										//if not add the record
		  erpAdd=true;										//indicate add took place
	  }
	  if(rpe.contains(toAdd)!=true){						//check if record currently exists
		  rpe.add(toAdd);										//if not add the record
		  rpeAdd=true;										//indicate add took place
	  }
	  if(per.contains(toAdd)!=true){						//check if record currently exists
		  per.add(toAdd);										//if not add the record
		  perAdd=true;										//indicate add took place
	  }
	  
	  if(erpAdd && rpeAdd && perAdd){						//determine if all items were added
		  added = true;										//if yes, return true
	  }
	  else{
		  added = false;									//if no, return false
	  }
	  return added;											//return the result
  }
  // Return a List of the Records that match the given query. If no
  // Records match, the returned list should be empty. If a String
  // matching the TripleStore's current wild card is used for one of
  // the fields of the query, multiple Records may be returned in the
  // match.  An appropriate tree must be selected and searched
  // correctly in order to meet the target complexity.
  // 
  // TARGET COMPLEXITY: O(K + log N) 
  // K: the number of matching records 
  // N: the number of records in the triplestore.
  public List<Record> query(String entity, String relation, String property){
	  //Method variables
	  int choice = 0;													//decision variable
	  List<Record> queryoutput = new ArrayList<Record>();				//create list
	  Record q = makeQuery(this.wildcard, entity, relation, property);	//create recordquery
	  Boolean eW = q.entityWild();										//**************************************
	  Boolean rW = q.relationWild();									//Gather truth values of each element
	  Boolean pW = q.propertyWild();									//**************************************
	  
	  if((eW==true)&&(rW==false)&&(pW==false)){							//**************************************					
		  //use rpe														//Determine the order in which the wilds
		  choice = 2;													//are stored. This will determine which
	  }//Represents: * _ _												//tree to gather the tailset from, assign
	  if((eW==false)&&(rW==true)&&(pW==false)){							//a choice value of 1-3 to represent the
		  //use per														//trees. 1:ERP TREE 2:RPE TREE 3:PER TREE
		  choice = 3;													//***************************************
	  }//Represents: _ * _
	  if((eW==false)&&(rW==false)&&(pW==true)){
		  //use erp
		  choice = 1;
	  }//Represents: _ _ *
	  if((eW==true)&&(rW==true)&&(pW==false)){
		  //use per
		  choice = 3;
	  }//Represents: * * _
	  if((eW==true)&&(rW==false)&&(pW==true)){
		  //use rpe
		  choice = 2;
	  }//Represents: * _ *
	  if((eW==false)&&(rW==true)&&(pW==true)){
		  //use erp
		  choice = 1;
	  }//Represents: _ * *
	  if((eW==true)&&(rW==true)&&(pW==true)){
		  //use erp
		  choice = 1;
	  }//Represents: * * *
	  if((eW==false)&&(rW==false)&&(pW==false)){
		  //use erp
		  choice = 1;
	  }//Represents: _ _ _
	  
	  if(choice == 1){										//Use ERP Tree Tailset
		Iterator<Record> erpTail = erp.tailSet(q).iterator(); //Create Tailset iterator from ERP Tree
		while(erpTail.hasNext()){							//iterate until there is not a match
			  Record current = erpTail.next();				//retrieve record
			  if(current.matches(q)){						//get the correct items
				 queryoutput.add(current);					//add to list
			  }
			  else{
				  break;									//stops when the next encounter is not a match
			  }												//to get subset of tailset
		  }
		  return queryoutput;								//return the subset
	  }
	  else if(choice == 2){									//Use RPE Tree Tailset
		Iterator<Record> rpeTail = rpe.tailSet(q).iterator();//Create Tailset iterator from RPE Tree
		while(rpeTail.hasNext()){							//iterate until there is not a match
			  Record current = rpeTail.next();				//retrieve record
			  if(current.matches(q)){						//get the correct items
				 queryoutput.add(current);					//add to list
			  }
			  else{
				  break;									//stops when the next encounter is not a match
			  }												//to get subset of tailset
		  }
		  return queryoutput;								//return the subset
	  }
	  else if(choice == 3){									//Use PER Tree Tailset
		Iterator<Record> perTail = per.tailSet(q).iterator();//Create Tailset iterator from PER Tree
		while(perTail.hasNext()){							//iterate until there is not a match
			  Record current = perTail.next();				//retrieve record
			  if(current.matches(q)){						//get the correct items
				 queryoutput.add(current);					//add to list
			  }
			  else{
				  break; 									//stops when the next encounter is not a match
			  }												//to get subset of tailset
		  }
		  return queryoutput;								//return the subset
	  }
	  else{	  
		  return queryoutput;								//if something went wrong, return empty list
	  }
  }
  // Remove elements from the TripleStore that match the parameter
  // query. If no Records match, no Records are removed.  Any of the
  // fields given may be the TripleStore's current wild card which may
  // lead to multiple Records bein matched and removed. Return the
  // number of records that are removed from the TripleStore.
  // 
  // TARGET COMPLEXITY: O(K * log N)
  // K: the number of matching records
  // N: the number of records in the triplestore.
  public int remove(String e, String r, String p){
	  int recordsRemoved = 0;								//remove counter
	  List<Record> deletionList = query(e,r,p);				//create list
	  Iterator<Record> itr = deletionList.iterator();		//create list iterator
	  
	  while(itr.hasNext()){									//iterate through query list
		  Record current=itr.next();						//retrieve record
		  erp.remove(current);								//**************
		  rpe.remove(current);								//remove records
		  per.remove(current);								//**************
		  recordsRemoved++;									//increment when deletion takes place
	  }
	  return recordsRemoved;
  }
  // Produce a String representation of the TripleStore. Each Record
  // is formatted with its toString() method on its own line. Records
  // must be shown sorted by Entity, Relation, Property in the
  // returned String. 
  // 
  // TARGET COMPLEXITY: O(N)
  // N: the number of records stored in the TripleStore
  public String toString(){
	  //method variables
	  StringBuilder sb = new StringBuilder(erp.size());		//create string builder
	  Iterator<Record> itr = erp.iterator();				//create iterator
	  
	  while(itr.hasNext()){									//iterate through records of ERP
	      Record current = itr.next();						//retrieve record
	      sb.append(current.toString());					//build the string
	      sb.append("\n");									//add a new line
	  }	  
	  return sb.toString();									//return the string
  }
}