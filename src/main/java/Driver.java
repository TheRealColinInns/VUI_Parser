
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development, Colin Inns
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
		Instant start = Instant.now();
		
		//Creates my argument map and parses the command line arguments
		ArgumentMap myArgumentMapStem = new ArgumentMap();
		myArgumentMapStem.parse(args);

		
		//ArrayList myStorage is used as a temporary basic data structure that will be converted to a map later
		ArrayList<String> myStorage = new ArrayList<String>();
		//Map that the ArrayList will eventually convert itself into
		
		
		// TODO Split the data storage versus data parsing logic into separate classes (e.g. a data structure class that focuses on storage with as few assumptions as possible, and a "builder" or "factory" class that creates that data structure in a specific way)

		// TODO What this means is put myMap in an "InvertedIndex" class and moving more of this to your DataConverter class.
		
		//this gets and adds all the elements from all files and directories located at the input file into the simple storage system
		if(myArgumentMapStem.hasFlag("-text")) {
			Path myPath = null;
			boolean pathExist = true;
			try {
				myPath = Path.of(myArgumentMapStem.getString("-text"));
				if(myPath==null) {
					pathExist = false;
				}
				if(pathExist) {
					
					dataConverter.createStorage(myPath, myStorage);
					
					
				}
			}
			catch(Exception e) {
				System.out.println("There was an IO exception while creating the data structure from path a null path");
			}
			
			
			
		}
		
		//This step converts the basic arraylist into a more complex map data structure which will be much more useful later
		InvertedIndex myInvertedIndex = new InvertedIndex();
		dataConverter.arrayListToMap(myStorage, myInvertedIndex);
		
		
		
		if(myArgumentMapStem.hasFlag("-index")) {
			String filename = myArgumentMapStem.getString("-index");
			if(filename==null) {
				filename = "index.json";
			}
			myInvertedIndex.dataWriter(filename);
		}
		
		 
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
