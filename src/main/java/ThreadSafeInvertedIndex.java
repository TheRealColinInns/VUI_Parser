import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The indexLock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock indexLock;

	/** The indexLock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock wordCountLock;

	public ThreadSafeInvertedIndex() {
		super();
		indexLock = new ReadWriteLock();
		wordCountLock = new ReadWriteLock();
	}

	/*
	 * Methods for index
	 */

	@Override
	public Collection<String> getWords() {
		synchronized (indexLock.readLock()) {
			return super.getWords();
		}
	}

	@Override
	public Collection<String> getLocations(String key) {
		synchronized (indexLock.readLock()) {
			return super.getLocations(key);
		}
	}

	@Override
	public Collection<Integer> getPositions(String outerKey, String innerKey) {
		synchronized (indexLock.readLock()) {
			return super.getPositions(outerKey, innerKey);
		}
	}

	@Override
	public boolean containsWord(String key) {
		synchronized (indexLock.readLock()) {
			return super.containsWord(key);
		}
	}

	@Override
	public boolean containsLocation(String outerKey, String innerKey) {
		synchronized (indexLock.readLock()) {
			return super.containsLocation(outerKey, innerKey);
		}
	}

	@Override
	public boolean containsPosition(String outerKey, String innerKey, Integer value) {
		synchronized (indexLock.readLock()) {
			return super.containsPosition(outerKey, innerKey, value);
		}
	}

	@Override
	public int sizeWords() {
		synchronized (indexLock.readLock()) {
			return super.sizeWords();
		}
	}

	@Override
	public int sizeLocations(String key) {
		synchronized (indexLock.readLock()) {
			return super.sizeLocations(key);
		}
	}

	@Override
	public int sizePositions(String outerKey, String innerKey) {
		synchronized (indexLock.readLock()) {
			return super.sizePositions(outerKey, innerKey);
		}
	}

	@Override
	public String toString() {
		synchronized (indexLock.readLock()) {
			return super.toString();
		}
	}

	@Override
	public void indexWriter(Path filename) throws IOException {
		synchronized (indexLock.readLock()) {
			super.indexWriter(filename);
		}
	}

	@Override
	public void exactSearch(Set<String> queries, SearchResults results, String queryText) {
		synchronized (indexLock.readLock()) {
			super.exactSearch(queries, results, queryText);
		}
	}

	@Override
	public void partialSearch(Set<String> queries, SearchResults results, String queryText) {
		synchronized (indexLock.readLock()) {
			super.partialSearch(queries, results, queryText);
		}
	}

	@Override
	public void add(String outerKey, String innerKey, Integer value) {
		synchronized (indexLock.writeLock()) {
			super.add(outerKey, innerKey, value);
		}
	}

	@Override
	public void addAll(List<String> words, String location) {
		synchronized (indexLock.writeLock()) {
			super.addAll(words, location);
		}
	}

	/*
	 * Methods for word count
	 */

	@Override
	public boolean containsWordCount(String location) {
		synchronized (wordCountLock.readLock()) {
			return super.containsWordCount(location);
		}
	}

	@Override
	public Integer getWordCount(String location) {
		synchronized (wordCountLock.readLock()) {
			return super.getWordCount(location);
		}
	}

	@Override
	public void writeWordCount(Path countPath) throws IOException {
		synchronized (wordCountLock.readLock()) {
			super.writeWordCount(countPath);
		}
	}

	@Override
	protected void addToWordCount(String location) {
		synchronized (wordCountLock.writeLock()) {
			super.addToWordCount(location);
		}
	}

	@Override
	public void parse(Path fileName, SearchResults results, boolean exact, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		try (BufferedReader mybr = Files.newBufferedReader(fileName, StandardCharsets.UTF_8);) {
			for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
				TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
				if (!parsed.isEmpty()) {
					queue.execute(new Task(this, results, parsed, String.join(" ", parsed), exact));
				}
			}

		}
		queue.join();
	}

	/**
	 * Task class creates the tasks that will be paralleled
	 * 
	 * @author colininns
	 *
	 */
	public static class Task implements Runnable {

		/** the inverted index we are using */
		InvertedIndex index;

		/** the results the task will add to */
		SearchResults results;

		/** the query */
		TreeSet<String> parsed;

		/** the text of the query */
		String queryText;

		/** which test to run */
		boolean exact;

		/**
		 * constructor for task
		 * 
		 * @param num    the number we are testing
		 * @param primes the results
		 */
		public Task(InvertedIndex index, SearchResults results, TreeSet<String> parsed, String queryText,
				boolean exact) {
			System.out.println("Creating Task");
			this.index = index;
			this.results = results;
			this.parsed = parsed;
			this.queryText = queryText;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized (index) {
				if (exact) {

					index.exactSearch(parsed, results, queryText);

				} else {

					index.partialSearch(parsed, results, queryText);
				}
			}
			System.out.println("Finishing Task");
		}
	}
}
