import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
//Colin Inns
/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class Driver {
	static ArrayList<String> temp = new ArrayList<String>();

	/**
	 * Traverses through the directory and its subdirectories, outputting all
	 * paths to the console. For files, also includes the file size in bytes.
	 *
	 * @param start the initial path to traverse
	 * @throws IOException if an I/O error occurs
	 */
	public static void printListing(Path start) throws IOException {
		// use the Files class to get information about a path
		if (Files.isDirectory(start)) {
			// output trailing slash to indicate directory
			// start directory traversal
			traverseDirectory(start);
		}
		else {
			// and to the placeholder arraylist, make sure it is a text file because this is in a directory
			if(start.toString().toLowerCase().endsWith(".txt")||start.toString().toLowerCase().endsWith(".text")) {
				temp.addAll(TextFileStemmer.listStems(start));
			}
			
		}
	}
	/**
	 * Traverses through the directory and its subdirectories, outputting all
	 * paths to the console. For files, also includes the file size in bytes.
	 *
	 * @param directory the directory to traverse
	 * @throws IOException if an I/O error occurs
	 */
	private static void traverseDirectory(Path directory) throws IOException {
		/*
		 * The try-with-resources block makes sure we close the directory stream
		 * when done, to make sure there aren't any issues later when accessing this
		 * directory.
		 *
		 * Note, however, we are still not catching any exceptions. This type of try
		 * block does not have to be accompanied with a catch block. (You should,
		 * however, do something about the exception.)
		 */
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			// use an enhanced-for or for-each loop for efficiency and simplicity
			for (Path path : listing) {
				printListing(path);
			}
		}
	}
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		boolean toWrite = false;
		Path myPath = null;
		ArgumentMap myArgMapStem = new ArgumentMap();
		//stores the filename to add into temp
		String filename = null;
		//reset the temporary arraylist
		temp = new ArrayList<String>();
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		Instant start = Instant.now();
		myArgMapStem.parse(args);
		boolean notPath = true;
		//this gets and adds all the elements from all files and directories located at the input file
		if(myArgMapStem.hasFlag("-text")) {
			try {
				myPath = Path.of(myArgMapStem.getString("-text"));
			}
			catch(Exception e7) {
				notPath = false;
			}
			if(notPath) {
				if(Files.isRegularFile(myPath)) {
					try {
						temp.addAll(TextFileStemmer.listStems(myPath));
					} catch (IOException e) {
						System.out.println("Falure while getting printing single file");
					}
				}
				else {
					try {
						printListing(myPath);
					} catch (IOException e) {
						System.out.println("Falure while getting directory");
					}
				}
			}
			
		}
		//gets the path in which to later print the json file
		if(myArgMapStem.hasFlag("-index")) {
			filename = myArgMapStem.getString("-index");
			if(filename==null) {
				filename = "index.json";
			}
			toWrite = true;
		}
		String pathname = null;
		if(!temp.isEmpty()) {
			pathname = temp.get(0);
		}
		//converts the temporary arraylist into the datastructure that I want to use, a nested map
		for(int i = 1; i<temp.size(); i++) {
			Map<String, Collection<Integer>> valueToAdd = new TreeMap<String, Collection<Integer>>();
			Collection<Integer> colToAdd = new TreeSet<Integer>();
			if(temp.get(i).contains("/")) {
				pathname = temp.get(i);
			}
			else {
				if(myMap.containsKey(temp.get(i))) {
					if(myMap.get(temp.get(i)).containsKey(pathname)) {
						myMap.get(temp.get(i)).get(pathname).add(Integer.parseInt(temp.get(++i)));
					}
					else {
						colToAdd.add(Integer.parseInt(temp.get(i+1)));
						myMap.get(temp.get(i++)).put(pathname, colToAdd);
					}
				}
				else {
					colToAdd.add(Integer.parseInt(temp.get(i+1)));
					valueToAdd.put(pathname, colToAdd);
					myMap.put(temp.get(i++), valueToAdd);
				}
			}
			
		}
		 //checks if we are supposed to be writing and then writes the datastructure
		if(toWrite) {
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asNestedArray(myMap).toString());
			} catch (IOException e) {
				toWrite = false;
				System.out.println("Invalid Path");
			}
			
			
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
