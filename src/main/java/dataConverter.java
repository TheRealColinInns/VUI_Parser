import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class dataConverter {
	/**
	 * Converts the array list into our desired data structure using what we know
	 * about the indexes of specific pieces of data
	 *
	 * @param myStorage the array list that will be converted into the more useful
	 *                  data structure
	 * @return a nested map in the correct data structure for the json file
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

			if (myStorage.get(i).contains("/")) {
				pathname = myStorage.get(i);
				numberSincePath = 0;
			} else {
				numberSincePath++;
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
	 * @return a nested map in the correct data structure for the json file
	 */
	public static void createStorage(Path myPath, ArrayList<String> myStorage) throws IOException {
		if (Files.isRegularFile(myPath)) {
			myStorage.addAll(TextFileStemmer.listStems(myPath));
			System.out.println("Falure while getting path: " + myPath.toString());

		} else {

			DirectoryNavigator.printListing(myPath, myStorage);
			System.out.println("Falure while getting directory at path: " + myPath.toString());
		}
	}

}
