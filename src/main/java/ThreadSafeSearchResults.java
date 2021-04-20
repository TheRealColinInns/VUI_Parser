import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


public class ThreadSafeSearchResults extends SearchResults {
	ReadWriteLock lock = new ReadWriteLock();
	
	@Override
	public Set<String> getResultKeySet() {
		synchronized(lock.readLock()) {
			return super.getResultKeySet();
		}
	}
	
	@Override
	public String getLocation(String query, int index) {
		synchronized(lock.readLock()) {
			return super.getLocation(query, index);
		}
	}
	
	@Override
	public int getCount(String query, int index) {
		synchronized(lock.readLock()) {
			return super.getCount(query, index);
		}
	}
	
	@Override
	public Double getScore(String query, int index) {
		synchronized(lock.readLock()) {
			return super.getScore(query, index);
		}
	}
	
	@Override
	public boolean containsIndex(String query, int index) {
		synchronized(lock.readLock()) {
			return super.containsIndex(query, index);
		}
	}
	
	@Override
	public int size(String query) {
		synchronized(lock.readLock()) {
			return super.size(query);
		}
	}
	
	@Override
	public void write(Path output) throws IOException {
		synchronized(lock.readLock()) {
			super.write(output);
		}
	}
	
	@Override
	public void add(String query, List<Result> result) {
		synchronized(lock.writeLock()) {
			super.add(query, result);
		}
	}
	
	@Override
	public boolean add(String query, Result result) {
		synchronized(lock.writeLock()) {
			return super.add(query, result);
		}
	}
	


}
