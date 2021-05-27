import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Writes the results to a file
 * 
 * @author colininns
 * @version VUI
 */
public class FileWriter {

	/**
	 * writes the intents
	 * 
	 * @param intents the intents map
	 * @param writer  the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writeIntents(Map<String, Integer> intents, BufferedWriter writer) throws IOException {
		writer.write("Frequency of Intents:\n");
		for (String intent : intents.keySet()) {
			writer.write(intent + " was the intent " + intents.get(intent) + " times.\n");
		}
	}

	/**
	 * writes the words
	 * 
	 * @param words  the words map
	 * @param writer the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writeWords(Map<String, Integer> words, BufferedWriter writer) throws IOException {
		writer.write("\nFrequency of Words:\n");
		for (String word : words.keySet()) {
			writer.write(word + " occurred " + words.get(word) + " times.\n");
		}
	}

	/**
	 * writes the top ten most frequent words
	 * 
	 * @param topten the top ten set
	 * @param writer the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writeTopTen(Set<String> topten, BufferedWriter writer) throws IOException {
		writer.write("\nTop Ten Most Frequent Words:\n");
		for (String word : topten) {
			writer.write(word + "\n");
		}
	}

	/**
	 * writes the bottom ten least frequent words
	 * 
	 * @param bottomten the bottom ten set
	 * @param writer    the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writeBottomTen(Set<String> bottomten, BufferedWriter writer) throws IOException {
		writer.write("\nBottom Ten Least Frequent Words:\n");
		for (String word : bottomten) {
			writer.write(word + "\n");
		}
	}

	/**
	 * writes the WCount
	 * 
	 * @param count  the WCount
	 * @param writer the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writeWCount(int count, BufferedWriter writer) throws IOException {
		writer.write("\nNumber of questions containing wh... words and the character sequence \"fare code\": " + count
				+ "\n");
	}

	/**
	 * writes the Plane Count
	 * 
	 * @param count  the plane count
	 * @param writer the writer to write to
	 * @throws IOException throws if unable to write
	 */
	private static void writePlaneCount(int count, BufferedWriter writer) throws IOException {
		writer.write("\nNumber of questions containing the word airplane or any of its synonyms: " + count + "\n");
	}

	/**
	 * writes everything in the index class
	 * 
	 * @param index the instance of the index class
	 * @param path  the location to write to
	 * @throws IOException throws if unable to write
	 */
	public static void write(Index index, Path path) throws IOException {
		try (BufferedWriter filewriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeIntents(index.getInents(), filewriter);
			writeWords(index.getWords(), filewriter);
			writeTopTen(index.mostFrequent(), filewriter);
			writeBottomTen(index.leastFrequent(), filewriter);
			writeWCount(index.getWCount(), filewriter);
			writePlaneCount(index.getPlaneCount(), filewriter);
		}
	}
}
