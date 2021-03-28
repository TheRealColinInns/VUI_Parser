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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	
	// TODO Exception handling
	// TODO Fix exception handling (don't throw in Driver.main but throw in most methods) 
	// TODO user output (user friendly and informative)
	// TODO Try to have 2 try/catch blocks, one per flag you have to process
	
	// TODO What to include in Driver
	// TODO Driver is usually programmer-specific and not shared. Try to keep project-specific logic in Driver only, so all other classes are as general as possible for other developers to use *and* move any generally-useful code outside of Driver so it can be reused.

	// TODO Static variables
	// TODO Avoid static data when possible, local variables often do the trick
	
	// TODO Variable naming (self-documenting variable names without "temp" numbers, or abbreviations)
	

	// TODO If you pass the list as a parameter, it doesn't have to be static
	/**
	 * Traverses through the directory and its subdirectories, outputting all
	 * paths to the console. For files, also includes the file size in bytes.
	 *
	 * @param start the initial path to traverse
	 * @throws IOException if an I/O error occurs
	 */
	public static void printListing(Path start, ArrayList<String> myStorage, Map<String, Integer> myWordCountMap) throws IOException {
		// use the Files class to get information about a path
		if (Files.isDirectory(start)) {
			// output trailing slash to indicate directory
			// start directory traversal
			traverseDirectory(start, myStorage, myWordCountMap);
		}
		else {
			// and to the placeholder arraylist, make sure it is a text file because this is in a directory
			if(start.toString().toLowerCase().endsWith(".txt")||start.toString().toLowerCase().endsWith(".text")) {
				int wordCount = 0;
				for(String myString:TextFileStemmer.listStems(start)) {
					myStorage.add(myString);
					wordCount++;
				}
				myWordCountMap.put(start.toString(), (wordCount-1)/2);
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
	private static void traverseDirectory(Path directory, ArrayList<String> myStorage, Map<String, Integer> myWordCountMap) throws IOException {
		/*
		 * The try-with-resources block makes sure we close the directory stream
		 * when done, to make sure there aren't any issues later when accessing this
		 * directory.
		 *
		 * Note, however, we are still not catching any exceptions. This type of try
		 * block does not have to be accompanied with a catch block. (You should,
		 * however, do something about the exception.)
		 */
		try (DirectoryStream<Path> myDirectoryStream = Files.newDirectoryStream(directory)) {
			// use an enhanced-for or for-each loop for efficiency and simplicity
			for (Path temporaryPath : myDirectoryStream) {
				printListing(temporaryPath, myStorage, myWordCountMap);
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
		/*
		 * TODO Try to simplify
		 * 

		declare your argument map
		declare your inverted index data structure
		
		if (-text flag) {
			path value is of the -text flag
			
			try {
					1-2 method calls inside to trigger building
			 }
			 catch (  ) {
			 
			 }
			
		}
		
		if (-index flag) {
			path value is of the -index flag
			
			try {
					1-2 method calls inside to trigger writing
			 }
			 catch (  ) {
			 
			 }
		}

		 */
		
		
		
		// store initial start time
		QueryParser myQueryParser = new QueryParser();
		boolean queryExist = false;
		boolean toWrite = false;
		Path myPath = null;
		ArgumentMap myArgMapStem = new ArgumentMap();
		//stores the filename to add into temp
		String filename = null;
		Path queryPath = null;
		Map<String, ArrayList<ArrayList<String>>> searchResults = new TreeMap<String, ArrayList<ArrayList<String>>>();
		//reset the temporary arraylist
		ArrayList<String> myStorage = new ArrayList<String>();
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		Map<String, Integer> myWordCountMap = new TreeMap<String, Integer>();
		HashSet<? extends TreeSet<String>> myQuerySet = new HashSet<TreeSet<String>>();
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
						int wordCount = 0;
						for(String myString:TextFileStemmer.listStems(myPath)) {
							myStorage.add(myString);
							wordCount++;
						}
						myWordCountMap.put(myPath.toString(), (wordCount-1)/2);
					} catch (IOException e) {
						System.out.println("Falure while getting printing single file");
					}
				}
				else {
					try {
						printListing(myPath, myStorage, myWordCountMap);
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
		if(!myStorage.isEmpty()) {
			pathname = myStorage.get(0);
		}
		if(myArgMapStem.hasFlag("-query")) {
			queryPath = Path.of(myArgMapStem.getString("-query"));
			queryExist = true;
		}
		
		//converts the temporary arraylist into the datastructure that I want to use, a nested map
		for(int i = 1; i<myStorage.size(); i++) {
			Map<String, Collection<Integer>> valueToAdd = new TreeMap<String, Collection<Integer>>();
			Collection<Integer> colToAdd = new TreeSet<Integer>();
			if(myStorage.get(i).contains("/")) {
				pathname = myStorage.get(i);
			}
			else {
				if(myMap.containsKey(myStorage.get(i))) {
					if(myMap.get(myStorage.get(i)).containsKey(pathname)) {
						myMap.get(myStorage.get(i)).get(pathname).add(Integer.parseInt(myStorage.get(++i)));
					}
					else {
						colToAdd.add(Integer.parseInt(myStorage.get(i+1)));
						myMap.get(myStorage.get(i++)).put(pathname, colToAdd);
					}
				}
				else {
					colToAdd.add(Integer.parseInt(myStorage.get(i+1)));
					valueToAdd.put(pathname, colToAdd);
					myMap.put(myStorage.get(i++), valueToAdd);
				}
			}
			
		}
		
		String countPath;
		if(myArgMapStem.hasFlag("-counts")) {
			countPath = myArgMapStem.getString("-counts");
			if(countPath==null) {
				countPath = "counts.json";
			}
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(countPath),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asWordCountNestedArray(myWordCountMap).toString());
			} catch (IOException e) {
				System.out.println("Invalid Path");
				
				// Unable to write the inverted to JSON file from -index value: + path (-index value was)
			}
		}
		
		
		
		
		
		 //checks if we are supposed to be writing and then writes the datastructure
		if(toWrite) {
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asNestedArray(myMap).toString());
			} catch (IOException e) {
				toWrite = false;
				System.out.println("Invalid Path");
				
				// Unable to write the inverted to JSON file from -index value: + path (-index value was)
			}
			
			
		}
		
		if(queryExist) {
			myQuerySet = myQueryParser.parse(queryPath);
			if(myArgMapStem.hasFlag("-exact")) {
				for(TreeSet<String> singleQuery:myQuerySet) {
					String textQuery = "";
					for(String myQuery:singleQuery) {
						textQuery += myQuery+" ";
					}
					textQuery = textQuery.substring(0, textQuery.length() - 1);
					searchResults.put(textQuery, SearchQuery.exactSearch(myMap, myWordCountMap, singleQuery));
				}
			}
			else {
				for(TreeSet<String> singleQuery:myQuerySet) {
					String textQuery = "";
					for(String myQuery:singleQuery) {
						textQuery += myQuery+" ";
					}
					textQuery = textQuery.substring(0, textQuery.length() - 1);
					searchResults.put(textQuery, SearchQuery.partialSearch(myMap, myWordCountMap, singleQuery));
				}
			}
			
		}
		if(myArgMapStem.hasFlag("-results")) {
			String resultPath = myArgMapStem.getString("-results");
			//System.out.println("Results: "+searchResults.toString());
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(resultPath),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asResultNestedArray(searchResults).toString());
			} catch (IOException e) {
				System.out.println("Invalid Path");
				
				// Unable to write the inverted to JSON file from -index value: + path (-index value was)
			}
		}
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
