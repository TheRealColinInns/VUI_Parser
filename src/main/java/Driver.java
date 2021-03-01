import java.io.BufferedWriter;
//import java.io.File;
import java.io.IOException;
//import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
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
			// output the file path and file size in bytes
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
		String filename = null;
		temp = new ArrayList<String>();
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		//Map<Map<String, String>, Collection<Integer>> myMap = new HashMap<Map<String, String>, Collection<Integer>>();
		Instant start = Instant.now();
		myArgMapStem.parse(args);
		boolean notPath = true;
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
			/*
			if(Files.isRegularFile(myPath)) {
				try {
					temp = (TextFileStemmer.listStems(myPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(Files.isDirectory(myPath)){
				
				
			}
			ArrayList<Path> generalList = new ArrayList<Path>();
			ArrayList<Path> dirAt = new ArrayList<Path>();
			generalList.add(myPath);
			dirAt.add(myPath);
			while(!generalList.isEmpty()) {
				//System.out.println("BIG LOOP-------------------------------+");
				for(int i = 0; i<generalList.size(); i++) {
					//System.out.println("Looping general: length: "+generalList.size()+"     iteration: "+i);
					if(generalList.get(i).toFile().isDirectory()) {
						//System.out.println("Directory: "+generalList.get(i));
						dirAt.remove(generalList.get(i));
						//System.out.println("Removed: "+generalList.get(i).toString());
						try {
							for(File tempFile:generalList.get(i).toFile().listFiles()) {
								if(tempFile.toString().toLowerCase().endsWith(".txt")||tempFile.toString().toLowerCase().endsWith(".text")) {
									dirAt.add(tempFile.toPath());
								}
								//System.out.println("Added: "+tempFile.toPath().toString());
							}
						}
						catch(Exception e5) {
							//System.out.println("Problem here: "+e5);
						}
					}
					else {
						//if(generalList.get(i).toString().toLowerCase().endsWith(".txt")||generalList.get(i).toString().toLowerCase().endsWith(".text")) {
						//System.out.println("2: "+generalList.get(i).toString().toLowerCase());
						//String genListString = generalList.get(i).toString().toLowerCase();
						
						try {
							
							temp.addAll(TextFileStemmer.listStems(generalList.get(i)));
							
							//System.out.println("Wrote + Removed: "+generalList.get(i).toString());
							dirAt.remove(generalList.get(i));
							
						} catch (IOException e) {
							dirAt.remove(generalList.get(i));
							System.out.println("Invalid Path");
							//e.printStackTrace();
						}
						//}
					}
				}
				
			
				try {
					generalList = dirAt;
				}
				catch(Exception E){
					System.out.println("Cant change");
				}
				
			}
			*/
			
		}
		//dirAt = new HashSet<Path>();
		//myPath = Path.of(myArgMapStem.getString("-text"));
		if(myArgMapStem.hasFlag("-index")) {
			filename = myArgMapStem.getString("-index");
			if(filename==null) {
				filename = "index.json";
			}
			toWrite = true;
		}
		//System.out.println(temp.toString());
		String pathname = null;
		if(!temp.isEmpty()) {
			pathname = temp.get(0);
		}
		
		for(int i = 1; i<temp.size(); i++) {
			Map<String, Collection<Integer>> valueToAdd = new TreeMap<String, Collection<Integer>>();
			Collection<Integer> colToAdd = new TreeSet<Integer>();
			//System.out.println("Current: "+temp.get(i));
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
		//System.out.println("HERE: "+toWrite);
		 
		if(toWrite) {
			try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename),StandardCharsets.UTF_8)){
				writer.write(SimpleJsonWriter.asNestedArray(myMap).toString());
			} catch (IOException e) {
				toWrite = false;
				System.out.println("Invalid Path");
			}
			
			
		}
		/*
		for(int i = 0; i<temp.size(); i++) {
			if(Character.isLetter(temp.get(i).charAt(0))) {
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(temp.get(i));
				tempList.add(temp.get(++i));
				if(myMap.containsKey(tempList)) {
					i++;
					myMap.get(tempList).add(Integer.parseInt(temp.get(i+1)));
					i++;
					
				}
				else {
					ArrayList<Integer> tempList2 = new ArrayList<Integer>();
					//ArrayList<String> tempList3 = new ArrayList<String>();
					System.out.println(temp.get(i));
					tempList2.add(Integer.parseInt(temp.get(i++)));
					System.out.println(temp.get(i));;
					myMap.put(tempList, tempList2);
					System.out.println(temp.get(i));
				}
			}
		}
		*/
		//System.out.println(temp.toString());
		//System.out.println(myMap.toString());
		//System.out.println(SimpleJsonWriter.asNestedArray(myMap).toString());
		/*
		if(filename==null) {
			filename="index.json";
		}
		if(toWrite) {
		System.out.println("Writing...");
		try (PrintWriter out = new PrintWriter(filename)) {
			System.out.println("Writing2...");
			out.write(SimpleJsonWriter.asNestedArray(myMap).toString());
			
		}
		catch(Exception E) {
			System.out.println("Error: "+E);
		}
		}
		*/
		
		

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
