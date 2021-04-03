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

		// Creates my argument map and parses the command line arguments
		ArgumentMap myArgumentMapStem = new ArgumentMap(); // TODO Rethink name
		myArgumentMapStem.parse(args);

		// ArrayList myStorage is used as a temporary basic data structure that will be
		// converted to a map later
		ArrayList<String> myStorage = new ArrayList<String>(); // TODO Remove, only storage needed up here should be the inverted index

		// the input file into the simple storage system
		if (myArgumentMapStem.hasFlag("-text")) {
			// TODO Simplify!
			/*
			 * TODO Path input = ...
			 * 
			 * if (input is null) { warn }
			 * else {
			 * 	try {
			 * 		call 1 method here
			 * 	}
			 * 	catch ( ) {
			 * 		user friendly output
			 * 	}
			 * }
			 * 
			 * No need for the boolean variable.
			 *
			 */
			Path myPath = null;
			boolean pathExist = true;
			try {
				myPath = Path.of(myArgumentMapStem.getString("-text"));
				if (myPath == null) {
					pathExist = false;
				}
				if (pathExist) {

					dataConverter.createStorage(myPath, myStorage);

				}
			} catch (Exception e) {
				// TODO Are you sure that is what happened? Is this really that user friendly?
				System.out.println("There was an IO exception while creating the data structure from path a null path");
			}

		}

		// This step converts the basic arraylist into a more complex map data structure
		// which will be much more useful later
		InvertedIndex myInvertedIndex = new InvertedIndex();
		dataConverter.arrayListToMap(myStorage, myInvertedIndex);

		if (myArgumentMapStem.hasFlag("-index")) {
			// TODO Use getPath(-index, Path.of(index.json)) here instead
			String filename = myArgumentMapStem.getString("-index");
			if (filename == null) {
				filename = "index.json";
			}
			myInvertedIndex.dataWriter(filename);
			
			// TODO Should be a try/catch here
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
