package advising;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import advising.RatingTable.Difficulty;
import advising.Semester.State;


public class Curriculum {

	static Semester headSem = new Semester(0, 0, null);
	static LinkedList<Subject> subjectsTaken = new LinkedList<Subject>();
	static RequisiteTable reqTable = new RequisiteTable();
	static SubjectOffering offr = new SubjectOffering();
	static RatingTable ratings = new RatingTable();
	static LinkedList<Subject> electives = new LinkedList<Subject>();
	
	public Curriculum() {
		
	}
	
	public void setElectives(LinkedList<Subject> list){
		electives = list;
	}
	
	public LinkedList<Subject> getElectives(){
		return electives;
	}
	
	public void setSubjectsTaken(LinkedList<Subject> subsTaken){
		subjectsTaken = subsTaken;
	}
	
	public List<Subject> getSubjectsTaken(){
		return subjectsTaken;
	}
	
	public void setRatingTable(RatingTable ratings){
		Curriculum.ratings = ratings;
	}
	
	public void assignSubPriority(){
		for(Semester sem = getFirstSem(); sem!=null; sem = sem.getNext()){
			sem.getSubjects().forEach(n ->  n.setPriority(reqTable.getPriority(n)));
			sem.sortByPriority();
		}
		
	}
	
	public static Semester getFirstSem(){
		return headSem.getNext();
	}
	
	public Semester getLastSem(){
		if(isEmpty())
			return null;
		
		Semester t = getFirstSem();
		while(t.getNext() != null)
			t = t.getNext();
		
		return t;
	}
	
	public void insertSem(Semester sem){//append sem
		if(isEmpty()){
			sem.setNext(headSem.getNext());
			headSem.setNext(sem);
		}
		else
		getLastSem().setNext(sem);
	}
	
	/***
	 * Automatically creates new semester in the right order.
	 * @param yr
	 * @param trm
	 */
	public void createSem(int yr, int trm){
		if(isEmpty()){
			Semester newSem = new Semester(yr, trm, headSem.getNext());
			headSem.setNext(newSem);
		}
		else{	
			Semester p = headSem;
			Semester t = p.getNext();
			Semester newSem = new Semester (yr, trm, null);
			
			while(t != headSem){
				if(newSem.isBefore(t)){
					newSem.setNext(t);
					p.setNext(newSem);
					return;
				}
				
				else if(newSem.isAfter(t) && t.getNext()==null){
					newSem.setNext(t.getNext());
					t.setNext(newSem);
					return;
				}
				
				p = p.getNext(); 
				t = t.getNext();
			}
			
		}
	
	}
	
	/**
	 * Automatically inserts a subject to its designated semester.
	 * @param s - subject
	 * @param yr - semester year
	 * @param trm - semester term
	 */
	public void addSubjectToSem(Subject s, int yr, int trm){
		Semester sem = searchSem(yr, trm);
		if(sem!=null)
			sem.addSubject(s);
		
		
		else{
			createSem(yr, trm);
			Semester newSem = searchSem(yr, trm);
			newSem.addSubject(s);
		}
	}
	
	public static Semester searchSem(int yr, int trm){
		Semester sem = getFirstSem();
		
		while(sem!=null){
			if(sem.getYear() == yr && sem.getTerm() == trm)
				return sem;
			sem = sem.getNext();
		}
			
		return null;
	}
	
	public boolean isEmpty(){
		return headSem.getNext()==null;
	}
	
	/**
	 * Returns a subject from the curriculum with the provided code.
	 * @param code - code of the subject to be searched.
	 * @return <code>null</code> if the subject is not found.
	 */
	public Subject getSubjectByCode(int code)
	{  
		
		Set<Subject> subjects = new HashSet<Subject>();
		for(Semester sem = getFirstSem(); sem!=null; sem = sem.getNext())
			subjects.addAll(sem.getSubjects());
		
		subjects.addAll(subjectsTaken);
		subjects.addAll(electives);
		
		for(Subject s: subjects)
			if(code==s.getCode())
				return s;
		
		return null;
	}
	
	
	
	public void organize(int yr, int trm, int allowedUnits){	
		//version 2 - organize subjects
		headSem.adjust(yr, trm,allowedUnits);
		
		Semester tmp = getFirstSem();
		Semester sem = searchSem(yr, trm);
	
		while(tmp!=null){
			//Semester nxtSem = tmp.getNext();
			if(tmp==sem || tmp.isAfter(sem)){
				if(tmp.getState()==State.UNDERLOAD)
				tmp.fillSubjects();
			}
			tmp = tmp.getNext();
			
		}
	}
	
	
	
	public RequisiteTable getRequisiteTable(){
		return reqTable;
	}
	
	public void setRequisiteTable(RequisiteTable reqTable){
		Curriculum.reqTable = reqTable;
	}
	
	public void setSubOffering(SubjectOffering offr){
		Curriculum.offr = offr;
	}
	
	public void setSubjectsTakenCode(List<Integer> subjectsTakenCodes){
		subjectsTakenCodes.add(3);
		subjectsTakenCodes.add(2);
		subjectsTakenCodes.add(11);
		subjectsTakenCodes.add(69);
		subjectsTakenCodes.add(14);
		subjectsTakenCodes.add(43);
		subjectsTakenCodes.add(13);
		for(int i : subjectsTakenCodes){
			Subject s = getSubjectByCode(i);
			
			if(s!=null){
				subjectsTaken.add(getSubjectByCode(i));
				
				if(isElective(i))
					markElectiveTaken(i);
				else
					markSubjectTaken(i);
			}
			
		}
	
	
	}
	
