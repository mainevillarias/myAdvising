package advising;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


public class MainProgram {

	public static String url = "jdbc:postgresql://127.0.0.1:5432/advisingDb";
	static Curriculum bscs = new Curriculum();

	public static void main(String[] args) {
		
		int programCode = 1;
		int studentCode = 1;
		int yrLevel = 1;
		int semToEnroll = 2;
		int allowedUnits = 23;
		
		dbConnect();
		buildCurriculum(programCode);
		buildRequisiteTable(programCode);
		buildRatingTable(programCode);
		assignSubjectPriority();
		setElectiveList(programCode);
		setSubjectsTaken(studentCode);
		organizeCurriculum(yrLevel, semToEnroll, allowedUnits);
		
		System.out.println("\nSUBJECTS TAKEN\n"+bscs.getSubjectsTaken()+"\n");
		System.out.println(bscs);
		
		System.out.println("\nHYBRID RECOMMENDATION\n");
		bscs.applyHyrbidRecommendation();
		System.out.println("\n"+bscs);
		
		System.out.println("\nTotal units to be taken: "+bscs.getTotalUnits());
		
		System.out.println("student current yrLevel = "+bscs.getCurrentYearLevel());
		System.out.println("OVERALL WEIGHT (taken+untaken subjects) = "+Curriculum.ratings.getOverallWeight());
		System.out.println("OVERALL WEIGHT (untaken subjects only) = "+Curriculum.ratings.getTotalWeightOfUntakenSubjects());
	
	}//end of main
	
	public static void dbConnect(){
		try{
			Class.forName("org.postgresql.Driver");
		}
	    catch( Exception e ){
	    	System.out.println("Failed to load postgresql driver.");
	    	return;
	    }
	}
	
	public static void assignSubjectPriority(){
		bscs.assignSubPriority();
	}
	
	
	public static void buildCurriculum(int programCode){
		//for subject offering
		SubjectOffering subOffering = new SubjectOffering();
		
    	
		try{
			Connection conn = DriverManager.getConnection(url, "postgres", "postgres");
			Statement select = conn.createStatement();
			ResultSet result = select.executeQuery
                    ("select * from subject inner join curriculum "
                    		+ "on subject.course_code = curriculum.course_code where "
                    		+ "program_code = '"+programCode+"'  order by year,term asc");
			while(result.next()) { // process results one row at a time
				
				int levelReq = 0;
				int priority=0;
				
				String id = result.getString("course_id");
				String level_req = (String) result.getString("level_req");
		    	int code = result.getInt("course_code");
		    	double credit = result.getDouble("units");
		    	boolean isMajor = result.getBoolean("is_major");
		    	byte offr = result.getByte("sem_offering");
			    
		    	if(level_req!=null)
		    		levelReq = Integer.parseInt(level_req,2);
		    	
		    	if(isMajor) 
		    		priority = 1;
		   
		    	Subject s = new Subject(code, id, credit, priority, levelReq);
		    	int yearLevel = result.getInt("year");
		    	int term = result.getInt("term");
		    
		    	bscs.addSubjectToSem(s, yearLevel, term);
		    	subOffering.setSubOffering(code, offr);
			}
		      select.close();
		      conn.close();
		 }
		 catch( Exception e ) {
		      e.printStackTrace();
		 }
	 
		bscs.setSubOffering(subOffering);
	}
	
