package advising;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import advising.RatingTable.Difficulty;

public class Semester {
	int yr;
	int trm;
	int allowedUnits = 22;
	LinkedList<Subject> subjects = new LinkedList<Subject>();
	Semester nxtSem;
	
	
	enum State{
		OVERLOAD, UNDERLOAD, REGULAR
	}
	
	public Semester(int yr, int trm, Semester nxtSem) {
		this.yr = yr;
		this.trm = trm;
		this.nxtSem = nxtSem;
	}
	
	public State getState(){
		if ((getLoad() >= (allowedUnits-2)) && (getLoad() <= (allowedUnits+2)))
			return State.REGULAR;
		else if(getLoad()>(allowedUnits+2))
			return State.OVERLOAD;
		else
			return State.UNDERLOAD;
	}
	
	public float getLoad(){
		float totalUnits = 0;
	    	if(!isEmpty()){
	    		for (Subject s : subjects)
	    			totalUnits += s.getCredit();   
	    	}
	    	return totalUnits;
	}
	
	public boolean isEmpty(){
		return subjects.isEmpty();
	}
	
	public void setAllowedUnits(int units){
		allowedUnits = units;
	}
	
	public int getAllowedUnits(){
		return allowedUnits;
	}
	
	public int getYear(){
		return yr;
	}
	
	public void setYear(int yr){
		this.yr = yr;
	}
	
	public int getTerm(){
		return trm;
	}
	
	public void setTerm(int trm){
		this.trm = trm;
	}
	
	public LinkedList<Subject> getSubjects(){
		return subjects;
	}
	
	public void setSubjects(LinkedList<Subject> subjects){
		this.subjects = subjects;
	}
	
	public void addSubject(Subject s){
		subjects.add(s);
		setAllowedUnits((int)getLoad());
	}
	
	public Semester getNext(){
		return nxtSem;
	}
	
	public void setNext(Semester nxtSem){
		this.nxtSem = nxtSem;
	}
	
	public String toString(){
		return getSemWeight()+" "+getState()+" "+getLoad()+"/"+getAllowedUnits()+" ["+yr+"/"+trm+"]\t"+subjects;
		//return " ["+yr+"/"+trm+"]\t"+subjects;
	}
	
	public boolean isBefore(Semester t){
		if(yr>t.getYear())
			return false;
		else if(yr<t.getYear())
			return true;
		else
			return (trm < t.getTerm());
	}
	
	public boolean isAfter(Semester t){
		if(yr > t.getYear())
			return true;
		else if(yr < t.getYear())
			return false;
		else
			return (trm > t.getTerm());
	}
	

	public boolean isIn(int subjectTakenCode)
	{
		for (Subject s : subjects)
		{
			if (s.getCode()==(subjectTakenCode))
			{
				s.setSubjectTakenCode(subjectTakenCode);
				return true;
			}
		}
		return false;
	}
	
	public List<Subject> getSubjectsTaken()
	{
		List<Subject> subjectsTaken = new LinkedList<Subject>();
		for(Subject s: subjects)
			if(s.getSubjectTakenCode()!=0)
				subjectsTaken.add(s);
			  
		return subjectsTaken;
	}
	
	public List<Subject> getSubjectsNotTaken(){
		List<Subject> notTaken = new LinkedList<Subject>();
		for(Subject s: subjects)
			if(s.getSubjectTakenCode()==0)
				notTaken.add(s);
		
		return notTaken;
	}
	
	
	public Semester createNextSem()
	{
		int year = yr;
		int term = trm;
		if(term==2)
		{
			term=1; year+=1;
		}
		else
			term+=1;
		
		return new Semester(year, term, null);
			
	}
	
	
	
	public void insert(Subject s){
		int test[] = AdvisingRules.insertChecker(s, this);	
		
		subjects.addFirst(s);
		sortByPriority();
		
		if(test[0]==0)
			preReqRemover(s);	
		
		if(test[1]==0 || test[2]==0)
			coReqRemover(s);
		
		organize();
	}
	
	public void organize(){
	
		while(getState()==State.OVERLOAD ){
			if(nxtSem==null)
				nxtSem = createNextSem();
			
			coReqRemover(subjects.getLast());
			
		}
		
	}
	
	
	/***
	 * Removes subject in a semester and automatically replaces it with a new one.
	 * @param s - subject to be replaced.
	 */
	public void replaceSub(Subject s){
		subjects.remove(s);
		Subject fillInSub = pullSubject();
		
		if(fillInSub!=null){
			Semester sFill = Curriculum.searchSemWithSub(fillInSub);
			Subject co = Curriculum.reqTable.coReqOf(fillInSub);
			Semester sCo = Curriculum.searchSemWithSub(co);
			
			insert(fillInSub);
			
			if(co!=null){
				insert(co);
				sCo.replaceSub(co);
			}
				
			sFill.replaceSub(fillInSub);
			
		}
	}
	
	
	
	public void coReqRemover(Subject s){//remove subjects with/without coreq
		if(nxtSem==null)
			nxtSem = createNextSem();
		
		Subject r = Curriculum.reqTable.coReqOf(s);
		Semester semContainer = Curriculum.searchSemWithSub(r);
		if(r!=null && semContainer!=null)
		{
			semContainer.subjects.remove(r);
			nxtSem.insert(r);
		}
			
			subjects.remove(s);
			nxtSem.insert(s);
	}
	
