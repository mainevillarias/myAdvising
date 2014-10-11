package advising;


import java.util.LinkedList;



public class RequisiteTable
{
  enum Requisite { PRE, CO}; 
  
  class Entry
  {
    public Requisite req;
    public Subject subj1;
    public Subject subj2;
    
    public Entry(Requisite q, Subject s, Subject r)
    {
      req = q;
      subj1 = s;
      subj2 = r;
    }
    
  }

  
   Entry[] table;
   int count;
  
  public RequisiteTable()
  {
    table = new Entry[50];
    count = 0;
  }
  
  public void addPreReq(Subject s, Subject r)
  {
    table[count++] = new Entry(Requisite.PRE, s, r);
  }
  
  public void addCoReq(Subject s, Subject c)
  {
    table[count++] = new Entry(Requisite.CO, s, c);
  }
  
  public boolean isPreReq(Subject s, Subject r)
  {
    for (int i=0; i<count; i++)
      if (table[i].req == Requisite.PRE && table[i].subj1 == s && table[i].subj2 == r)
      return true;
    return false;
  }
  
  public LinkedList<Subject> preReqOf(Subject s)
  {
	  LinkedList<Subject> subs = new LinkedList<Subject>();
	  
	  for (int i=0; i<count; i++)
		  if (table[i].req == Requisite.PRE && table[i].subj1 == s)
			  subs.add(table[i].subj2);
	  return subs;
	  
  }
  
  public boolean isCoreq(Subject s, Subject c){
	  for (int i=0; i<count; i++)
	      if (table[i].req == Requisite.CO && table[i].subj1 == s && table[i].subj2 == c)
	      return true;
	    return false;
  }
  
  public Subject coReqOf(Subject s)
  {
    for (int i=0; i<count; i++)
      if (table[i].req == Requisite.CO && table[i].subj1 == s)
      return table[i].subj2;
    return null;
  }
  
  
 

   public String toString()
   {
	   
	  StringBuffer s = new StringBuffer("");
	  if(!isEmpty()){
	  	for(int i = 0; i<count; i++){
	  		s.append("["+i+"] "+table[i].req+": "+table[i].subj1.id+" -> "+table[i].subj2.id+"\n");
	  	}
	  }
	  return s.append("").toString();
   }
   
   public LinkedList<Subject> requiresOf(Subject r)
   {
		  LinkedList<Subject> subjects= new LinkedList<Subject>();
		  for (int i=0; i<count; i++)
			  if (table[i].req == Requisite.PRE && table[i].subj2 == r)
				  subjects.add(table[i].subj1);
		
		  return subjects;
   }
	  
   public Subject coreqBy(Subject s){
	   
	   for (int i=0; i<count; i++)
			  if (table[i].req == Requisite.CO && table[i].subj2 == s)
				  return table[i].subj1;
	   return null;
   }
   
   public int getPriority(Subject s)
	   {
			  int r = 0;
			  
			  if(!isEmpty()){
				  LinkedList<Subject> subjects = requiresOf(s);
				  for(int i = 0; i<subjects.size(); i++)
				      r = r + getPriority(subjects.get(i));		  	
			  }
				 
			  return r + 1;
		  }
	  
   public boolean isEmpty()
   {
	   return count==0;	 
   }
	 
   public boolean isIn(Subject s)
   {
	
		   for(int i = 0; i<count; i++)
			   if(table[i].subj1.getCode()==s.code || table[i].subj2.getCode()==s.getCode())
				   return true;
	   
	 
		 
	   return false;
   }
	

}