	//NOTE: this only works if curriculum is already built
	public static void buildRequisiteTable(int programCode){
		RequisiteTable requisiteTable = new RequisiteTable();
				
		try {
			Connection con = DriverManager.getConnection(url, "postgres", "postgres");
			Statement select = con.createStatement();
			ResultSet result = select.executeQuery
					("select requisite.course_code, requisite.prereq_code, requisite.coreq_code from requisite "
							+ "inner join curriculum on requisite.course_code = curriculum.course_code "
			                + "where curriculum.program_code = '"+programCode+"' ");         
			     
			while(result.next()) { // process results one row at a time
				int courseCode = result.getInt("course_code");
				int prereqCode = result.getInt("prereq_code");
				int coreqCode = result.getInt("coreq_code");
			    	
				Subject subject = bscs.getSubjectByCode(courseCode);
				Subject prereqSubject = bscs.getSubjectByCode(prereqCode);
				Subject coreqSubject = bscs.getSubjectByCode(coreqCode);
				
				if(prereqSubject!=null && coreqSubject!=null){
					requisiteTable.addPreReq(subject, prereqSubject);	 
					requisiteTable.addCoReq(subject, coreqSubject); 
				}
				else if(coreqSubject==null && prereqSubject!=null){
					requisiteTable.addPreReq(subject, prereqSubject);	 
				}
				else if(prereqSubject==null && coreqSubject!=null){
			    	requisiteTable.addCoReq(subject, coreqSubject);  	 
			    }	 
			}		    	
			
			select.close();
			con.close();
		}
			
		catch( Exception e ) {
			e.printStackTrace();
		}
		bscs.setRequisiteTable(requisiteTable);
	}

	/*
	 * NOTE: This only works if curriculum is already built
	 */
	public static void buildRatingTable(int programCode){
		 RatingTable ratingTable = new RatingTable();
			
		 try {
			 Connection con = DriverManager.getConnection(url, "postgres", "postgres");
		      Statement select = con.createStatement();
		      ResultSet result = select.executeQuery
		                          ("SELECT enrollment.course_code, enrollment.rating FROM curriculum"
		                          		+ " INNER JOIN enrollment ON curriculum.course_code = enrollment.course_code"
		                          		+ " where curriculum.program_code = '"+programCode+"'");         
		     
		      while(result.next()) { // process results one row at a time
		    	  int courseCode = result.getInt("course_code");
		    	  int rating = result.getInt("rating");
		    	  
		    	  Subject s = bscs.getSubjectByCode(courseCode);
		    	  
		    	  ratingTable.addRating(s, rating);
		      }
		      
		      select.close();
			  con.close();
		 }
		 
		 catch (Exception e){
		 }
		      
		bscs.setRatingTable(ratingTable);
	 }

	
	public static void setElectiveList(int programCode){
		LinkedList<Subject> electives = new LinkedList<Subject>();
		
		try{
			Connection con = DriverManager.getConnection(url, "postgres", "postgres");
			Statement select = con.createStatement();
			ResultSet result = select.executeQuery("SELECT * FROM elective INNER JOIN "
					+ "subject on elective.course_code = subject.course_code where "
					+ "program_code = '"+programCode+"'");
			
			while(result.next()){
				int priority = 0;//not important
				int levelReq = 0;//not important
				String id = result.getString("course_id");
				int code = result.getInt("course_code");
		    	double credit = result.getDouble("units");
		     
		  
		    	Subject s = new Subject(code, id, credit, priority, levelReq);
		    	electives.add(s);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		bscs.setElectives(electives);
		System.out.println("ELECTIVES LIST: "+bscs.getElectives());
	}
	
	public static void setSubjectsTaken(int studentCode){
		List<Integer> subjectsTakenCode = new LinkedList<Integer>();
	//	LinkedList<Subject> subjectsTaken = new LinkedList<Subject>();
		try{
			Connection con = DriverManager.getConnection(url, "postgres", "postgres");
			Statement select = con.createStatement();
			ResultSet result = select.executeQuery
					("select * from grade_view where student_code = '"+studentCode+"'");
			
			while(result.next()) { // process results one row at a time
				int code = result.getInt("course_code");
		    	/*
				int levelReq = 0;
				int priority=0;
				
				String id = result.getString("course_id");
				String level_req = (String) result.getString("level_req");
		    	double credit = result.getDouble("units");
		    	
		    	if(level_req!=null)
		    		levelReq = Integer.parseInt(level_req,2);
		    	
		    	Subject s = new Subject(code, id, credit, priority, levelReq);
				
				subjectsTaken.add(s);
				
				*/
				subjectsTakenCode.add(code);

		    }     

			select.close();
		    con.close();
		}
		 
		catch( Exception e ) {
			e.printStackTrace();
		}
	
		subjectsTakenCode.clear();
		bscs.setSubjectsTakenCode(subjectsTakenCode);
		
	}
	
	public static void organizeCurriculum(int yr, int trm, int units){
		bscs.organize(yr, trm, units);
	}

}
