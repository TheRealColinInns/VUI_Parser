import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 * 
 * @author colininns
 * @version VUI
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. Additionally helps to extract and parse the content on a page.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();

		// Creates my argument map and parses the command line arguments
		ArgumentMap flagValuePairs = new ArgumentMap();
		flagValuePairs.parse(args);

		// makes sure we have to proper flag
		if (flagValuePairs.hasFlag("-input")) {
			// creates map that will be used to store the content on a page
			Map<String, List<String>> fetchedContent = null;
			// creates an instance of the Index class to parse the content
			Index index = new Index();
			// fetches the content of the page and palces it into the previously created map
			try {
				fetchedContent = HttpsFetcher
						.fetchURL(LinkParser.normalize(new URL(flagValuePairs.getString("-input"))));
			} catch (MalformedURLException e) {
				System.out.println(
						"(Malformed URL) Error with input URL: " + flagValuePairs.getString("-input", "No URL given"));
			} catch (IOException e) {
				System.out.println(
						"(IO) Unable to reach input URL: " + flagValuePairs.getString("-input", "No URL given"));
			} catch (Exception e) {
				System.out.println("Error with URL: " + flagValuePairs.getString("-input", "No URL given"));
			}

			// only run if we got content
			if (fetchedContent != null) {
				if (!fetchedContent.isEmpty()) {
					// make sure it is a text file
					if (fetchedContent.get("Content-Type").get(0).contains("text")) {
						String currentIntent = "ERROR NO INTENT";
						for (String content : fetchedContent.get("Content")) {
							String tempIntent = index.parse(content, currentIntent);
							if (tempIntent != null) {
								currentIntent = tempIntent;
							}
						}
					} else {
						System.out.println("Wrong Content Type, Expected text but recieved: "
								+ fetchedContent.get("Content-Type").get(0));
					}
				} else {
					System.out.println("URL empty: " + flagValuePairs.getString("-input", "No URL given"));
				}
			}

			try {
				FileWriter.write(index, flagValuePairs.getPath("-results", Path.of("results")));
			} catch (IOException e) {
				System.out.println("IO exception while writing results to "
						+ flagValuePairs.getPath("-results", Path.of("/results")));
			}
		} else {
			System.out.println("No input given.");
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
