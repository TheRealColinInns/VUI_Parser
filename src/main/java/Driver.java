import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development - Colin Inns
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
		ArgumentMap flagValuePairs = new ArgumentMap();
		flagValuePairs.parse(args);

		// the inverted index data structure that we will store all of the data in
		InvertedIndex myInvertedIndex = new InvertedIndex();

		// the input file into the inverted index
		if (flagValuePairs.hasFlag("-text")) {
			Path inputPath = flagValuePairs.getPath("-text");
			if (inputPath == null) {
				System.out.println("The input file was null");
			} else {
				try {
					InvertedIndexCreator.createInvertedIndex(inputPath, myInvertedIndex);
				} catch (Exception e) {
					System.out.println("IO Exception for input path: " + inputPath.toString());
				}
			}
		}

		// writes the inverted index to the desired location
		if (flagValuePairs.hasFlag("-index")) {
			Path outputPath = flagValuePairs.getPath("-index", Path.of("index.json"));
			try {
				myInvertedIndex.dataWriter(outputPath);
			} catch (Exception e) {
				System.out.println("IOException while writing to " + outputPath.toString());
			}

		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
