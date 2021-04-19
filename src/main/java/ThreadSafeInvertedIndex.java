import java.util.Collection;

public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;
	
	
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	@Override
	public Collection<String> getWords() {
		synchronized(lock.readLock()){
			return super.getWords();
		}
	}
	
	@Override
	public void add(String outerKey, String innerKey, Integer value) {
		synchronized(lock.writeLock()) {
			super.add(outerKey, innerKey, value);
		}
	}
}
