import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for storing the data structure See the README for details.
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class InvertedIndex {

	/**
	 * this is our data structure
	 */
	private final TreeMap<String, TreeMap<String, Collection<Integer>>> index;

	/**
	 * this is our wordCount map
	 */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * Constructor for inverted index
	 */
	public InvertedIndex() {
		index = new TreeMap<String, TreeMap<String, Collection<Integer>>>();
		wordCount = new TreeMap<String, Integer>();
	}

	/*
	 * +--------------------------------------------------------------------------+
	 * Methods for index:
	 */

	/**
	 * Getter for the inverted index
	 * 
	 * @return my inverted index for a specific instance
	 * 
	 */
	public Collection<String> getWords() {
		return Collections.unmodifiableCollection(this.index.keySet());
	}

	/**
	 * Getter for the nested map inside the inverted index
	 * 
	 * @param key word
	 * @return the nested map inside the inverted index for a specified key, null if
	 *         it doesn't exist
	 * 
	 */
	public Collection<String> getLocations(String key) {
		if (this.containsWord(key)) {
			return Collections.unmodifiableCollection(this.index.get(key).keySet());
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Getter for the nested array inside the inverted index
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return the nested array inside the inverted index
	 * @throws NullPointerException if inner or outer key dont exist
	 */
	public Collection<Integer> getPositions(String outerKey, String innerKey) {
		if (this.containsLocation(outerKey, innerKey)) {
			return Collections.unmodifiableCollection(this.index.get(outerKey).get(innerKey));
		}
		return Collections.emptySet();
	}

	/**
	 * Contains method for the entire inverted index
	 * 
	 * @param key word
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsWord(String key) {
		return this.index.containsKey(key);
	}

	/**
	 * Contains method for the nested map
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsLocation(String outerKey, String innerKey) {
		if (this.containsWord(outerKey)) {
			return this.index.get(outerKey).containsKey(innerKey);
		} else {
			return false;
		}
	}

	/**
	 * contains method for the nested array
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @param value    position
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsPosition(String outerKey, String innerKey, Integer value) {
		if (this.containsLocation(outerKey, innerKey)) {
			return this.index.get(outerKey).get(innerKey).contains(value);
		} else {
			return false;
		}
	}

	/**
	 * add method for the nested array
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @param value    position
	 */
	public void add(String outerKey, String innerKey, Integer value) {
		this.addToWordCount(innerKey);
		if (this.index.putIfAbsent(outerKey, new TreeMap<String, Collection<Integer>>(
				Map.of(innerKey, new ArrayList<Integer>(Arrays.asList(value))))) != null) {
			if (this.index.get(outerKey).putIfAbsent(innerKey, new ArrayList<Integer>(Arrays.asList(value))) != null) {
				this.index.get(outerKey).get(innerKey).add(value);
			}
		}
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @return int the size of the map
	 */
	public int sizeWords() {
		return this.index.size();
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param key word
	 * @return int the size of the map
	 */
	public int sizeLocations(String key) {
		if (this.containsWord(key)) {
			return this.index.get(key).size();
		} else {
			return -1;
		}
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return int the size of the arraylist
	 */
	public int sizePositions(String outerKey, String innerKey) {
		if (this.containsLocation(outerKey, innerKey)) {
			return this.index.get(outerKey).get(innerKey).size();
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * writes our inverted index into a specified file
	 *
	 * @param filename this is the file name that we are going to write the inverted
	 *                 index to
	 * @throws IOException Catch this is driver
	 */
	public void indexWriter(Path filename) throws IOException {
		SimpleJsonWriter.asNestedArray(this.index, filename);
	}

	/**
	 * adds an entire list into the index
	 * 
	 * @param words    the words to input
	 * @param location the location the words were found
	 */
	public void addAll(List<String> words, String location) {
		int position = 0;
		for (String word : words) {
			position++;
			this.add(word, location, position);
		}
	}

	/**
	 * helper function that searches for a piece of a query in a word
	 * 
	 * @param wordKey the word we are searching through
	 * @param query   the query we are looking for
	 * @return boolean whether or not it is there
	 */
	private static boolean partialSearcher(String wordKey, String query) {
		String compareWord;
		if (wordKey.equals(query)) {
			return true;
		}
		if (query.length() < wordKey.length()) {
			compareWord = wordKey.substring(0, query.length());
		} else {
			return false;
		}
		if (compareWord.equals(query)) {
			return true;
		}
		return false;
	}

	/**
	 * does an exact search of a query
	 * 
	 * @param queries   the queries we will search for
	 * @param result    the results we will add our findings to
	 * @param queryText the text form of the query
	 */
	public void exactSearch(Set<String> queries, SearchResults results, String queryText) {
		/*
		 * System.out.println("Current Queries:"); for(String temp:queries) {
		 * System.out.print(temp+" "); } System.out.print(",\n");
		 */
		Map<String, Integer> countsAtLocations = new HashMap<String, Integer>();
		for (String word : this.getWords()) {
			for (String path : this.getLocations(word)) {
				for (String query : queries) {
					if (word.compareToIgnoreCase(query) == 0) {
						if (countsAtLocations.containsKey(path)) {
							countsAtLocations.put(path, this.sizePositions(word, path) + countsAtLocations.get(path));
						} else {
							countsAtLocations.put(path, this.sizePositions(word, path));
						}
					}
				}
			}
		}
		// System.out.println("+----------------------------------------+");
		// System.out.println("Query: " + queryText + ", Counts: " +
		// countsAtLocations.toString());
		if (countsAtLocations.isEmpty()) {
			results.addBlank(queryText);
		} else {
			for (String path : countsAtLocations.keySet()) {
				results.add(queryText, path, countsAtLocations.get(path),
						countsAtLocations.get(path) / Double.valueOf(this.getWordCount(path)));
			}
		}

	}

	/**
	 * performs a partial search for a specified query
	 * 
	 * @param queries   the queries we will search for
	 * @param result    the results we will add our findings to
	 * @param queryText the text form of the query
	 */
	public void partialSearch(Set<String> queries, SearchResults results, String queryText) {
		Map<String, Integer> countsAtLocations = new HashMap<String, Integer>();
		for (String word : this.getWords()) {
			for (String path : this.getLocations(word)) {
				for (String query : queries) {

					if (partialSearcher(word, query)) {
						if (countsAtLocations.containsKey(path)) {
							countsAtLocations.put(path, this.sizePositions(word, path) + countsAtLocations.get(path));
						} else {
							countsAtLocations.put(path, this.sizePositions(word, path));
						}
					}
				}
			}
		}

		for (String path : countsAtLocations.keySet()) {
			results.add(queryText, path, countsAtLocations.get(path),
					(countsAtLocations.get(path) / Double.valueOf(this.getWordCount(path))));
		}
	}

	/*
	 * +--------------------------------------------------------------------------+
	 * These are the methods for the Word Count:
	 */

	/**
	 * contains method for word count
	 * 
	 * @param location the file
	 * @return if the file exists in the word count
	 */
	public boolean containsWordCount(String location) {
		return this.wordCount.containsKey(location);
	}

	/**
	 * add method for word count
	 * 
	 * @param location  the file the count came from
	 * @param wordCount the word count associated with a file
	 */
	private void addToWordCount(String location) {
		if (this.wordCount.putIfAbsent(location, 1) != null) {
			this.wordCount.put(location, this.getWordCount(location) + 1);
		}
	}

	/**
	 * get method for word count
	 * 
	 * @param location the file we want to know the word count of
	 * @return the word count at that location
	 */
	public Integer getWordCount(String location) {
		if (this.containsWordCount(location)) {
			return this.wordCount.get(location);
		} else {
			return null;
		}
	}

	/**
	 * writes the word counts to a specified file
	 * 
	 * @param countPath the file we are writing to
	 * @throws IOException throws if file doesn't exist
	 */
	public void writeWordCount(Path countPath) throws IOException {
		SimpleJsonWriter.asObject(wordCount, countPath);
	}

	/*
	 * +--------------------------------------------------------------------------+
	 * These are methods for the query:
	 */

	/**
	 * parses all of the queries at a location
	 * 
	 * @param fileName the file we are reading the query from
	 * @param exact    boolean whether or not to search exact or partial
	 * @throws IOException exception thrown if file doesn't exist
	 */
	public void parse(Path fileName, SearchResults results, boolean exact) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(fileName, StandardCharsets.UTF_8);) {
			if (exact) {
				for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
					TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
					if (!parsed.isEmpty()) {
						this.exactSearch(parsed, results, String.join(" ", parsed));
					}
				}
			} else {
				for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
					TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
					if (!parsed.isEmpty()) {
						this.partialSearch(parsed, results, String.join(" ", parsed));
					}
				}
			}
		}
	}

}
