import java.io.IOException;
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
		ArgumentMap myArgMapStem = new ArgumentMap();
		SimpleJsonWriter myWriter = new SimpleJsonWriter();
		TextFileStemmer myStemmer = new TextFileStemmer();
		ArrayList<String> temp = new ArrayList<String>();
		Map<String, Collection<Integer>> myMap = new HashMap<String, Collection<Integer>>();
		Instant start = Instant.now();
		myArgMapStem.parse(args);
		if(myArgMapStem.hasFlag("-text")) {
			try {
				temp.addAll(myStemmer.listStems(Path.of(myArgMapStem.getString("-text"))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(temp.toString());
		for(int i = 0; i<temp.size(); i++) {
			if(Character.isLetter(temp.get(i).charAt(0))) {
				if(myMap.containsKey(temp.get(i))) {
					myMap.get(temp.get(i)).add(Integer.parseInt(temp.get(i+1)));
					i++;
				}
				else {
					ArrayList<Integer> tempList = new ArrayList<Integer>();
					tempList.add(Integer.parseInt(temp.get(i+1)));
					myMap.put(temp.get(i), tempList);
					i++;
				}
			}
		}
		System.out.println(myWriter.asNestedArray(myMap).toString());
		
		//System.out.println(myMap.toString());
		/*
		try{String[] arr = new String[temp.size()]; arr = temp.toArray(arr); myArgMap.parse(arr);}
		catch(Exception E){
			System.out.println("Unable to Convert: "+E);
		}
		*/
		

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
