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
	// TODO Need to make this more general and object-oriented
	public static ArrayList<String> listStems(String line, Stemmer stemmer, int lineNum) {
		ArrayList<String> myList = new ArrayList<String>();
		String[] helperList;
		// TODO Could just call TextParser.parse
		helperList = TextParser.parse(line.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+","").toLowerCase());
		if(helperList.length>0) { // TODO Remove
			for(String i:helperList) {
				try {
					if(Character.isLetter(i.charAt(0))) {
						myList.add(stemmer.stem(i).toString());
						myList.add(String.valueOf(lineNum+=1));
					}

				}
				catch(Exception E) { // TODO Remove the try/catch
					
				}
			}
		}
		return myList;
		
		/* TODO 
		ArrayList<String> stems = new ArrayList<String>();
		stemLine(line, stemmer, stems);
		return stems;
		*/
	}
	
	/* TODO 
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		for (String word : TextParser.parse(line)) {
			stems.add(stemmer.stem(word).toString());
		}
	}
	*/

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

	// TODO Better variable names
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
		// TODO Make general where not worried about line numbers
		ArrayList<String> mylist = new ArrayList<String>(); // TODO stems
		mylist.add(inputFile.toString());
		int lineNum = 0;
		// TODO Stemmer stemmer = ...
		try (BufferedReader mybr = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);){
			for(String line = mybr.readLine(); line !=null; line = mybr.readLine()) {
				// TODO Call stemLine(line, stemmer, mylist);
				try {
					lineNum = Integer.parseInt(mylist.get(mylist.size()-1));
				}
				catch(Exception e1) {
					
				}
				// TODO Inefficient (3 reasons)
				// TODO 1) Usually slower to add 1 at a time versus call an addAll method
				// TODO 2) Avoid copying entirely when possible
				// TODO 3) Creates so many unused objects!
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
		// TODO There is a way to reuse code without introducing an efficiency issue
		for(String i:listStems(line, stemmer, 0)){
			if(!myset.contains(i)) { // TODO Remove
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
		for(String i:listStems(inputFile)){ // TODO Similar issues
			if(!myset.contains(i)) {
				myset.add(i);
			}
		}
		
		return myset;
	}
}
