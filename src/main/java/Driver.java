import java.io.IOException;
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

		// tests if we are multi-threading
		InvertedIndex myInvertedIndex;
		SearchResults results;
		boolean multithreaded;
		if (flagValuePairs.hasFlag("-threads")) {
			// the inverted index data structure that we will store all of the data in, but
			// thread safe
			myInvertedIndex = new ThreadSafeInvertedIndex();
			// the results of the search, but thread safe
			results = new ThreadSafeSearchResults();
			// tells code we are multi-threading
			multithreaded = true;

		} else {
			// the inverted index data structure that we will store all of the data in
			myInvertedIndex = new InvertedIndex();
			// the results of the search
			results = new SearchResults();
			// tells code we are not multi-threading
			multithreaded = false;
		}

		// the input file into the inverted index
		if (flagValuePairs.hasFlag("-text")) {
			Path inputPath = flagValuePairs.getPath("-text");
			if (inputPath == null) {
				System.out.println("The input file was null");
			} else {
				try {
					InvertedIndexCreator.createInvertedIndex(inputPath, myInvertedIndex);
				} catch (Exception e) {
					System.out.println("IO Exception while reading path: " + inputPath.toString());
				}
			}
		}

		// writes the inverted index to the desired location
		if (flagValuePairs.hasFlag("-index")) {
			Path outputPath = flagValuePairs.getPath("-index", Path.of("index.json"));
			try {
				myInvertedIndex.indexWriter(outputPath);
			} catch (Exception e) {
				System.out.println("IOException while writing index to " + outputPath.toString());
			}
		}

		// puts together the results
		if (flagValuePairs.hasFlag("-query")) {
			Path queryPath = flagValuePairs.getPath("-query");
			if (queryPath != null) {
				try {
					if (multithreaded) {
						if (flagValuePairs.hasFlag("-exact")) {
							myInvertedIndex.parse(queryPath, results, true, flagValuePairs.getInteger("-threads", 5));
						} else {
							myInvertedIndex.parse(queryPath, results, false, flagValuePairs.getInteger("-threads", 5));
						}
					} else {
						if (flagValuePairs.hasFlag("-exact")) {
							myInvertedIndex.parse(queryPath, results, true, 0);
						} else {
							myInvertedIndex.parse(queryPath, results, false, 0);
						}
					}
				} catch (IOException e) {
					System.out.println("Unable to aquire queries from path " + queryPath.toString());
				}
			}
		}

		// writes the counts
		if (flagValuePairs.hasFlag("-counts")) {
			Path countPath = flagValuePairs.getPath("-counts", Path.of("counts.json"));
			try {
				myInvertedIndex.writeWordCount(countPath);
			} catch (Exception e) {
				System.out.println("IO Exception while writing word count to " + countPath.toString());
			}
		}

		// writes the results of the search
		if (flagValuePairs.hasFlag("-results")) {
			Path resultsPath = flagValuePairs.getPath("-results", Path.of("results.json"));
			try {
				results.write(resultsPath);
			} catch (IOException e) {
				System.out.println("IO Exception while writing results to " + resultsPath);
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
