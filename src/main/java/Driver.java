import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;


/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development, Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {

	// TODO Variable naming (self-documenting variable names without "temp" numbers, or abbreviations)
	

	
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
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		
		
		
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
					if(Files.isRegularFile(myPath)) {
						myStorage.addAll(TextFileStemmer.listStems(myPath));
						System.out.println("Falure while getting path: "+myPath.toString());
					
					}
					else {
					
						DirectoryNavigator.printListing(myPath, myStorage);
						System.out.println("Falure while getting directory at path: "+myPath.toString());
					}
				}
			}
			catch(Exception e) {
				
			}
			
		}
		
		//This step converts the basic arraylist into a more complex map data structure which will be much more useful later
		myMap = dataConverter.arrayListToMap(myStorage);
		
		
		//This gets the path and prints to that path a pretty json file of the map data structure previously created in the last step
		if(myArgumentMapStem.hasFlag("-index")) {
			String filename = myArgumentMapStem.getString("-index");
			if(filename==null) {
				filename = "index.json";
			}
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asNestedArray(myMap).toString());
			} catch (IOException e) {
				System.out.println("Unable to write the inverted to JSON file from -index value: "+filename);
			}
		}
		
		 
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
