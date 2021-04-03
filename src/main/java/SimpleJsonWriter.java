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

// TODO Test your code here; make sure it passes for empty data structures, different nest levels, etc.

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
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> elementsIterator = elements.iterator();
		if (elementsIterator.hasNext()) {
			indent(elementsIterator.next().toString(), writer, level);
		}
		while (elementsIterator.hasNext()) {
			writer.write(",\n");
			indent(elementsIterator.next().toString(), writer, level);
		}
		writer.write("\n");
		
		// TODO Does this actually pass the original homework tests? It doesn't look like it.
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		indent("{\n", writer, level);
		level++;
		String next;
		Iterator<String> keyIterator = elements.keySet().iterator();
		if (keyIterator.hasNext()) {
			next = keyIterator.next();
			indent("\"" + next + "\": " + elements.get(next), writer, level);
		}
		while (keyIterator.hasNext()) {
			writer.write(",\n");
			next = keyIterator.next();
			indent("\"" + next + "\": " + elements.get(next), writer, level);
		}
		level--;
		indent("}", writer, level);
	}

	/**
	 * Helps write the elements as a pretty JSON object with a nested array. The
	 * generic notation used allows this method to be used for any type of map with
	 * any type of nested collection of integer objects.
	 *
	 * @param nested the elements to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void nestedMapHelper(Map<String, ? extends Collection<Integer>> nested, Writer writer, int level)
			throws IOException {
		Iterator<String> pathIterator = nested.keySet().iterator();
		String pathNext;
		if (pathIterator.hasNext()) {
			pathNext = pathIterator.next();
			indent("\"" + pathNext + "\": [\n", writer, level); // TODO The [ ] output should be in asArray, not here...
			level++;
			asArray(nested.get(pathNext), writer, level);
			level--;
			indent("]", writer, level);
		}
		while (pathIterator.hasNext()) {
			writer.write(",\n");
			pathNext = pathIterator.next();
			indent("\"" + pathNext + "\": [\n", writer, level);
			level++;
			asArray(nested.get(pathNext), writer, level);
			level--;
			indent("]", writer, level);
		}

		writer.write("\n");

	}
	
	/*
	 * TODO Rethink the names here. asNestedArray is more than outputting a nested array,
	 * which is what nestedMapHelper is now doing. That is useful beyond just being called
	 * inside your asNestedArray method! 
	 */

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements,
			Writer writer, int level) throws IOException {
		if (elements.isEmpty()) {
			writer.write("{\n}");
		} else {
			indent("{\n", writer, level);
			level++;
			String wordNext;
			Iterator<String> wordIterator = elements.keySet().iterator();
			if (wordIterator.hasNext()) {
				wordNext = wordIterator.next();
				indent("\"" + wordNext + "\": {\n", writer, level);
				level++;
				nestedMapHelper(elements.get(wordNext), writer, level);
				level--;
				indent("}", writer, level);
			}
			while (wordIterator.hasNext()) {
				writer.write(",\n");
				wordNext = wordIterator.next();
				indent("\"" + wordNext + "\": {\n", writer, level);
				level++;
				nestedMapHelper(elements.get(wordNext), writer, level);
				level--;
				indent("}", writer, level);
			}
			writer.write("\n}");
		}
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
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
		} catch (IOException e) {
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
		} catch (IOException e) {
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
	public static String asNestedArray(Map<String, ? extends Map<String, ? extends Collection<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param level   the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(String element, Writer writer, int level) throws IOException {
		writer.write("\t".repeat(level));
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param level   the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void quote(String element, Writer writer, int level) throws IOException {
		writer.write("\t".repeat(level));
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

}
