import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class responsible for storing the data structure See the README for details.
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class InvertedIndex {
	// TODO Upcasting is almost always the right approach, but you'll want a TreeMap reference for project 2.
	/**
	 * this is our data structure
	 */
	private Map<String, Map<String, Collection<Integer>>> myMap; // TODO Final

	/**
	 * Constructor for inverted index
	 */
	public InvertedIndex() {
		myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
	}

	/*
	 * TODO You cannot return nested data safely, ever. Both of these get methods
	 * below break encapsulation!
	 */
	
	// TODO Make this get method return an unmodifiable view of the outer keyset
	/**
	 * Getter for the inverted index
	 * 
	 * @return my inverted index for a specific instance
	 * 
	 */
	public Map<String, Map<String, Collection<Integer>>> getInvertedIndex() {
		return Collections.unmodifiableMap(this.myMap);
	}

	// TODO Make this get method return an unmodifiable view of the inner keyset
	/**
	 * Getter for the nested map inside the inverted index
	 * 
	 * @param key the specified key
	 * @return the nested map inside the inverted index for a specified key
	 * 
	 */
	public Map<String, Collection<Integer>> getNestedMap(String key) {
		return Collections.unmodifiableMap(this.myMap.get(key));
	}
	
	/*
	 * TODO Try to give your parameters meanings describing what they store.
	 * key --> word
	 * outerKey --> word, innerKey --> location
	 * etc.
	 */

	/**
	 * Getter for the nested array inside the inverted index
	 * 
	 * @param outerKey the specified key for the outer nest
	 * @param innerKey the specified key for the inner nest
	 * @return the nested array inside the inverted index
	 * 
	 */
	public Collection<Integer> getNestedArray(String outerKey, String innerKey) {
		// TODO What if outerKey or innerKey aren't in your data? Then this throws a null pointer exception.
		// TODO Use your contains method to test when you should return Collections.emptySet instead to avoid this problem.
		return Collections.unmodifiableCollection(this.myMap.get(outerKey).get(innerKey));
	}

	/**
	 * Contains method for the entire inverted index
	 * 
	 * @param key the specified key
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsKeyInvertedIndex(String key) {
		return this.myMap.containsKey(key);
	}

	/**
	 * Contains method for the nested map
	 * 
	 * @param outerKey the specified key to get to the nested structure
	 * @param innerKey the specific key to test
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsKeyNestedMap(String outerKey, String innerKey) {
		// TODO Null pointer if get(outerKey) is null, but should return false in that case
		return this.myMap.get(outerKey).containsKey(innerKey);
	}
	
	// TODO Fix all the null pointer issues. Test your methods on an empty index!

	/**
	 * contains method for the nested array
	 * 
	 * @param outerKey the specified key to get to the nested structure
	 * @param innerKey the specific key to get to the nested structure
	 * @param value    the specific Integer value to test
	 * @return {@code true} if the inverted index has a specified key
	 * 
	 */
	public boolean containsIntNestedArray(String outerKey, String innerKey, Integer value) {
		return this.myMap.get(outerKey).get(innerKey).contains(value);
	}

	/* 
	 * TODO Remove these unsafe add methods, that replace data without checking if something already exists.
	 * That is not safe to have as public methods. Make ONE add(String word, location, position) method
	 * that does all the initialization safely.
	 */
	/**
	 * add method for the entire inverted index
	 * 
	 * @param key   the specified key
	 * @param value the specified value
	 * 
	 */
	public void addInvertedIndex(String key, Map<String, Collection<Integer>> value) {
		this.myMap.put(key, value);
	}

	/**
	 * add method for the nested map
	 * 
	 * @param outerKey the key to get the nested map
	 * @param innerKey the specified key
	 * @param value    the specified value
	 */
	public void addNestedMap(String outerKey, String innerKey, Collection<Integer> value) {
		this.myMap.get(outerKey).put(innerKey, value);
	}

	/**
	 * add method for the nested array
	 * 
	 * @param outerKey the key to get the nested map
	 * @param innerKey the key to get the nested array
	 * @param value    the specified value
	 */
	public void addNestedArray(String outerKey, String innerKey, Integer value) {
		this.myMap.get(outerKey).get(innerKey).add(value);
	}

	// TODO Same comments about nulls and naming things for your size methods below
	
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
	 * @param key key to acces the nested map
	 * @return int the size of the map
	 */
	public int sizeNestedMap(String key) {
		return this.myMap.get(key).size();
	}

	/**
	 * size method for the entire inverted index
	 * 
	 * @param outerKey key to access the nested map
	 * @param innerKey key to access the nested array
	 * @return int the size of the arraylist
	 */
	public int sizeNestedArray(String outerKey, String innerKey) {
		return this.myMap.get(outerKey).get(innerKey).size();
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

	
/*
 * TODO 
 * Consider also adding an addAll convenience method such that given a list of
 * words and the location/path they came from, it adds each to the inverted
 * index using the list index as the position.
 */
	
}
