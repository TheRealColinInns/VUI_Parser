import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	private final TreeMap<String, TreeMap<String, Collection<Integer>>> myMap;

	/**
	 * Constructor for inverted index
	 */
	public InvertedIndex() {
		myMap = new TreeMap<String, TreeMap<String, Collection<Integer>>>();
	}

	/**
	 * Getter for the inverted index
	 * 
	 * @return my inverted index for a specific instance
	 * 
	 */
	public Collection<String> getInvertedIndex() {
		return Collections.unmodifiableCollection(this.myMap.keySet());
	}

	/**
	 * Getter for the nested map inside the inverted index
	 * 
	 * @param key word
	 * @return the nested map inside the inverted index for a specified key
	 * 
	 */
	public Collection<String> getNestedMap(String key) {
		return Collections.unmodifiableCollection(this.myMap.get(key).keySet());
	}

	/**
	 * Getter for the nested array inside the inverted index
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return the nested array inside the inverted index
	 * @throws NullPointerException if inner or outer key dont exist
	 */
	public Collection<Integer> getNestedArray(String outerKey, String innerKey) {
		if (this.containsKeyNestedMap(outerKey, innerKey)) {
			return Collections.unmodifiableCollection(this.myMap.get(outerKey).get(innerKey));
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
	public boolean containsKeyInvertedIndex(String key) {
		return this.myMap.containsKey(key);
	}

	/**
	 * Contains method for the nested map
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsKeyNestedMap(String outerKey, String innerKey) {
		if (this.containsKeyInvertedIndex(outerKey)) {
			return this.myMap.get(outerKey).containsKey(innerKey);
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
	public boolean containsIntNestedArray(String outerKey, String innerKey, Integer value) {
		return this.myMap.get(outerKey).get(innerKey).contains(value);
	}

	/**
	 * add method for the nested array
	 * 
	 * @param outerKey word
	 * @param innerKey location
	 * @param value    position
	 */
	public void add(String outerKey, String innerKey, Integer value) {
		if (this.containsKeyInvertedIndex(outerKey)) {
			if (this.containsKeyNestedMap(outerKey, innerKey)) {
				this.myMap.get(outerKey).get(innerKey).add(value);
			} else {
				Collection<Integer> nestedArrayList = new ArrayList<Integer>();
				nestedArrayList.add(value);
				this.myMap.get(outerKey).put(innerKey, nestedArrayList);
			}
		} else {
			Collection<Integer> nestedArrayList = new ArrayList<Integer>();
			TreeMap<String, Collection<Integer>> nestedMap = new TreeMap<String, Collection<Integer>>();
			nestedArrayList.add(value);
			nestedMap.put(innerKey, nestedArrayList);
			this.myMap.put(outerKey, nestedMap);
		}
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @return int the size of the map
	 */
	public int sizeInvertedIndex() {
		return this.myMap.size();
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param key word
	 * @return int the size of the map
	 */
	public int sizeNestedMap(String key) {
		if (this.containsKeyInvertedIndex(key)) {
			return this.myMap.get(key).size();
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
	public int sizeNestedArray(String outerKey, String innerKey) {
		if (this.containsKeyNestedMap(outerKey, innerKey)) {
			return this.myMap.get(outerKey).get(innerKey).size();
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return myMap.toString();
	}

	/**
	 * writes our inverted index into a specified file
	 *
	 * @param filename this is the file name that we are going to write the inverted
	 *                 index to
	 * @throws IOException Catch this is driver
	 */
	public void dataWriter(Path filename) throws IOException {
		SimpleJsonWriter.asNestedArray(this.myMap, filename);
	}
	// not exactly sure what the addAll function would be for? I think I will leave
	// it out for now and add it when I need it and know exactly what I want it to
	// do.
}
