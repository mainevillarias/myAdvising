package advising;

public class EquivalenceTable {
	
	class Entry{
		
		public Subject equiv;
		public Subject sub;
		
		public Entry(Subject e, Subject s){
			equiv = e;
			sub = s;
		}
	}
	
	Entry[] table;
	int count;
	
	public EquivalenceTable(){
		table = new Entry[100];
		count = 0;
	}

}
