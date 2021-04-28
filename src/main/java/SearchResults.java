import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class SearchResults {

	/**
	 * the results of the search
	 */
	private final TreeMap<String, List<InvertedIndex.Result>> results;

	/**
	 * the inverted index the results are coming from
	 */
	private final InvertedIndex index;

	/**
	 * the constructor for this class
	 * 
	 * @param myInvertedIndex the index to get the results from
	 */
	public SearchResults(InvertedIndex myInvertedIndex) {
		results = new TreeMap<String, List<InvertedIndex.Result>>();
		index = myInvertedIndex;
	}

	/**
	 * searches the index given a query
	 * 
	 * @param queryPath the file of queries
	 * @param exact     flag tells us what type of search
	 * @throws IOException throws if we can't read the query file
	 */
	public void search(Path queryPath, boolean exact) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
			if (exact) {
				for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
					TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
					if (!parsed.isEmpty()) {
						results.putIfAbsent(String.join(" ", parsed), index.exactSearch(parsed));
					}
				}
			} else {
				for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
					TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
					if (!parsed.isEmpty()) {
						results.putIfAbsent(String.join(" ", parsed), index.partialSearch(parsed));
					}
				}
			}
		}
	}
	
	/*
	 * TODO Pull logic inside the while loop into its own method, then call
	 * that method inside the while loop above instead.
	 * 
	 * 	
	public void search(Path queryPath, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
			for(...) {
				search(line, exact);
			}
		}
	}
	
	public void search(String queryLine, boolean exact) {
		stem
		join
		search
		store
	}
	
	...now you have 2 methods for almost the same amount of code, but this class
	can now be used on an entire file or individual lines. The second one could be
	useful for projects 3 and 4.
	 */

	/**
	 * gets an unmodifiable key set
	 * 
	 * @return an unmodifiable key set
	 */
	public Set<String> getResultKeySet() {
		return Collections.unmodifiableSet(results.keySet());
	}

	// TODO Remove rather than fix
	/**
	 * gets the location that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the location
	 */
	public String getLocation(String query, int index) {
		return this.results.get(query).get(index).getLocation();
	}

	// TODO Remove
	/**
	 * gets the count that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the count
	 */
	public int getCount(String query, int index) {
		return this.results.get(query).get(index).getCount();
	}

	// TODO Remove 
	/**
	 * gets the score that way we don't need to involve Result class
	 * 
	 * @param query the query at where we want to find
	 * @param index the index at where we want to find
	 * @return the score
	 */
	public Double getScore(String query, int index) {
		return this.results.get(query).get(index).getScore();
	}

	// TODO Remove
	/**
	 * tests if the results contains an index
	 * 
	 * @param query the key to test
	 * @param index the index to see if exists
	 * @return boolean whether or not it contains the index
	 */
	public boolean containsIndex(String query, int index) {
		if (this.results.get(query).size() - 1 >= index) {
			return true;
		} else {
			return false;
		}
	}

	// TODO Remove or fix the null pointer if query is not contained in map
	/**
	 * the size at a specific query
	 * 
	 * @param query the specific query
	 * @return the size in integer form
	 */
	public int size(String query) {
		return this.results.get(query).size();
	}

	/**
	 * writes the results
	 * 
	 * @param output the file we are writing to
	 * @throws IOException throws if the file is unreachable
	 */
	public void write(Path output) throws IOException {
		SimpleJsonWriter.asSearchResult(this, output);
	}
}
