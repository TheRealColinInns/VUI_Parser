import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


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
	// TODO Try to re-think by removing any kind of conditional within a loop here. See CampusWire #346: https://campuswire.com/c/G2DE6C962/feed/346
	// TODO Try to use this same approach for all these methods
	
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
		Iterator<Integer> elementsIterator = elements.iterator();
		while(elementsIterator.hasNext()) {
			indent(elementsIterator.next().toString(), writer, level);
			if(elementsIterator.hasNext()) {
				writer.write(",\n");
			}
			else {
				writer.write("\n");
			}
		}
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
		indent("{\n", writer, level);
		level++;
		Iterator<String> keyIterator = elements.keySet().iterator();
		while(keyIterator.hasNext()) {
			String next = keyIterator.next();
			indent("\""+next+"\": "+elements.get(next), writer, level);
			if(keyIterator.hasNext()) {
				writer.write(",\n");
			}
			else {
				writer.write("\n");
			}
		}
		level--;
		indent("}", writer, level);
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
		indent("{\n", writer, level);
		level++;
		Iterator<String> wordIterator = elements.keySet().iterator();
		while(wordIterator.hasNext()) {
			String wordNext = wordIterator.next();
			indent("\""+wordNext+"\": {\n", writer, level);
			level++;
			Iterator<String> pathIterator = elements.get(wordNext).keySet().iterator();
			while(pathIterator.hasNext()) {
				String pathNext = pathIterator.next();
				indent("\""+pathNext+"\": [\n", writer, level);
				level++;
				asArray(elements.get(wordNext).get(pathNext), writer, level);
				level--;
				indent("]", writer, level);
				if(pathIterator.hasNext()) {
					writer.write(",\n");
				}
				else {
					writer.write("\n");
				}
			}
			level--;
			indent("}", writer, level);
			if(wordIterator.hasNext()) {
				writer.write(",\n");
			}
			else {
				writer.write("\n");
			}
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
	
}
