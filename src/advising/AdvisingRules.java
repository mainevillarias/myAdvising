package advising;

import java.util.LinkedList;
import java.util.List;

public abstract class AdvisingRules{

	static RequisiteTable reqTable = Curriculum.reqTable;
	static SubjectOffering offr = Curriculum.offr;
	static LinkedList<Subject> subTaken = Curriculum.subjectsTaken;
	
	public static int[] insertChecker(Subject s, Semester sem){
		int[] test= {0,0,0};
		
		
		//check if subject is a prereq
		if(!hasPrereqIssue(s, sem))
			test[0] = 1;
		
		//check if subject is offered for current sem
		if(isOffered(s.getCode(), sem.getTerm()))
			test[1] = 1;
		
		//check if subject has passed level_required
		if(isLevelPassed(s, sem.getYear()))
			test[2] = 1;
		
		return test;
	}
	
	public static int[] pullChecker(Subject s, Semester sem){
		int[] test = {0,0,0,0,0};
		
		if(isPrereqCleared(s, sem))
			test[0] = 1;
	
		if(isCoreqCleared(s, sem))
			test[1] = 1;
					
		if(isOffered(s.getCode(), sem.getTerm()))
			test[2] = 1;	
				
		if(isLevelPassed(s, sem.getYear()))
			test[3] = 1;
			
		if(!isSubjectTaken(s))
			test[4] = 1;
			
		return test;
	}
	
	public static boolean isOffered(int code, int trm){
		return (offr.isOffered(code, trm));
	}
	
	public static boolean isSubjectTaken(Subject s){
		if(s.getSubjectTakenCode()!=0)
			return true;
		
		return false;
	}
	
	public static  boolean hasPrereqIssue(Subject s, Semester sem)
	{
		List<Subject> subjectsWithPrereq = reqTable.requiresOf(s);
		if(!subjectsWithPrereq.isEmpty())
			for(Subject s1: sem.getSubjects())
				if(subjectsWithPrereq.contains(s1))
					return true;
				
		return false;
	}
	
	public static boolean isPrereqCleared(Subject s, Semester sem){
		LinkedList<Subject> prereq = reqTable.preReqOf(s);
		if(!prereq.isEmpty()){
			if(!subTaken.containsAll(prereq)){
				for(Subject p : prereq){
					Semester semThatContains = Curriculum.searchSemWithSub(p);
					if(semThatContains!=null)
						if(semThatContains.isAfter(sem) || sem.getSubjects().contains(p))
							return false;
				}
				return true;
			}
			else
				return true;
		}
		return true;
	}
	
	public static boolean isLevelPassed(Subject s, int yr)
	{	
		return ((yr-1)>=s.getLevelReq());
	}
	
	public static boolean isCoreqCleared(Subject co, Semester sem)
	{
		Subject s = reqTable.coreqBy(co);//csc151
		Subject q = reqTable.coReqOf(co);//csc181
			
		if(s!=null){
			if(subTaken.contains(s) || isInOrBefore(s, co))
				return true;
			else	
				return false;
				
		}
		else if(q!=null){
			if(!isInOrBefore(q, co))
				return true;
			else
				return false;
				
		}
		
		return true;
	}
	
	public static int checker(int[] test){
		int result = 0;
		for(int i = 0; i < test.length; i++)
			result+=test[i];
		
		return result;
	}
	
	public static boolean isInOrBefore(Subject s1, Subject s2)
	{
	 	
	 	Semester sem1 = Curriculum.searchSemWithSub(s1);
	 	Semester sem2 = Curriculum.searchSemWithSub(s2);
		
	 	if(sem1==null || sem2==null)
	 		return false;
	 	
	 	return	sem1.isAfter(sem2);
	}
	
	
}
