import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	static ArrayList<WordCount> spamWords = new ArrayList<WordCount>();
	static double SPAM_THRESHOLD;
	static double inputProb = 0;
	static double spamProb = 0;
	
	public static void main(String[] args)
	{
		String line = new String();
		Scanner s;
		try {
			s = new Scanner(new File("info.txt"));
			SPAM_THRESHOLD = s.nextDouble();
		}
		catch (IOException e){}
		try {
			s = new Scanner(new File("spam.txt"));
			while (s.hasNext())
			{
				line = s.next();
				if (line.contains("!") || line.contains(".")
					|| line.contains(",") || line.contains("?")
					|| line.contains(":") || line.contains(";"))
				{
					addWord(spamWords, line.substring(0,line.length()-1).toLowerCase());
					addWord(spamWords, line.substring(line.length()-1));
				}
				else
					addWord(spamWords, line.toLowerCase());
			}
		}
		catch (IOException e){}
		
		//System.out.println("Threshold: " + SPAM_THRESHOLD);
		//printCount();
		s = new Scanner(System.in);
		line = new String("");
		while (!line.equals("quit")) {
			System.out.println("Which test input file? (type 'quit' to exit)");
			line = s.next();
			if (line.equals("quit"))
			{
				System.out.println("Thank you. Have a nice day.");
				break;
			}
			if (classify(line))
			{
				//System.out.println("Input Probability: " + inputProb);
				System.out.println("I found this to be spam. Am I correct? (Yes/No)");
				if (s.next().toLowerCase().equals("yes"))
				{
					System.out.println("Thank you, sir.");
				}
				else
				{
					System.out.println("I apologize. I will attempt to learn from my mistakes.");
					SPAM_THRESHOLD += 0.05;
					PrintWriter writer;
					try {
						writer = new PrintWriter("info.txt", "UTF-8");
						writer.println("" + SPAM_THRESHOLD);
						writer.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			else
			{
				//System.out.println("Input Probability: " + inputProb);
				System.out.println("I found this to be ham. Am I correct? (Yes/No)");
				if (s.next().toLowerCase().equals("yes"))
				{
					System.out.println("Thank you, sir.");
				}
				else
				{
					System.out.println("I apologize. I will attempt to learn from my mistakes.");
					SPAM_THRESHOLD -= 0.05;
					PrintWriter writer;
					try {
						writer = new PrintWriter("info.txt", "UTF-8");
						writer.println("" + SPAM_THRESHOLD);
						writer.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void addWord(ArrayList<WordCount> list, String s)
	{
		for (WordCount w : list)
		{
			if (w.getWord().equals(s))
			{
				w.increment();
				return;
			}
		}
		
		list.add(new WordCount(s));
	}
	
	public static int totalCount(ArrayList<WordCount> list)
	{
		int count = 0;
		for (WordCount w : list)
		{
			count += w.getCount();
		}
		return count;
	}
	
	public static void printCount()
	{
		int total = totalCount(spamWords);
		for (WordCount w : spamWords)
		{
			double prob = (double)w.getCount()/total;
			System.out.println(w.getWord() + ": " + prob);
		}
	}
	
	public static boolean isPunctuation(String s)
	{
		if (s.equals("!") || s.equals("?") || s.equals("."))
			return true;
		return false;
	}
	
	public static boolean classify(String fileName)
	{
		Scanner scan;
		ArrayList<WordCount> inputWords = new ArrayList<WordCount>();
		String line;
		try {
			scan = new Scanner(new File(fileName));
			while (scan.hasNext())
			{
				line = scan.next();
				if (line.contains("!") || line.contains(".")
						|| line.contains(",") || line.contains("?")
						|| line.contains(":") || line.contains(";"))
				{
					addWord(inputWords, line.substring(0,line.length()-1).toLowerCase());
					addWord(inputWords, line.substring(line.length()-1));
				}
				else
					addWord(inputWords, line.toLowerCase());
			}
		}
		catch (IOException e) {}
		
		int total = totalCount(inputWords);
		int spamTotal = totalCount(spamWords);
		double inputFreq;
		int count = 0;
		double unknownCount = 0;
		boolean found;
		for (WordCount w : inputWords)
		{
			found = false;
			inputFreq = (double)w.getCount()/total;
			for (WordCount x : spamWords)
			{
				double spamFreq;
				if (w.getWord().equals(x.getWord()))
				{
					found = true;
					spamFreq = (double)x.getCount()/spamTotal;
					if (inputFreq >= spamFreq)
					{
						count++;
					}
					break;
				}
			}
			if (!found)
			{
				unknownCount += 0.005;
			}
		}
		
		//System.out.println("Count: " + count + "\nTotal: " + total + "\nUnknown Count: " + unknownCount);
		inputProb = ((double)count / total) + unknownCount;
		
		if (inputProb >= SPAM_THRESHOLD)
			return true;
		return false;
	}
}