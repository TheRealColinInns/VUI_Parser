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
 * creates a thread safe version of the search results class
 * 
 * @author colininns
 *
 */
public class ThreadSafeSearchResults implements SearchResultsInterface {
	/**
	 * the work queue
	 */
	private WorkQueue queue;

	/**
	 * the results of the search
	 */
	private final TreeMap<String, List<InvertedIndex.Result>> results;

	/**
	 * the inverted index the results are coming from
	 */
	private final InvertedIndex index;

	/**
	 * constructor for thread safe search results
	 * 
	 * @param myInvertedIndex the index we will get the results from
	 */
	public ThreadSafeSearchResults(ThreadSafeInvertedIndex myInvertedIndex, WorkQueue queue) {
		results = new TreeMap<String, List<InvertedIndex.Result>>();
		this.index = myInvertedIndex;
		this.queue = queue;
	}

	@Override
	public synchronized Set<String> getResultKeySet() {
		return Collections.unmodifiableSet(results.keySet());
	}

	@Override
	public synchronized int size(String query) {
		if (this.results.containsKey(query)) {
			return this.results.get(query).size();
		} else {
			return -1;
		}
	}

	@Override
	public synchronized void write(Path output) throws IOException {
		SimpleJsonWriter.asSearchResult(results, output);
	}

	@Override
	public synchronized void search(String queryLine, boolean exact) {
		TreeSet<String> parsed = TextFileStemmer.uniqueStems(queryLine);
		if (!parsed.isEmpty()) {
			String joined = String.join(" ", parsed);
			if (!results.containsKey(joined)) {
				results.put(joined, index.search(parsed, exact));
			}
		}
	}

	/**
	 * almost overrides the original search function but with a work queue
	 * 
	 * @param queryPath the location of the query
	 * @param exact     tells us what type of search
	 * @param workqueue the workqueue we will multithread
	 * @throws IOException in case of error with reading
	 */
	public void search(Path queryPath, boolean exact) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
			for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
				queue.execute(new Task(line, exact, this));
			}
		}
		queue.finish();
	}

	/**
	 * Task class creates the tasks that will be paralleled
	 * 
	 * @author colininns
	 *
	 */
	public class Task implements Runnable {

		/** the text of the query */
		String line;

		/** which test to run */
		boolean exact;

		/** to access the method */
		ThreadSafeSearchResults results;

		/**
		 * constructor for task
		 * 
		 * @param line    the string line we are testing
		 * @param exact   tells us what type of search
		 * @param results the results needed to run a specific method
		 */
		public Task(String line, boolean exact, ThreadSafeSearchResults results) {
			this.line = line;
			this.exact = exact;
			this.results = results;
		}

		@Override
		public void run() {
			results.search(line, exact);
			
		
		TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
		if (!parsed.isEmpty()) {
			String joined = String.join(" ", parsed);
			
			synchronized (results) {
				if (results.containsKey(joined)) {
					return;
				}
			}
			
			var local = index.search(parsed, exact)
			
			synchronized (results) {
				results.put(joined, local);
			}
		}	
		}
	}

}
