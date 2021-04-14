import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
