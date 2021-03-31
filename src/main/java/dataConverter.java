import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for converting an array list into the map data structure desired
 * that will be very important later for creating the json file
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class dataConverter {
	/**
	 * Converts the array list into our desired data structure using what we know about the indexes of specific pieces of data
	 *
	 * @param myStorage the array list that will be converted into the more useful data structure
	 * @return a nested map in the correct data structure for the json file
	 */
	public static Map<String, Map<String, Collection<Integer>>> arrayListToMap(ArrayList<String> myStorage) {
		//myMap the data structure we want, will be filled and returned
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		//pathname is a is used as a place holder for the first element in the array, which we know will be the path name
		String pathname = null;
		
		if(!myStorage.isEmpty()) {
			pathname = myStorage.get(0);
		}
		//converts the temporary arraylist into the datastructure that I want to use, a nested map
		for(int i = 1; i<myStorage.size(); i++) {
			Map<String, Collection<Integer>> valueToAdd = new TreeMap<String, Collection<Integer>>();
			Collection<Integer> columnToAdd = new TreeSet<Integer>();
			if(myStorage.get(i).contains("/")) {
				pathname = myStorage.get(i);
			}
			else {
				if(myMap.containsKey(myStorage.get(i))) {
					if(myMap.get(myStorage.get(i)).containsKey(pathname)) {
						myMap.get(myStorage.get(i)).get(pathname).add(Integer.parseInt(myStorage.get(++i)));
					}
					else {
						columnToAdd.add(Integer.parseInt(myStorage.get(i+1)));
						myMap.get(myStorage.get(i++)).put(pathname, columnToAdd);
					}
				}
				else {
					columnToAdd.add(Integer.parseInt(myStorage.get(i+1)));
					valueToAdd.put(pathname, columnToAdd);
					myMap.put(myStorage.get(i++), valueToAdd);
				}
			}
			
		}
		return myMap;
	}
}
