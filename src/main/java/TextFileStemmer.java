//Colin Inns
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 *
 * @see TextParser
 */
public class TextFileStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param lineNum line number
	 * @return a list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer, int lineNum) {
		ArrayList<String> tempList = new ArrayList<String>();
		String[] tempList2;
		String temp = line.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+","").toLowerCase();
		tempList2 = TextParser.parse(temp);
		if(tempList2.length>0) {
			for(String i:tempList2) {
				try {
					if(Character.isLetter(i.charAt(0))) {
						tempList.add(stemmer.stem(i).toString());
						tempList.add(String.valueOf(lineNum+=1));
					}

				}
				catch(Exception E) {
					
				}
			}
		}
		return tempList;
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param lineNum line number
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #listStems(String, Stemmer, int)
	 */
	public static ArrayList<String> listStems(String line, int lineNum) {
		return listStems(line, new SnowballStemmer(DEFAULT), lineNum);
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		ArrayList<String> mylist = new ArrayList<String>();
		mylist.add(inputFile.toString());
		int lineNum = 0;
		try (BufferedReader mybr = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
			for(String line = mybr.readLine(); line !=null; line = mybr.readLine()) {
				try {
					lineNum = Integer.parseInt(mylist.get(mylist.size()-1));
				}
				catch(Exception e1) {
					
				}
				for(String i:listStems(line, lineNum)) {
					mylist.add(i);
				}
				
			}
		}
		return mylist;
		
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> myset = new TreeSet<String>();
		for(String i:listStems(line, stemmer, 0)){
			if(!myset.contains(i)) {
				myset.add(i);
			}
		}
		return myset;
	}
	

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		TreeSet<String> myset = new TreeSet<String>();
		for(String i:listStems(inputFile)){
			if(!myset.contains(i)) {
				myset.add(i);
			}
		}
		
		return myset;
	}
}