	public void markSubjectTaken(int i){
		for(Semester sem = getFirstSem(); sem!=null; sem = sem.getNext())
			if(sem.isIn(i))
				return;
	}
	
	public void markElectiveTaken(int i){
		for(Semester sem = getFirstSem(); sem!=null; sem = sem.getNext())
			if(sem.isElectiveIn(i))
				return;
	}
	
	
	//for peer-aided recommendation version 1
	public void applyHyrbidRecommendation(){
		int semLeft = getNumberOfSem();
		float semWeight = ratings.getTotalWeightOfUntakenSubjects()/semLeft;
		int max = 0;
		
		System.out.println("PREVIOUS NUMBER OF SEM: "+semLeft);
		System.out.println("WEIGHT PER SEMESTER: "+semWeight);
		System.out.println("LIST OF COMPLEX SUBJECTS (total="+getDifficultSubjects().size()+") : "+getDifficultSubjects());
		
		max = getDifficultSubjects().size()/semLeft;
		if(max<=0)
			max = 1;
		System.out.println("No. of COMPLEX subjects per sem: (atleast "+(max)+" or "+(max-1)+")");
		
		for(Semester sem = getFirstSem(); sem != null; sem = sem.getNext()){
			Semester nxtSem = sem.getNext();
			if(!sem.getSubjects().isEmpty()){
				
				LinkedList<Subject> list = sem.getDifficultSubjects();
				while(list.size()>max && nxtSem!=null){
				
					nxtSem.insert(list.getLast());//subject with least priority
					sem.getSubjects().remove(list.getLast());
					list = sem.getDifficultSubjects();
				}
				if(sem.getState()==State.UNDERLOAD)
					sem.fillSubjects(Difficulty.EASY);
			}
		}
		
		
	}
	
	public void applyPeerAidedRecommendation(){
		int semLeft = getNumberOfSem();
		int max = 0;
		
		max = getDifficultSubjects().size()/semLeft;
	
		
		for(Semester sem = getFirstSem(); sem != null; sem = sem.getNext()){
			Semester nxtSem = sem.getNext();
			if(!sem.getSubjects().isEmpty()){
				
				LinkedList<Subject> list = sem.getDifficultSubjects();
				while(list.size()>max && nxtSem!=null){
					
					nxtSem.insert(list.getLast());//subject with least priority
					sem.getSubjects().remove(list.getLast());//MUST CHANGE THIS!
					list = sem.getDifficultSubjects();
				}
				if(sem.getState()==State.UNDERLOAD)
					sem.fillSubjects(Difficulty.EASY);
			}
		}
		
	}
	
	
	/**
	 * Returns the semester containing a subject s.
	 * @param s - subject that belongs to a semester.
	 * @return <code>null</code> if subject is already taken or not present in the curriculum
	 * 
	 */
	public static Semester searchSemWithSub(Subject s){
		for(Semester sem = getFirstSem(); sem != null; sem = sem.getNext())
			if(sem.getSubjects().contains(s))
				return sem;
		
		return null;
	}
	
	public int getTotalUnits(){
		int total = 0;
		for(Semester sem = getFirstSem(); sem != null; sem = sem.getNext())
			total+=sem.getLoad();
		
		return total;
	}
	
	public void sort(){
		for(Semester sem = getFirstSem(); sem!=null; sem=sem.getNext())
			sem.sortByPriority();
	}
	
	
	public String toString(){
		StringBuffer s = new StringBuffer("");
		if(!isEmpty())
			for(Semester sem = getFirstSem(); sem!=null; sem = sem.getNext())
				s.append(sem.toString()).append("\n");
			
		return s.toString();
		
	}
	
	//for electives
	public boolean isElective(int i){
		for(Subject s: electives)
			if(s.getCode()==i)
				return true;

		return false;
	}
	
	
	/**
	 * Determines student current year level based on the number of units already taken.
	 * @return student current year level
	 */
	public int getCurrentYearLevel(){
		int yrLevel = 0;
		int unitsTaken = 0;
		
		for(Subject s: subjectsTaken)
			unitsTaken += s.getCredit();
		
		for(Semester sem = getFirstSem(); sem!= null; sem = sem.getNext()){
			if(unitsTaken<=sem.getAllowedUnits())
				return sem.getYear();
		}
		return yrLevel;
	}
	
	/**
	 * Determines number of semester to be taken.
	 * @return number of semesters
	 */
	public int getNumberOfSem(){
		int n = 0;
		Semester sem = getFirstSem();
		while(sem!=null){
			if(!sem.getSubjects().isEmpty())
				n+=1;
			sem = sem.getNext();
		}
		return n;
	}
	
	/**
	 * <b>getDifficultSubjects</b>
	 * @return list of COMPLEX subjects
	 */
	public LinkedList<Subject> getDifficultSubjects(){
		LinkedList<Subject> list = new LinkedList<Subject>();
		Semester sem = getFirstSem();
		
		while(sem!=null){
			list.addAll(sem.getDifficultSubjects());
			sem = sem.getNext();
		}
		
		return list;
	}
		

}
