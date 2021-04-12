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

		// the inverted index data structure that we will store all of the data in
		InvertedIndex myInvertedIndex = new InvertedIndex();
		//the word count map we will store the word counts in
		WordCount myWordCount = new WordCount();
		//the query structure we will use to store the queries
		QueryParser myQueryParser = new QueryParser();

		// the input file into the inverted index
		if (flagValuePairs.hasFlag("-text")) {
			Path inputPath = flagValuePairs.getPath("-text");
			if (inputPath == null) {
				System.out.println("The input file was null");
			} else {
				try {
					InvertedIndexCreator.createInvertedIndex(inputPath, myInvertedIndex, myWordCount);
				} catch (Exception e) {
					System.out.println("IO Exception while reading path: " + inputPath.toString());
				}
			}
		}

		// writes the inverted index to the desired location
		if (flagValuePairs.hasFlag("-index")) {
			Path outputPath = flagValuePairs.getPath("-index", Path.of("index.json"));
			try {
				myInvertedIndex.dataWriter(outputPath);
			} catch (Exception e) {
				System.out.println("IOException while writing index to " + outputPath.toString());
			}
		}
		
		if(flagValuePairs.hasFlag("-query")) {
			Path queryPath = flagValuePairs.getPath("-query");
			try {
				myQueryParser.parse(queryPath);
				if(flagValuePairs.hasFlag("-exact")) {
					SearchQuery.exactSearch(myInvertedIndex, myWordCount, myQueryParser);
				}
				else {
					SearchQuery.partialSearch(myInvertedIndex, myWordCount, myQueryParser);
				}
			} catch (IOException e) {
				System.out.println("Unable to aquire queries from path "+queryPath.toString());
			}
		}
		
		if(flagValuePairs.hasFlag("-counts")) {
			Path countPath = flagValuePairs.getPath("-counts", Path.of("counts.json"));
			try {
				myWordCount.write(countPath);
			}
			catch(Exception e) {
				System.out.println("IO Exception while writing word count to "+countPath.toString());
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
