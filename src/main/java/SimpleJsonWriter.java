import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2021
 */
public class SimpleJsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer,
			int level) throws IOException {
		level++;
		boolean revfirstTimer = false;
		
		writer.write("[\n");
		for(Integer item:elements) {
			if(revfirstTimer) {
				writer.write(",\n");
			}
			indent(item.toString(), writer, level);
			revfirstTimer = true;
		}
		if(!elements.isEmpty()) {
			writer.write("\n");
		}
		writer.write("]");

		
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer,
			int level) throws IOException {
		level++;
		boolean revfirstTimer = false;
		writer.write("{\n");
		for(Map.Entry<String,Integer> item : elements.entrySet()) {
			if(revfirstTimer) {
				writer.write(",\n");
			}
			
			indent("\""+item.getKey()+"\": "+item.getValue(), writer, level);
			revfirstTimer = true;
		}
		if(!elements.isEmpty()) {
			writer.write("\n");
		}
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The
	 * generic notation used allows this method to be used for any type of map
	 * with any type of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(
			Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements, Writer writer,
			int level) throws IOException {
		level++;
		//boolean revfirstTimer = false;
		writer.write("{\n");
		int putSqigBracketComma = elements.size();
		for(Entry<String, ? extends Map<String, ? extends Collection<Integer>>> item : elements.entrySet()) {
			int putBracketComma = item.getValue().size();
			indent("\""+item.getKey()+"\": {\n",writer, level);
			level++;
			for(Entry<String, ? extends Collection<Integer>> nestedItem:item.getValue().entrySet()) {
				int putNumberComma = nestedItem.getValue().size();
				indent("\""+nestedItem.getKey()+"\": [\n",writer, level);
				level++;
				for(Integer nestedInt:nestedItem.getValue()) {
					if(putNumberComma>1) {
						indent(nestedInt.toString()+",\n",writer, level);
					}
					else {
						indent(nestedInt.toString()+"\n",writer, level);
						level--;
					}
					putNumberComma--;
				}
				if(putBracketComma>1) {
					indent("],\n",writer, level);
				}
				else {
					indent("]\n",writer, level);
					level--;
				}
				putBracketComma--;
			}
			if(putSqigBracketComma>1) {
				indent("},\n",writer,level);
			}
			else {
				indent("}\n",writer,level);
			}
			putSqigBracketComma--;
			
		}
		
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(
			Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements, Path path)
			throws IOException {
		try (
				BufferedWriter writer = Files.newBufferedWriter(path,
						StandardCharsets.UTF_8)
		) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(
			Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(String element, Writer writer, int level)
			throws IOException {
		writer.write("\t".repeat(level));
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "}
	 * quotation marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param level the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void quote(String element, Writer writer, int level)
			throws IOException {
		writer.write("\t".repeat(level));
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
	
	public static String asWordCountNestedArray(Map<String, Integer> wordCountMap) {
		int counter = wordCountMap.keySet().size();
		try {
			StringWriter writer = new StringWriter();
			writer.write("{\n");
			for(String mapKey:wordCountMap.keySet()) {
				if(wordCountMap.get(mapKey)!=0) {
					if(counter>2) {
						indent("\""+mapKey+"\": "+wordCountMap.get(mapKey)+",\n", writer, 1);
					}
					else {
						indent("\""+mapKey+"\": "+wordCountMap.get(mapKey)+"\n", writer, 1);
					}
					counter--;
				}
			}
			writer.write("}");
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}

	}
	public static String asResultNestedArray(Map<String, ArrayList<ArrayList<String>>> dirtyResults) {
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		try {
			StringWriter writer = new StringWriter();
			int counter = dirtyResults.keySet().size();
			for(String myKey:dirtyResults.keySet()) {
				counter--;
				int level = 1;
				writer.write("\""+myKey+"\": [\n");
				int queryCounter = dirtyResults.get(myKey).size();
				for(ArrayList<String> singleQuery:dirtyResults.get(myKey)) {
					indent("{\n", writer, level);
					level++;
					queryCounter--;
					indent("\"where\": \""+singleQuery.get(2)+"\",\n", writer, level);
					indent("\"count\": "+singleQuery.get(1)+",\n", writer, level);
					indent("\"score\": "+FORMATTER.format(singleQuery.get(0))+"\n", writer, level);
					level--;
					if(queryCounter>0) {
						indent("},\n", writer, level);
					}
					else {
						indent("}\n", writer, level);
					}
				}
				level--;
				if(counter>0) {
					indent("],\n", writer, level);
				}
				else {
					indent("]\n", writer, level);
				}
				
			}
			
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
}
