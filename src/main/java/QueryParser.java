import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class responsible for converting an array list into the map data structure
 * desired that will be very important later for creating the json file
 *
 * @author CS 212 Software Development - Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class QueryParser {

	/**
	 * the structure used to store the queries
	 */
	private Set<TreeSet<String>> querySet;

	/**
	 * constructor for query parser class
	 */
	public QueryParser() {
		querySet = new HashSet<TreeSet<String>>();
	}

	/**
	 * gets an unmodified set
	 * 
	 * @return the unmodified set
	 */
	public Set<TreeSet<String>> get() {
		return Collections.unmodifiableSet(this.querySet);
	}

	/**
	 * parses all of the queries at a location
	 * 
	 * @param fileName the file we are reading the query from
	 * @throws IOException exception thrown if file doesn't exist
	 */
	public void parse(Path fileName) throws IOException {
		try (BufferedReader mybr = Files.newBufferedReader(fileName, StandardCharsets.UTF_8);) {
			for (String line = mybr.readLine(); line != null; line = mybr.readLine()) {
				TreeSet<String> parsed = TextFileStemmer.uniqueStems(line);
				if (!parsed.isEmpty()) {
					querySet.add(parsed);
				}
			}
		}
	}
}
