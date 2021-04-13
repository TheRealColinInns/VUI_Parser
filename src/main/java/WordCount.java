import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;

public class WordCount {
	/**
	 * this is our data structure for the word count
	 */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * Constructor for Word Count class
	 */
	public WordCount() {
		wordCount = new TreeMap<String, Integer>();
	}

	/**
	 * contains method for word count
	 * 
	 * @param location the file
	 * @return if the file exists in the word count
	 */
	public boolean contains(String location) {
		return this.wordCount.containsKey(location);
	}

	/**
	 * add method for word count
	 * 
	 * @param location  the file the count came from
	 * @param wordCount the word count associated with a file
	 */
	public void add(String location, Integer wordCount) {
		if (wordCount > 0) {
			if (this.wordCount.putIfAbsent(location, wordCount) != null) {
				System.out.println("Error, location already has a word count");
			}
		}
	}

	/**
	 * get method for word count
	 * 
	 * @param location the file we want to know the word count of
	 * @return the word count at that location
	 */
	public Integer get(String location) {
		if (this.contains(location)) {
			return this.get(location);
		} else {
			return -1;
		}
	}
	
	/**
	 * writes the word counts to a specified file
	 * @param countPath the file we are writing to
	 * @throws IOException throws if file doesn't exist
	 */
	public void write(Path countPath) throws IOException {
		SimpleJsonWriter.asObject(wordCount, countPath);
	}

	@Override
	public String toString() {
		return this.wordCount.toString();
	}
}
