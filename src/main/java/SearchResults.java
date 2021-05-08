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

/*
 * TODO Create a common SearchResultsInterface with the common methods 
 * 
 * Implement that interface in both SearchResults and ThreadSafeSearchResults
 * but each class will have its own data and its own implementations.
 */

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

	// TODO Remove WorkQueue
	/**
	 * searches the index given a query
	 * 
	 * @param queryPath the file of queries
	 * @param exact     flag tells us what type of search
	 * @param workqueue irrelevant
	 * @throws IOException throws if we can't read the query file
	 */
	public void search(Path queryPath, boolean exact, WorkQueue workqueue) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
			for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
				this.search(line, exact);
			}
		}
	}

	/**
	 * does a search of a single query line
	 * 
	 * @param queryLine the lin ewe are searching for
	 * @param exact     {code=true} if we are doing an exact search
	 */
	public void search(String queryLine, boolean exact) {
		TreeSet<String> parsed = TextFileStemmer.uniqueStems(queryLine);
		if (!parsed.isEmpty()) {
			String joined = String.join(" ", parsed);
			if (!results.containsKey(joined)) {
				results.put(joined, index.search(parsed, exact));
			}
		}
	}

	/**
	 * gets an unmodifiable key set
	 * 
	 * @return an unmodifiable key set
	 */
	public Set<String> getResultKeySet() {
		return Collections.unmodifiableSet(results.keySet());
	}

	/**
	 * the size at a specific query
	 * 
	 * @param query the specific query
	 * @return the size in integer form
	 */
	public int size(String query) {
		if (this.results.containsKey(query)) {
			return this.results.get(query).size();
		} else {
			return -1;
		}
	}

	/**
	 * writes the results
	 * 
	 * @param output the file we are writing to
	 * @throws IOException throws if the file is unreachable
	 */
	public void write(Path output) throws IOException {
		SimpleJsonWriter.asSearchResult(results, output);
	}
}
