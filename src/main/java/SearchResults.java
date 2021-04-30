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
	private void search(String queryLine, boolean exact) {
		TreeSet<String> parsed = TextFileStemmer.uniqueStems(queryLine);
		if (!parsed.isEmpty()) {
			/*
			 * TODO What happens if there are duplicate lines in the query file?
			 *
			 * For example, suppose there is a query file with the line "hello world"
			 * repeated 100 times in the file.
			 *
			 * If you already found results "hello world" there is no need to do it
			 * again. Those results only need to be generated once. However, your code
			 * re-does the search over and over again 100 times.
			 *
			 * In other words, only search if the joined query string is NOT already in
			 * the map of results. That will require you to save the joined String as
			 * a variable so you can test it.
			 * 
			 * var joined = String.join(...)
			 * 
			 * if (joined is not a key in results...) { search }
			 */
			if (exact) {
				/*
				 * TODO This is common logic. Just like map.putIfAbsent or
				 * map.getOrDefault are convenience methods that make common code
				 * reusable, lets create an search(Set<String> queries, boolean exact)
				 * method in the inverted index that has similar logic: returns the
				 * results of the exact search or the partial search depending on the
				 * boolean exact parameter. Then, call that method here.
				 */
				results.putIfAbsent(String.join(" ", parsed), index.exactSearch(parsed));
			} else {
				results.putIfAbsent(String.join(" ", parsed), index.partialSearch(parsed));
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
		if(this.results.containsKey(query)) {
			return this.results.get(query).size();
		}
		else {
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
