import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	@Override
	public Collection<String> getWords() {
		synchronized (lock.readLock()) {
			return super.getWords();
		}
	}

	@Override
	public Collection<String> getLocations(String key) {
		synchronized (lock.readLock()) {
			return super.getLocations(key);
		}
	}

	@Override
	public Collection<Integer> getPositions(String outerKey, String innerKey) {
		synchronized (lock.readLock()) {
			return super.getPositions(outerKey, innerKey);
		}
	}

	@Override
	public boolean containsWord(String key) {
		synchronized (lock.readLock()) {
			return super.containsWord(key);
		}
	}

	@Override
	public boolean containsLocation(String outerKey, String innerKey) {
		synchronized (lock.readLock()) {
			return super.containsLocation(outerKey, innerKey);
		}
	}

	@Override
	public boolean containsPosition(String outerKey, String innerKey, Integer value) {
		synchronized (lock.readLock()) {
			return super.containsPosition(outerKey, innerKey, value);
		}
	}

	@Override
	public int sizeWords() {
		synchronized (lock.readLock()) {
			return super.sizeWords();
		}
	}

	@Override
	public int sizeLocations(String key) {
		synchronized (lock.readLock()) {
			return super.sizeLocations(key);
		}
	}

	@Override
	public int sizePositions(String outerKey, String innerKey) {
		synchronized (lock.readLock()) {
			return super.sizePositions(outerKey, innerKey);
		}
	}

	@Override
	public String toString() {
		synchronized (lock.readLock()) {
			return super.toString();
		}
	}

	@Override
	public void indexWriter(Path filename) throws IOException {
		synchronized (lock.readLock()) {
			super.indexWriter(filename);
		}
	}

	@Override
	public void exactSearch(Set<String> queries, SearchResults results, String queryText) {
		synchronized (lock.readLock()) {
			super.exactSearch(queries, results, queryText);
		}
	}

	@Override
	public void partialSearch(Set<String> queries, SearchResults results, String queryText) {
		synchronized (lock.readLock()) {
			super.partialSearch(queries, results, queryText);
		}
	}

	@Override
	public void add(String outerKey, String innerKey, Integer value) {
		synchronized (lock.writeLock()) {
			super.add(outerKey, innerKey, value);
		}
	}

	@Override
	public void addAll(List<String> words, String location) {
		synchronized (lock.writeLock()) {
			super.addAll(words, location);
		}
	}

}