	public void preReqRemover(Subject s){//remove subjects with prereq
		List<Subject> subs = Curriculum.reqTable.requiresOf(s);
		if(!subs.isEmpty())
			for(Subject p : subs)
				if(subjects.contains(p) )
					coReqRemover(p);	
	}
	
	/**
	 * @return subject that met all rules and conditions
	 */
	public Subject pullSubject(){
		for(Semester sem = nxtSem; sem!=null; sem = sem.getNext()){
				for(Subject s: sem.getSubjects()){
					int[] test = AdvisingRules.pullChecker(s, this);
					if(AdvisingRules.checker(test)==5)
						return s;
			}
		}
	
		return null;
	}
	
	
	
	
	public void sortByPriority()
  	{
		Collections.sort(subjects, Collections.reverseOrder());	
	}
	
	
	
	//for version 2 - organize
	public void adjust(int yr, int trm, int allowedUnits){
		Semester sem = Curriculum.searchSem(yr, trm);
		if(nxtSem!=null){
			if(this==sem){
				setAllowedUnits(allowedUnits);
				getSubjectsTaken().forEach(s -> replaceSub(s));
				organize();
			}
			else if(this.isAfter(sem)){
				getSubjectsTaken().forEach(s -> replaceSub(s));
				organize();
			}
			else{
				//setAllowedUnits(0);
				getSubjectsNotTaken().forEach(s -> nxtSem.insert(s));
				subjects.clear();
			}
			nxtSem.adjust(yr, trm, allowedUnits);
		}
		else if(nxtSem==null)
			getSubjectsTaken().forEach(s -> subjects.remove(s));
			
	}
	

	
	//for technical electives
	public boolean isElectiveIn(int e){
		//997 - code for technical elective subject
		for(Subject s: subjects)
			if(s.getCode()==997 && s.getSubjectTakenCode()==0){
				s.setSubjectTakenCode(e);
				return true;
			}
		
		return false;
				
	}
	
	/**
	 * Return subjects marked as COMPLEX in a semester.
	 * @return list of COMPLEX subjects
	 */
	public LinkedList<Subject> getDifficultSubjects(){
		LinkedList<Subject> result = new LinkedList<Subject>();
		RatingTable ratings = Curriculum.ratings;
		
		for(Subject s:subjects){
			if(ratings.getDifficulty(s)==RatingTable.Difficulty.DIFFICULT)
				result.add(s);
		}
		
		
		
		return result;
	}
	
	/*
	 * for peer-aided recommendation
	 * semester weight is based on subject ratings
	 */
	public float getSemWeight(){
		float result = 0;
		RatingTable ratings = Curriculum.ratings;
		
		for(Subject s: subjects)
			result+=ratings.getSubjectWeight(s);
		
		return result;
	}
	

	public void fillSubjects(){
		Subject sub = pullSubject();
		
		while(getState()==State.UNDERLOAD && sub!=null){
			
			Semester semWithSub = Curriculum.searchSemWithSub(sub);	
			insert(sub);
			
			semWithSub.replaceSub(sub);
			sub = pullSubject();
		}
		
	}
	
	/**
	 * Fills remaining units of the semester with subjects from other semester.
	 * @param diff - defines the type of subject to be added
	 */
	public void fillSubjects(Difficulty type){
		Subject easy = null;
		Subject difficult = null;
		
		if(type==Difficulty.EASY){
			easy = pullEasySubject();
			while(getState()==State.UNDERLOAD && easy!=null){
				
				Semester semWithEasy = Curriculum.searchSemWithSub(easy);	
				insert(easy);
				
				semWithEasy.replaceSub(easy);
				easy = pullEasySubject();
			}
		}
			
		else{
			difficult = pullDifficultSubject();
			while(getState()==State.UNDERLOAD && difficult!=null){
				
				Semester semWithDifficult = Curriculum.searchSemWithSub(difficult);	
				insert(difficult);
				
				semWithDifficult.replaceSub(difficult);
				difficult = pullEasySubject();
			}
		}
			
		
		
		
	}
	
	public Subject pullEasySubject(){
		RatingTable ratings = Curriculum.ratings;
		for(Semester sem = nxtSem; sem!=null; sem = sem.getNext()){
			for(Subject s : sem.getSubjects()){
				int[] test = AdvisingRules.pullChecker(s, this);
				if(AdvisingRules.checker(test)==5)
					if(ratings.getDifficulty(s)==Difficulty.EASY)
						return s;
			}
		}
		
		return null;
	}
	
	public Subject pullDifficultSubject(){
		RatingTable ratings = Curriculum.ratings;
		for(Semester sem = nxtSem; sem!=null; sem = sem.getNext()){
			for(Subject s : sem.getSubjects()){
				int[] test = AdvisingRules.pullChecker(s, this);
				if(AdvisingRules.checker(test)==5)
					if(ratings.getDifficulty(s)==Difficulty.DIFFICULT)
						return s;
			}
		}
		
		return null;
	}
	
	

	
	
	
	

}
