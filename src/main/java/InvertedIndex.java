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
	/*
	 * TODO Not okay to have public data. Need to also properly initialize in the constructor.
	 */
	
	/**
	 * this is our data structure
	 */
	public Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();

	/**
	 * writes our inverted index into a specified file
	 *
	 * @param filename this is the file name that we are going to write the inverted
	 *                 index to
	 */
	public void dataWriter(String filename) { // TODO Take a Path as a parameter when you know your code will open or write a file
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
			// TODO Most inefficient way to do this!
			writer.write(SimpleJsonWriter.asNestedArray(this.myMap).toString());
			
			/*
			 * TODO What is happening above is you create a GIANT string of the entire index
			 * in memory and do a SINGLE large write operation to the file. Your entire index
			 * is now in two places in memory temporarily, which is pretty inefficient. Instead,
			 * use the version that uses the BufferedWriter directly... never making a giant
			 * copy in memory and instead writing directly to the file.
			 */
		} catch (IOException e) {
			// TODO Fix exception handling; remember throw exceptions in most methods and catch exceptions in Driver.main (only place that should really produce console output)
			System.out.println("Unable to write the inverted to JSON file from -index value: " + filename);
		}
	}

	/*
 * TODO Improve generalization/usefulness of this class.
 *
 * All nested levels should somehow be accessible. For example, TextFileIndex
 * had 2 levels of nesting and had 2 contains methods. This class has 3 levels
 * of nesting, and so should have 3 contains methods... and so on.
 *
 * Standard methods include: add, contains or has methods, size or num
 * methods, and get methods.
 *
 * Consider also adding an addAll convenience method such that given a list of
 * words and the location/path they came from, it adds each to the inverted
 * index using the list index as the position.
 *
 * TextFileIndex (homework) and PrefixMap (lecture) have some good examples of
 * how to go about this.
 */

	// TODO Always @Override the toString method with something useful.

}
