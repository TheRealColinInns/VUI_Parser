import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
//Colin Inns
/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Path myPath = null;
		ArgumentMap myArgMapStem = new ArgumentMap();
		String filename = null;
		ArrayList<String> temp = new ArrayList<String>();
		Map<Map<String, String>, Collection<Integer>> myMap = new HashMap<Map<String, String>, Collection<Integer>>();
		Instant start = Instant.now();
		myArgMapStem.parse(args);
		if(myArgMapStem.hasFlag("-text")) {
			try {
				myPath = Path.of(myArgMapStem.getString("-text"));
				temp.addAll(TextFileStemmer.listStems(myPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(myArgMapStem.hasFlag("-index")) {
			filename = myArgMapStem.getString("-index");
		}
		//System.out.println(temp.toString());
		String pathname = temp.get(0);
		
		for(int i = 1; i<temp.size(); i++) {
			Map<String, String> myKey = new HashMap<String, String>();
			myKey.put(temp.get(i), pathname);
			if(myMap.containsKey(myKey)) {
				myMap.get(myKey).add(Integer.parseInt(temp.get(++i)));
			}
			else {
				ArrayList<Integer> tempListtoPut = new ArrayList<Integer>();
				tempListtoPut.add(Integer.parseInt(temp.get(++i)));
				myMap.put(myKey, tempListtoPut);
			}
			
		}
		/*
		for(int i = 0; i<temp.size(); i++) {
			if(Character.isLetter(temp.get(i).charAt(0))) {
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(temp.get(i));
				tempList.add(temp.get(++i));
				if(myMap.containsKey(tempList)) {
					i++;
					myMap.get(tempList).add(Integer.parseInt(temp.get(i+1)));
					i++;
					
				}
				else {
					ArrayList<Integer> tempList2 = new ArrayList<Integer>();
					//ArrayList<String> tempList3 = new ArrayList<String>();
					System.out.println(temp.get(i));
					tempList2.add(Integer.parseInt(temp.get(i++)));
					System.out.println(temp.get(i));;
					myMap.put(tempList, tempList2);
					System.out.println(temp.get(i));
				}
			}
		}
		*/
		//System.out.println(temp.toString());
		//System.out.println(myMap.toString());
		System.out.println(SimpleJsonWriter.asNestedArray(myMap).toString());
		/*
		if(filename!=null) {
			System.out.println("Writing...");
			try (PrintWriter out = new PrintWriter(filename)) {
				System.out.println("Writing2...");
				out.println(SimpleJsonWriter.asNestedArray(myMap).toString());
				
			}
			catch(Exception E) {
				System.out.println("Error: "+E);
			}
		}
		*/
		
		
		

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
