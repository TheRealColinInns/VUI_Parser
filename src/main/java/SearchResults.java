import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;

public class SearchResults {
	/**
	 * the results of the search
	 */
	private final TreeMap<String, SingleQueryResult> results;

	/**
	 * the constructor for this class
	 */
	public SearchResults() {
		results = new TreeMap<String, SingleQueryResult>();
	}

	/**
	 * adds a single query result to the results
	 * 
	 * @param query       the location we are storing it at
	 * @param queryResult the value we are storing
	 */
	public void add(String query, SingleQueryResult queryResult) {
		results.putIfAbsent(query, queryResult);
	}

	/**
	 * writes the results
	 * 
	 * @param output the file we are writing to
	 * @throws IOException throws if the file is unreachable
	 */
	public void write(Path output) throws IOException {
		SimpleJsonWriter.asSearchResult(this.results, output);
	}

}
