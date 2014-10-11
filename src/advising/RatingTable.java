package advising;

public class RatingTable {

	class Entry{
		public Subject subject;
		public int count;
		public float sum;
		
		public Entry(Subject subject, int count, float sum){
			this.subject = subject;
			this.count = count;
			this.sum = sum;
		}
	}
	
	Entry[] table;
	int count;
	
	public RatingTable(){
		table = new Entry[100];
	}
	
	public void addRating(Subject s, float rate){
		Entry sub = getSubject(s.getCode());
		
		if(sub!=null){
			sub.count+=1;
			sub.sum+=rate;
		}
		else{
			table[count++] = new Entry(s, 1, rate);
		}
	}
	
	public Entry getSubject(int code){
		for(int i = 0; i<count; i++){
			if(table[i].subject.getCode()==code)
				return table[i];
		}
		return null;
	}
	
	

	public String toString(){
		StringBuffer s = new StringBuffer("");
		
		for(int i = 0; i<count; i++)
			s.append("["+i+"] "+table[i].subject.id+"\t\t COUNT: "+table[i].count+" SUM: "+table[i].sum+"\n");
		
		return s.toString();
	}
	

	 public enum Difficulty{
		 //EASY, MEDIUM, TOUGH, DIFFICULT, COMPLEX, NOT_AVAILABLE
		 EASY, DIFFICULT, NOT_AVAILABLE
	 }
	 
	 public Difficulty getDifficulty(Subject s){
		 float ave = getSubjectWeight(s);
		 
		 if(ave>=4)
			 return Difficulty.DIFFICULT;
		 else if(ave<4)
			 return Difficulty.EASY;
		 else
			 return Difficulty.NOT_AVAILABLE;
		
		
	 }	
	 
	 /*
	  * NOTE: this include subjects already taken
	  */
	 public float getOverallWeight(){
		 float total = 0;
		 
		 for(int i=0; i<count; i++)
			 total = total + (table[i].sum/table[i].count);
		 
		 
		 return total;
	 }
	 
	 public float getTotalWeightOfUntakenSubjects(){
		 float total = 0;
		 
		 for(int i=0; i<count; i++)
			 if(table[i].subject.getSubjectTakenCode()==0)
				 total=total+(table[i].sum/table[i].count);
		 return total;
	 }
	 
	 /*
	  * Get average weight of subject based on ratings
	  */
	 public float getSubjectWeight(Subject s){
		float ave = 0;
		Entry subject = getSubject(s.getCode());
			
		if(subject!=null){
			ave = subject.sum/subject.count;
		}
			
		return ave;
	}
	 
	 
}
