package advising;

public class Subject implements Comparable<Subject> {
	
	String id;
	
	int subjectTakenCode;
	int priority;
	int code;
	double credit;
	int levelReq;
	
	
	public Subject(int code, String id, double credit, int priority, int levelReq) {
		this.id = id;
		this.code = code;
		this.credit = credit;
		this.priority = priority;
		this.levelReq = levelReq;
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public int getSubjectTakenCode() {
		return subjectTakenCode;
	}


	public void setSubjectTakenCode(int subjectTakenCode) {
		this.subjectTakenCode = subjectTakenCode;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int i) {
		 priority += i;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public double getCredit() {
		return credit;
	}


	public void setCredit(double credit) {
		this.credit = credit;
	}


	public int getLevelReq() {
		return levelReq;
	}


	public void setLevelReq(int levelReq) {
		this.levelReq = levelReq;
	}

	
	public String toString(){
		RatingTable ratings = Curriculum.ratings;
		return (String)(id);
		//return (String)(id)+" (w="+ratings.getSubjectWeight(this)+")";
	}
	
	@Override
	 public int compareTo(Subject o) {
	 	int comparedPriority = o.priority;
	 	if (this.priority > comparedPriority) {
	 		return 1;
	 	} else if (this.priority == comparedPriority) {
	 		return 0;
	 	} else {
	 		return -1;
	 	}
	 }
	

}
