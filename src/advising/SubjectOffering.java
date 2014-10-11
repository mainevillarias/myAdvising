package advising;
import java.util.HashMap;
import java.util.Map;

public class SubjectOffering
{
	
	
	Map<Integer, Byte> semsOffered = new HashMap<Integer, Byte>();
	
	public SubjectOffering()
	{
		/*
		semsOffered.put(12, (byte)100);		//hist 1
		semsOffered.put(2,  (byte)110);		//hist 3
		semsOffered.put(13, (byte)100);		//pe 1
		semsOffered.put(14, (byte)100);		//nstp 1
		semsOffered.put(1,  (byte)111);		//math 17
		semsOffered.put(11, (byte)100);		//csc 100
		semsOffered.put(3,  (byte)110);		//eng 3
		semsOffered.put(69, (byte)100);		//eng 1
		semsOffered.put(43, (byte)110);		//fil 1
		
		semsOffered.put(18, (byte)110);		//bio 1
		semsOffered.put(6,  (byte)110);		//hist 5
		semsOffered.put(5,  (byte)110);		//csc 101
		semsOffered.put(44, (byte)110);		//philo 2
		semsOffered.put(20, (byte)10);		//nstp 2
		semsOffered.put(4,  (byte)110);		//math 51
		semsOffered.put(15, (byte)10);		//eng 2
		semsOffered.put(19, (byte)10);		//pe 2
	
		semsOffered.put(45, (byte)110);		//physics 21
		semsOffered.put(24, (byte)100);		//pe 3
		semsOffered.put(10, (byte)110);		//eng 4
		semsOffered.put(9, (byte)111);		//csc 102
		semsOffered.put(8, (byte)110);		//math 61
		semsOffered.put(7, (byte)110);		//csc 141
		semsOffered.put(46, (byte)110);		//physics 21.1
		
		semsOffered.put(51, (byte)10);		//csc 111
		semsOffered.put(26, (byte)10);		//math 131
		semsOffered.put(28, (byte)111);		//csc121
		semsOffered.put(31, (byte)10);		//pe 4
		semsOffered.put(48, (byte)110);		//math 71
		semsOffered.put(49, (byte)110);		//physics 41
		semsOffered.put(50, (byte)110);		//physics 41.1
		
		semsOffered.put(37, (byte)100);		//csc 124
		semsOffered.put(54, (byte)100);		//math 121
		semsOffered.put(52, (byte)110);		//csc 111.1
		semsOffered.put(12, (byte)110);		//hist 1
		semsOffered.put(47, (byte)100);		//csc 142
		semsOffered.put(32, (byte)101);		//csc 151
		semsOffered.put(38, (byte)100);		//csc 181
		
		semsOffered.put(53, (byte)10);		//csc 144
		semsOffered.put(33, (byte)110);		//polsci 2
		semsOffered.put(56, (byte)10);		//csc 183
		semsOffered.put(57, (byte)10);		//csc 184
		semsOffered.put(58, (byte)10);		//csc 185
		semsOffered.put(59, (byte)10);		//csc 198
		semsOffered.put(29, (byte)10);		//csc 112
		
		semsOffered.put(40, (byte)100);		//csc 186
		semsOffered.put(55, (byte)100);		//csc 145
		semsOffered.put(61, (byte)100);		//csc 155
		semsOffered.put(30, (byte)110);		//csc 112.1
		semsOffered.put(62, (byte)100);		//csc 157
		semsOffered.put(63, (byte)100);		//csc 188
		semsOffered.put(68, (byte)111);		//csc 199
		
		semsOffered.put(64, (byte)10);		//csc 109
		semsOffered.put(65, (byte)10);		//csc 113
		semsOffered.put(42, (byte)110);		//csc 171
		semsOffered.put(66, (byte)10);		//csc 193
		semsOffered.put(17, (byte)111);		//hum 1
		semsOffered.put(60, (byte)111);		//bus e 66
		semsOffered.put(100, (byte)110);	//SEN
			
		*/
	}
	
	
	public boolean isOffered(int subjectID, int term)
	{
		int nterm =0;		
	
		switch(term){
		case 1:
			nterm = 2;
			break;
		case 2:
			nterm = 1;
			break;
		case 3:
			nterm = 0;
			break;
		}
		
		return ((semsOffered.get(subjectID) >> nterm) & 1) == 1;	    
	}
	
	public void clear(){
		semsOffered.clear();
	}
	
	public void setSemsOffered(Map<Integer, Byte> semsOffered){
		this.semsOffered = semsOffered;
	}
	
	public void setSubOffering(int code, byte offr){
		semsOffered.put(code, offr);
	}
	
	public String toString(){
		return semsOffered.toString();
	}
	
}