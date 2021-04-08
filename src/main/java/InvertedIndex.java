import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Class responsible for storing the data structure See the README for details.
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class InvertedIndex {

	/**
	 * this is our data structure
	 */
	private final TreeMap<String, TreeMap<String, Collection<Integer>>> index;

	/**
	 * Constructor for inverted index
	 */
	public InvertedIndex() {
		index = new TreeMap<String, TreeMap<String, Collection<Integer>>>();
	}

	/**
	 * Getter for the inverted index
	 * 
	 * @return my inverted index for a specific instance
	 * 
	 */
	public Collection<String> getWords() {
		return Collections.unmodifiableCollection(this.index.keySet());
	}

	/**
	 * Getter for the nested map inside the inverted index
	 * 
	 * @param key word
	 * @return the nested map inside the inverted index for a specified key, null if
	 *         it doesn't exist
	 * 
	 */
	public Collection<String> getLocations(String key) {
		if (this.containsWord(key)) {
			return Collections.unmodifiableCollection(this.index.get(key).keySet());
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Getter for the nested array inside the inverted index
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return the nested array inside the inverted index
	 * @throws NullPointerException if inner or outer key dont exist
	 */
	public Collection<Integer> getPositions(String outerKey, String innerKey) {
		if (this.containsLocation(outerKey, innerKey)) {
			return Collections.unmodifiableCollection(this.index.get(outerKey).get(innerKey));
		}
		return Collections.emptySet();
	}

	/**
	 * Contains method for the entire inverted index
	 * 
	 * @param key word
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsWord(String key) {
		return this.index.containsKey(key);
	}

	/**
	 * Contains method for the nested map
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsLocation(String outerKey, String innerKey) {
		if (this.containsWord(outerKey)) {
			return this.index.get(outerKey).containsKey(innerKey);
		} else {
			return false;
		}
	}

	/**
	 * contains method for the nested array
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @param value    position
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsPosition(String outerKey, String innerKey, Integer value) {
		if (this.containsLocation(outerKey, innerKey)) {
			return this.index.get(outerKey).get(innerKey).contains(value);
		} else {
			return false;
		}
	}

	/**
	 * add method for the nested array
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @param value    position
	 */
	public void add(String outerKey, String innerKey, Integer value) {
		if (this.containsWord(outerKey)) {
			if (this.containsLocation(outerKey, innerKey)) {
				this.index.get(outerKey).get(innerKey).add(value);
			} else {
				Collection<Integer> nestedArrayList = new ArrayList<Integer>();
				nestedArrayList.add(value);
				this.index.get(outerKey).put(innerKey, nestedArrayList);
			}
		} else {
			Collection<Integer> nestedArrayList = new ArrayList<Integer>();
			TreeMap<String, Collection<Integer>> nestedMap = new TreeMap<String, Collection<Integer>>();
			nestedArrayList.add(value);
			nestedMap.put(innerKey, nestedArrayList);
			this.index.put(outerKey, nestedMap);
		}
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @return int the size of the map
	 */
	public int sizeWords() {
		return this.index.size();
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param key word
	 * @return int the size of the map
	 */
	public int sizeLocations(String key) {
		if (this.containsWord(key)) {
			return this.index.get(key).size();
		} else {
			return -1;
		}
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return int the size of the arraylist
	 */
	public int sizePositions(String outerKey, String innerKey) {
		if (this.containsLocation(outerKey, innerKey)) {
			return this.index.get(outerKey).get(innerKey).size();
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * writes our inverted index into a specified file
	 *
	 * @param filename this is the file name that we are going to write the inverted
	 *                 index to
	 * @throws IOException Catch this is driver
	 */
	public void dataWriter(Path filename) throws IOException {
		SimpleJsonWriter.asNestedArray(this.index, filename);
	}

	/**
	 * 
	 * @param words    the words to input
	 * @param location the location the words were found
	 */
	public void addAll(List<String> words, String location) {
		int position = 0;
		for (String word : words) {
			position++;
			this.add(word, location, position);
		}
	}
}
