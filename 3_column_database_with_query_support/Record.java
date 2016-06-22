import java.util.Comparator;

// Immutable.  Stores 3 strings referred to as entity, relation, and
// property. Each Record has a unique integer ID which is set on
// creation.  All records are made through the factory method
// Record.makeRecord(e,r,p).  Record which have some fields wild are
// created using Record.makeQuery(wild,e,r,p)
public class Record implements Comparator<Record>{
	//Declare Global Variables
	public int id;
	public static int nextid;
	public String entity;
	public String relation;
	public String property;
	public String wild;
	
 public static final Comparator<Record> ERPCompare = new Comparator<Record>(){
	 public int compare(Record r1, Record r2){
		 int comparison=0;
		 int ecompare = (r1.entity).compareTo(r2.entity);		//******************
		 int rcompare = (r1.relation).compareTo(r2.relation);	//String Comparisons
		 int pcompare = (r1.property).compareTo(r2.property);	//******************
		 
		 if((ecompare != 0)&& (comparison == 0)){				//Check for entity equality
			 comparison = ecompare;								//Assign value
			 if(r1.entityWild()){								//Check whether wild
				comparison += -700;									//if so weight heavily
			 }
			 if(r2.entityWild()){								//Check whether wild
				 comparison += 700;									//if so weight heavily
			 }
		 }
		 else if((rcompare != 0)&& (comparison == 0)){			//Check for relation equality
			 comparison += rcompare;							//assign value
			 if(r1.relationWild()){								//Check whether wild
				comparison += -600;									//if so weight heavily 
			 }
			 if(r2.relationWild()){								//Check whether wild
				comparison += 600;									//if so weight heavily
			 }
		 }
		 else if((pcompare !=0)&& (comparison == 0)){			//Check for property equality
			 comparison += pcompare;							//assign value
			 if(r1.propertyWild()){								//Check whether wild
				comparison += -500;									//if so weight heavily
			 }
			 if(r2.propertyWild()){								//Check whether wild
				comparison += 500;									//if so weight heavily
			 }
		 }
		 return comparison;										//return final result
	 }
 };
 public static final Comparator<Record> RPECompare = new Comparator<Record>(){
	 public int compare(Record r1, Record r2){
		 int comparison=0;
		 int ecompare = (r1.entity).compareTo(r2.entity);		//******************
		 int rcompare = (r1.relation).compareTo(r2.relation);	//String Comparisons
		 int pcompare = (r1.property).compareTo(r2.property);	//******************
		 
		 if((rcompare != 0)&& (comparison == 0)){				//Check for relation equality
			 comparison += rcompare;							//assign value
			 if(r1.relationWild()){								//Check whether wild
				comparison += -700;									//if so weight heavily
			 }
			 if(r2.relationWild()){								//Check whether wild
				 comparison += 700;									//if so weight heavily
			 }
		 }
		 else if((pcompare != 0)&& (comparison == 0)){			//Check for property equality
			 comparison += pcompare;							//assign value
			 if(r1.propertyWild()){								//Check whether wild
				comparison += -600;									//if so weight heavily
			 }
			 if(r2.propertyWild()){								//Check whether wild
				comparison += 600;									//if so weight heavily
			 }
		 }
		 else if((ecompare != 0)&& (comparison == 0)){			//Check for entity equality
			 comparison += ecompare;							//assign value
			 if(r1.entityWild()){								//Check whether wild
				comparison += -500;									//if so weight heavily
			 }
			 if(r2.entityWild()){								//Check whether wild
				comparison += 500;									//if so weight heavily
			 }
		 }
		 return comparison;										//return final result
	 }
 };
 public static final Comparator<Record> PERCompare = new Comparator<Record>(){
	 public int compare(Record r1, Record r2){
		 int comparison=0;
		 int wcompare = 0;
		 int ecompare = (r1.entity).compareTo(r2.entity);		//******************
		 int rcompare = (r1.relation).compareTo(r2.relation);	//String Comparisons
		 int pcompare = (r1.property).compareTo(r2.property);	//******************
		 
		 if((pcompare != 0) && (comparison == 0)){				//Check for property equality
			 comparison += pcompare;							//assign value			 
			 if(r1.propertyWild()){								//Check whether wild
				comparison += -700;								//if so weight heavily
			 }
			 if(r2.propertyWild()){								//Check whether wild
				comparison += 700;									//if so weight heavily
			 }
		 }
		 else if((ecompare != 0) && (comparison == 0)){			//Check for entity equality
			 comparison += ecompare;							//assign value
			 if(r1.entityWild()){								//Check whether wild
				comparison += -600;									//if so weight heavily
			 }
			 if(r2.entityWild()){								//Check whether wild
				comparison += 600;									//if so weight heavily
			 }
		 }
		 else if((rcompare != 0 )&& (comparison == 0)){			//Check for relation equality
			 comparison += rcompare;							//assign value
			 if(r1.relationWild()){								//Check whether wild
				comparison += -500;									//if so weight heavily
			 }
			 if(r2.relationWild()){								//Check whether wild		
				comparison += 500;									//if so weight heavily
			 }
		 }
		 return comparison;										//return final result
	 }
 };
 public int compare(Record r1, Record r2){
	 //generic compare method required for classes with comparator implementation
	 int ecompare = (r1.entity).compareTo(r2.entity);
	 int rcompare = (r1.relation).compareTo(r2.relation);
	 int pcompare = (r1.property).compareTo(r2.property);
	 int total = ecompare + rcompare + pcompare;
	 return total;
 }
  // Return the next ID that will be assigned to a Record on a call to
  // makeRecord() or makeQuery()
  public static int nextId(){
	  return nextid;				//retrieve global variable
  }

