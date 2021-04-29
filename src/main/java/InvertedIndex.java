
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
	 * @param word     word
	 * @param location location
	 * @param value    position
	 */
	public void add(String word, String location, Integer value) {
		this.index.putIfAbsent(word, new TreeMap<>());
		this.index.get(word).putIfAbsent(location, new TreeSet<>());
		boolean modified = this.index.get(word).get(location).add(value);
		if (modified) {
			this.addToWordCount(location);
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
	 * searches the index for exact queries
	 * 
	 * @param queries the queries we are searching for
	 * @return a list of results, in order
	 */
	public List<Result> exactSearch(Set<String> queries) {
		Map<String, Result> lookup = new HashMap<String, Result>();
		List<Result> results = new ArrayList<>();

		for (String query : queries) {
			if (this.index.containsKey(query)) {
				this.lookup(lookup, results, query);
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * searches the index for parts of queries
	 * 
	 * @param queries the queries we are searching for
	 * @return a list of results, in order
	 */
	public List<Result> partialSearch(Set<String> queries) {
		Map<String, Result> lookup = new HashMap<>();
		List<Result> results = new ArrayList<>();

		for (String query : queries) {
			for (String word : this.index.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					this.lookup(lookup, results, word);
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Helper function loops through the lookup to get the search results
	 * 
	 * @param lookup  the lookup map
	 * @param results the singular result
	 * @param word    the word we found
	 */
	private void lookup(Map<String, Result> lookup, List<Result> results, String word) {
		for (String path : this.index.get(word).keySet()) {
			if (lookup.containsKey(path)) {
				lookup.get(path).update(word);
			} else {
				int count = this.index.get(word).get(path).size();
				Result local = new Result(path, count,
						Double.valueOf(count / Double.valueOf(this.wordCount.get(path))));
				lookup.put(path, local);
				results.add(local);
			}
		}
	}

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
	 * @param location the file the count came from
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
	 * Inner class that stores a single result
	 * 
	 * @author colininns
	 *
	 */
	public class Result implements Comparable<Result> {
		/**
		 * stores where the count came from
		 */
		private final String location;
		/**
		 * the amount of hits it found
		 */
		private int count;
		/**
		 * the hits divided by the word count
		 */
		private Double score;

		/**
		 * Constructor for the result
		 * 
		 * @param location the location
		 * @param count    the amount of hits
		 * @param score    the score of the location
		 */
		public Result(String location, int count, Double score) {
			DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
			this.location = location;
			this.count = count;
			this.score = Double.valueOf(FORMATTER.format(score));
		}

		/**
		 * gets the location
		 * 
		 * @return a string of the location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * gets the count
		 * 
		 * @return an int of the count
		 */
		public int getCount() {
			return count;
		}

		/**
		 * gets the score
		 * 
		 * @return a double of the score
		 */
		public Double getScore() {
			return score;
		}

		/**
		 * updates the result with a new count and score
		 * 
		 * @param query the query
		 */
		public void update(String query) {
			this.count += index.get(query).get(location).size();
			this.score = this.count / (double) wordCount.get(location);
		}

		@Override
		public int compareTo(Result original) {
			int scoreComparison = Double.compare(original.getScore(), this.getScore());
			if (scoreComparison != 0) {
				return scoreComparison;
			} else {
				int countComparison = Integer.compare(original.getCount(), this.getCount());
				if (countComparison < 0) {
					return countComparison;
				} else {
					return this.getLocation().compareToIgnoreCase(original.getLocation());
				}
			}
		}
	}
}
