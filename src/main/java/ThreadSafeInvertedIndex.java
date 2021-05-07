import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// TODO https://github.com/usf-cs212-spring2021/lectures/blob/main/MultithreadingSynchronization/src/main/java/ConcurrentSet.java
// TODO Use a single lock object instead

/**
 * creates a thread safe inverted index
 * 
 * @author colininns
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The indexLock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock indexLock;

	/** The indexLock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock wordCountLock;

	/**
	 * constructor for thread safe index
	 */
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

	// TODO Override exactSearch and parialSearch instead 
	@Override
	public List<Result> search(Set<String> queries, boolean exact) {
		synchronized (indexLock.readLock()) {
			return super.search(queries, exact);
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

}
