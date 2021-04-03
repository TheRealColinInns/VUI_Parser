import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO Only stemLine needs to pass in a collection. The other listStems and uniqueStems
 * methods should not---changes those back to the original method declarations. 
 * 
 * If you aren't quite clear on what the original issues were and how to fix them, please
 * stop by office hours or post on CampusWire!
 */

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
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param stems   the collection
	 * @return a list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 */
	public static Collection<String> listStems(String line, Stemmer stemmer, Collection<String> stems) {
		stemLine(line, stemmer, stems);
		return stems;

	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param stems   the mutable collection we will edit
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		for (String word : TextParser.parse(line)) {
			stems.add(stemmer.stem(word).toString());
		}
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line  the line of words to clean, split, and stem
	 * @param stems the collection
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #listStems(String, Stemmer, Collection)
	 */
	public static Collection<String> listStems(String line, Collection<String> stems) {
		return listStems(line, new SnowballStemmer(DEFAULT), stems);
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
		// you suggested to create another stemmer here however I don't see the purpose,
		// wouldn't that be inneficient
		ArrayList<String> stems = new ArrayList<String>();
		stems.add(inputFile.toString());
		try (BufferedReader myBufferedReader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
				listStems(line, stems); // TODO Call stemLine instead
			}
		}
		return stems;

	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
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
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> stems = new TreeSet<String>();
		stems.addAll(listStems(line, stemmer, stems)); // TODO Same efficiency issue we already discussed?
		return stems;
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
		TreeSet<String> stems = new TreeSet<String>();
		stems.addAll(listStems(inputFile)); // TODO Same efficiency issue we already discussed?
		return stems;
	}
}
