import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Class name is too general and not capitalized properly. Rename!
 */

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class dataConverter {
	// TODO Better method names too...
	/**
	 * Converts the array list into our desired data structure using what we know
	 * about the indexes of specific pieces of data
	 *
	 * @param myStorage       the array list that will be converted into the more
	 *                        useful data structure
	 * @param myInvertedIndex the inverted index where out data structure is
	 */
	public static void arrayListToMap(ArrayList<String> myStorage, InvertedIndex myInvertedIndex) {
		// myMap the data structure we want, will be filled and returned

		// pathname is a is used as a place holder for the first element in the array,
		// which we know will be the path name
		String pathname = null;
		int numberSincePath = 0;

		if (!myStorage.isEmpty()) {
			pathname = myStorage.get(0);
		}
		// converts the temporary arraylist into the datastructure that I want to use, a
		// nested map
		for (int i = 1; i < myStorage.size(); i++) {
			Map<String, Collection<Integer>> valueToAdd = new TreeMap<String, Collection<Integer>>();
			Collection<Integer> collectionToAdd = new TreeSet<Integer>();

			if (myStorage.get(i).contains("/")) { // TODO Noooooo this only works on certain systems and isn't necessary!
				pathname = myStorage.get(i);
				numberSincePath = 0;
			} else {
				numberSincePath++;
				// TODO So much complexity here that is unnecessary if you had proper "data structure" methods (like add) in your InvertedIndex class 
				if (myInvertedIndex.myMap.containsKey(myStorage.get(i))) {
					if (myInvertedIndex.myMap.get(myStorage.get(i)).containsKey(pathname)) {
						myInvertedIndex.myMap.get(myStorage.get(i)).get(pathname).add(numberSincePath);
					} else {
						collectionToAdd.add(numberSincePath);
						myInvertedIndex.myMap.get(myStorage.get(i)).put(pathname, collectionToAdd);
					}
				} else {
					collectionToAdd.add(numberSincePath);
					valueToAdd.put(pathname, collectionToAdd);
					myInvertedIndex.myMap.put(myStorage.get(i), valueToAdd);
				}
			}

		}
	}

	/**
	 * takes in a mutable arralist and changes it, this way only one arraylist needs
	 * to be created
	 *
	 * @param myStorage the array list that will be converted into the more useful
	 *                  data structure
	 * @param myPath    the path that the arraylist is going to grab the data from
	 * @throws IOException it really shouldn't throw tho
	 */
	public static void createStorage(Path myPath, ArrayList<String> myStorage) throws IOException {
		if (Files.isRegularFile(myPath)) {
			/*
			 * TODO Whenever you move data from one data structure (like a list) into
			 * another data structure (like an inverted index), there is an efficiency
			 * issue caused by the extra time and space the copy operation takes.
			 *
			 * This is a classic case where reusing your general code (in this case from
			 * TextFileStemmer) is not going to be the most efficient way forward. It is
			 * for efficiency reasons that we often have to create a less-general solution.
			 *
			 * Copy over some of the parsing and stemming logic here so as soon as you have
			 * a stemmed word, you add it directly to the inverted index instead of a list.
			 * Keep TextFileStemmer around, it will be useful again soon.
			 */
			myStorage.addAll(TextFileStemmer.listStems(myPath));
			System.out.println("Falure while getting path: " + myPath.toString());

		} else {

			DirectoryNavigator.printListing(myPath, myStorage);
			System.out.println("Falure while getting directory at path: " + myPath.toString());
		}
	}

	/*
	 * TODO Create 1 method that handles dealing with a single file (path input,
	 * not already parsed words) and another method that handles a directory ---
	 * that second method should call the one that handles a single file. Better
	 * for general functionality (more ways for other developers to use this
	 * class) but still promotes code reuse.
	 */
	
}
