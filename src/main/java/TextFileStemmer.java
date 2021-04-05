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

//class is not used see InvertedIndexCreator for actually stemming
/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author CS 212 Software Development - Colin Inns
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
	 * Reads an input file specifically into an array list
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		ArrayList<String> stems = new ArrayList<String>();
		stemsPath(inputFile, stems);
		return stems;
	}

	/**
	 * Reads an input file specifically into a treeset
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
		stemsPath(inputFile, stems);
		return stems;
	}

	/**
	 * Takes in an inputfile and reads it into a collection
	 *
	 * @param inputFile the input path
	 * @param stems     the mutible collection
	 * @return a sorted set of unique cleaned and stemmed words
	 * @throws IOException
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static void stemsPath(Path inputFile, Collection<String> stems) throws IOException {
		try (BufferedReader myBufferedReader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			for (String line = myBufferedReader.readLine(); line != null; line = myBufferedReader.readLine()) {
				stemLine(line, new SnowballStemmer(DEFAULT), stems);
			}
		}
	}
}
