import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
		ArrayList<String> temp = new ArrayList<String>();
		Map<String, Map<String, Collection<Integer>>> myMap = new TreeMap<String, Map<String, Collection<Integer>>>();
		//Map<Map<String, String>, Collection<Integer>> myMap = new HashMap<Map<String, String>, Collection<Integer>>();
		Instant start = Instant.now();
		myArgMapStem.parse(args);
		//System.out.println(myArgMapStem.toString());
		if(myArgMapStem.hasFlag("-text")) {
			myPath = Path.of(myArgMapStem.getString("-text"));
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
								dirAt.add(tempFile.toPath());
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
							//if(generalList.get(i).toString().toLowerCase().endsWith(".txt")||generalList.get(i).toString().toLowerCase().endsWith(".text")) {
								temp.addAll(TextFileStemmer.listStems(generalList.get(i)));
							//}
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
		String pathname = temp.get(0);
		
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
