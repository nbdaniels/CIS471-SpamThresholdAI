public class WordCount {
		
		private String word;
		private int count;
		
		public WordCount(String s)
		{
			word = s;
			count = 1;
		}
		
		public void increment()
		{
			count++;
		}
		
		public String getWord()
		{
			return word;
		}
		
		public int getCount()
		{
			return count;
		}
	}