  // Return a stringy representation of the record. Each string should
  // be RIGHT justified in a field of 8 characters with whitespace
  // padding the left.  Java's String.format() is useful for padding
  // on the left.
  public String toString(){
	  //build and return the string from the global string variables
	  String space = " ";
	  String represent = String.format("%8s%s%8s%s%8s%s",entity,space,relation,space,property,space);
	  return represent;
  }
  // Return true if this Record matches the parameter record r and
  // false otherwise. Two records match if all their fields match.
  // Two fields match if the fields are identical or at least one of
  // the fields is wild.
  public boolean matches(Record r){
	  //Check for all possible combinations of a match taking place based on value of the fields or wildness
	  if((this.entity == r.entity)&&(this.relation == r.relation)&&(this.property == r.property)){
		  return true;
	  }//exact match _ _ _
	  else if((this.entity == r.wild)&&(this.relation == r.relation)&&(this.property == r.property)){
		  return true;
	  }//entity wild * _ _
	  else if((this.entity == r.entity)&&(this.relation == r.wild)&&(this.property == r.property)){
		  return true;
	  }//relation wild _ * _
	  else if((this.entity == r.entity)&&(this.relation == r.relation)&&(this.property == r.wild)){
		  return true;
	  }//property wild _ _ *
	  else if((this.entity == r.entity)&&(this.relation == r.wild)&&(this.property == r.wild)){
		  return true;
	  }//relation and property wild _ * * 
	  else if((this.entity == r.wild)&&(this.relation == r.wild)&&(this.property == r.property)){
		  return true;
	  }//entity and relation wild * * _
	  else if((this.entity == r.wild)&&(this.relation == r.relation)&&(this.property == r.wild)){
		  return true;
	  }//entity and property wild * _ *
	  else if((this.entity == r.wild)&&(this.relation == r.wild)&&(this.property == r.wild)){
		  return true;
	  }//all wilds * * *
	  else if((this.entity == r.entity)&&(this.relation == r.relation)&&(this.propertyWild())){
		  return true;
	  }//tests back and forth _ _ *
	  else if((this.entity == r.entity)&&(this.relation == r.relation)&&(r.propertyWild())){
		  return true;
	  }//tests back and forth _ _ *
	  else if((this.entity == r.entity)&&(this.relationWild())&&(this.property == r.property)){
		  return true;
	  }//tests back and forth _ * _
	  else if((this.entity == r.entity)&&(r.relationWild())&&(this.property == r.property)){
		  return true;
	  }//tests back and forth _ * _
	  else if((this.entityWild())&&(this.relation == r.relation)&&(this.property == r.property)){
		  return true;
	  }//tests back and forth * _ _
	  else if((r.entityWild())&&(this.relation == r.relation)&&(this.property == r.property)){
		  return true;
	  }//tests back and forth * _ _
	  else if((this.entityWild())&&(this.relationWild())&&(this.property == r.property)){
		  return true;
	  }//tests back and forth * * _
	  else if((r.entityWild())&&(r.relationWild())&&(this.property == r.property)){
		  return true;
	  }//tests back and forth * * _
	  else if((this.entity == r.entity)&&(this.relationWild())&&(this.propertyWild())){
		  return true;
	  }//tests back and forth _ * *
	  else if((this.entity == r.entity)&&(r.relationWild())&&(r.propertyWild())){
		  return true;
	  }//tests back and forth _ * *
	  else if((this.entityWild())&&(this.relation == r.relation)&&(this.propertyWild())){
		  return true;
	  }//tests back and forth * _ *
	  else if((r.entityWild())&&(this.relation == r.relation)&&(r.propertyWild())){
		  return true;
	  }//tests back and forth * _ *
	  else if((this.entityWild())&&(this.relationWild())&&(this.propertyWild())){
		  return true;
	  }//tests back and forth * * *
	  else if((r.entityWild())&&(r.relationWild())&&(r.propertyWild())){
		  return true;
	  }//tests back and forth * * *
	  else{
		  return false;
	  }
  }
  // Return this record's ID
  public int id(){
	  return id;
  }
  // Accessor methods to access the 3 main fields of the record:
  // entity, relation, and property.
  public String entity(){
	  return entity;						//retrieve string value
  }
  public String relation(){
	  return relation;						//retrieve string value
  }
  public String property(){
	  return property;						//retrieve string value
  }
  // Returns true/false based on whether the the three fields are
  // fixed or wild.
  public boolean entityWild(){
	  if(this.entity == wild)				//compare entity to wild
		  return true;
	  else									//return result
		  return false;
  }
  public boolean relationWild(){
	  if(this.relation == wild)				//compare relation to wild
		  return true;
	  else									//return result
		  return false;
  }
  public boolean propertyWild(){
	  if(this.property == wild)				//compare propert to wild
		  return true;
	  else									//return result
		  return false;
  }
  public String getWild(){
	  return wild;							//retrieve string value
  }
  // Factory method to create a Record. No public constructor is
  // required.
  public static Record makeRecord(String entity, String relation, String property){
	  //Prevent record creation containing null values, throw exception
	  if((entity == null)||(relation == null)||(property == null)){
		  throw new  IllegalArgumentException();
	  }
	  Record r = new Record();  	//create a record, assign values
	  	r.id = nextid;				//get id from static global
	  	r.entity = entity;			//*****************************
	  	r.relation = relation;		//assign values from parameters
	  	r.property = property;		//*****************************
	  		nextid++;				//increment next id		
	  return r;						//return the record
  }
  // Create a record that has some fields wild. Any field that is
  // equal to the first argument wild will be a wild card
  public static Record makeQuery(String wild, String entity, String relation, String property){
	  //Prevent record creation containing null values, throw exception
	  if((wild == null)||(entity == null)||(relation == null)||(property == null)){
		  throw new  IllegalArgumentException();
	  }	  
	  Record r = new Record();  	//create a record, assign values
	  	r.id = nextid;				//get id from static global
	  	r.entity = entity;			//*****************************
	  	r.relation = relation;		//assign values from parameters
	  	r.property = property; 
	  	r.wild = wild;	  			//*****************************
	  		nextid++;				//increment next id
	  return r;						//return the record
  }
}