import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ThreadSafeSearchResults extends SearchResults {

	/**
	 * the lock we will use to make it thread safe
	 */
	ReadWriteLock lock = new ReadWriteLock();

	/**
	 * constructor for thread safe search results
	 * 
	 * @param myInvertedIndex the index we will get the results from
	 */
	public ThreadSafeSearchResults(InvertedIndex myInvertedIndex) {
		super(myInvertedIndex);
	}

	@Override
	public Set<String> getResultKeySet() {
		synchronized (lock.readLock()) {
			return super.getResultKeySet();
		}
	}

	@Override
	public int size(String query) {
		synchronized (lock.readLock()) {
			return super.size(query);
		}
	}

	@Override
	public void write(Path output) throws IOException {
		synchronized (lock.readLock()) {
			super.write(output);
		}
	}

	@Override
	public void search(String queryLine, boolean exact) {
		synchronized (lock.writeLock()) {
			super.search(queryLine, exact);
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
	public void search(Path queryPath, boolean exact, WorkQueue workqueue) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8);) {
			for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
				workqueue.execute(new Task(line, exact, this));
			}
		}
		workqueue.finish();
	}

	/**
	 * Task class creates the tasks that will be paralleled
	 * 
	 * @author colininns
	 *
	 */
	public static class Task implements Runnable {

		/** the text of the query */
		String line;

		/** which test to run */
		boolean exact;

		/** to access the method */
		SearchResults results;

		/**
		 * constructor for task
		 * 
		 * @param line    the string line we are testing
		 * @param exact   tells us what type of search
		 * @param results the results needed to run a specific method
		 */
		public Task(String line, boolean exact, SearchResults results) {
			this.line = line;
			this.exact = exact;
			this.results = results;
		}

		@Override
		public void run() {
			results.search(line, exact);
		}
	}

}
