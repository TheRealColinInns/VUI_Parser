import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class responsible for storing the data structure See the README for details.
 *
 * @author CS 212 Software Development, Colin Inns
 * @author University of San Francisco
 * @version Spring 2021
 */
public class InvertedIndex {
	// this is the form our inverted index is going to follow
	public Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();

	/**
	 * writes our inverted index into a specified file
	 *
	 * @param filename this is the file name that we are going to write the inverted
	 *                 index to
	 */
	public void dataWriter(String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
			writer.write(SimpleJsonWriter.asNestedArray(this.myMap).toString());
		} catch (IOException e) {
			System.out.println("Unable to write the inverted to JSON file from -index value: " + filename);
		}
	}

}
